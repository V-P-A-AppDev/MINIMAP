package com.v_p_a_appdev.minimap;

import android.location.Location;
import android.view.View;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HelperMapActivityM extends UserMapActivityM {
    private DatabaseReference assignedReqLocationRef;
    private ValueEventListener assignedReqLocationRefListener;
    private String requesterId = "";
    private HelperMapActivity helperMapActivity;

    public HelperMapActivityM(HelperMapActivity userMapActivity) {
        super(userMapActivity);
        helperMapActivity = userMapActivity;
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(userId);

    }

    public void cancelJob() {
        userDatabase.child("RequesterJobId").removeValue();
        if (requesterId != null && requesterId != "") {
            DatabaseReference requesterRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Requesters").child(requesterId).child("AssignedHelperIdentification");
            requesterRef.removeValue();
            requesterId = null;
        }
    }


    public void getAssignedRequester() {
        DatabaseReference assignedReqRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(userId).child("RequesterJobId");
        assignedReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    requesterId = Objects.requireNonNull(snapshot.getValue()).toString();
                    getAssignedRequesterLocation();
                    getAssignedRequesterInfo();
                    sendNotificatoin.listenForMessages(helperMapActivity, userId, requesterId + userId);
                } else {
                    requesterId = "";
                    if (helperMapActivity.getJobMarker() != null) {
                        helperMapActivity.getJobMarker().remove();
                    }
                    if (assignedReqLocationRef != null) {
                        assignedReqLocationRef.removeEventListener(assignedReqLocationRefListener);
                    }
                    helperMapActivity.getRequesterInfo().setVisibility(View.GONE);
                    helperMapActivity.getRequesterName().setText("");
                    helperMapActivity.getRequesterPhone().setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void getAssignedRequesterLocation() {
        assignedReqLocationRef = FirebaseDatabase.getInstance().getReference().child("Request").child(requesterId).child("l");
        assignedReqLocationRefListener = assignedReqLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !requesterId.equals("")) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double ReqLat = 0;
                    double ReqLng = 0;
                    if (map.get(0) != null) {
                        ReqLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(0) != null) {
                        ReqLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng ReqLatLng = new LatLng(ReqLat, ReqLng);
                    if (helperMapActivity.getJobMarker() != null) {
                        helperMapActivity.getJobMarker().remove();
                    }
                    helperMapActivity.setJobMarker(ReqLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getAssignedRequesterInfo() {
        helperMapActivity.getRequesterInfo().setVisibility(View.VISIBLE);
        DatabaseReference requesterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Requesters").child(requesterId);
        requesterDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    User Requester = new User();
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        Requester.setUserName(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        Requester.setPhoneNumber(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        Requester.setUserImageUrl(Objects.requireNonNull(map.get("profileImageUrl")).toString());

                    }
                    helperMapActivity.ShowAssignedRequesterInfo(Requester);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void LogOut(){
        disconnectHelper();
        super.LogOut();
    }

    public void openLeaderBoard(){
        helperMapActivity.openLeaderBoard();
    }


    public void disconnectHelper() {
        helperMapActivity.removeLocationUpdates();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(userId).child("RequesterJobId");
        ref.removeValue();
        ref = FirebaseDatabase.getInstance().getReference("HelpersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        ref = FirebaseDatabase.getInstance().getReference("HelpersBusy");
        geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

    public void changeHelperAvailable(Location location) {
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("HelpersAvailable");
        DatabaseReference refBusy = FirebaseDatabase.getInstance().getReference("HelpersBusy");
        GeoFire geoFireAvailable = new GeoFire(refAvailable);
        GeoFire geoFireBusy = new GeoFire(refBusy);

        if ("".equals(requesterId)) {//*Case the helper is available.
            geoFireBusy.removeLocation(userId);
            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
        } else {//*Case the helper is currently busy .
            geoFireAvailable.removeLocation(userId);
            geoFireBusy.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void loadChat() {
        helperMapActivity.loadChat(userId,requesterId);
    }
}
