package com.v_p_a_appdev.minimap;

import android.location.Location;
import android.view.View;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequesterMapActivityM extends UserMapActivityM {
    private RequesterMapActivity requesterMapActivity;
    private int radius = 1;
    private boolean helperFound = false;
    private String helperFoundId;
    GeoQuery geoQuery;
    private LatLng requestLocation;
    private boolean isRequesting;
    private DatabaseReference helperLocRef;
    private ValueEventListener helperLocationRefListener;


    public RequesterMapActivityM(RequesterMapActivity requesterMapActivity) {
        super(requesterMapActivity);
        this.requesterMapActivity = requesterMapActivity;
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Requesters").child(userId);

    }

    public void Request(){
        if (isRequesting) {
            cancelRequest();
        }
        else {
            createRequest();
        }
    }

    private void createRequest(){
        isRequesting = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userId, new GeoLocation(requesterMapActivity.getLastLocation().getLatitude(), requesterMapActivity.getLastLocation().getLongitude()));
        requestLocation = new LatLng(requesterMapActivity.getLastLocation().getLatitude(), requesterMapActivity.getLastLocation().getLongitude());
        requesterMapActivity.changeRequestButtonText("Cancel.");
        getClosestHelper();
    }

    private void cancelRequest(){
        isRequesting = false;
        geoQuery.removeAllListeners();
        if (helperFound) {
            helperLocRef.removeEventListener(helperLocationRefListener);
            if (helperFoundId != null) {
                DatabaseReference helperRef = FirebaseDatabase.getInstance().getReference("Users").child("Helpers").child(helperFoundId);
                helperRef.child("RequesterJobId").removeValue();
                helperRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long rating;
                        try {
                            rating = (Long) snapshot.child("rating").getValue();
                            rating += 1;
                            helperRef.child("rating").setValue(rating);
                        }catch (Exception ignored){}
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                helperFoundId = null;
            }
            helperFound = false;
        }
        radius = 1;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (requesterMapActivity.getRequesterMarker() != null) {
            requesterMapActivity.getRequesterMarker().remove();
        }
        if (requesterMapActivity.getHelperMarker() != null) {
            requesterMapActivity.getHelperMarker().remove();
        }
        requesterMapActivity.changeRequestButtonText("Ask for help");
        requesterMapActivity.getHelperInfo().setVisibility(View.GONE);
        requesterMapActivity.getHelperName().setText("");
        requesterMapActivity.getHelperPhone().setText("");
    }

    private void getClosestHelper() {
        DatabaseReference helperLocation = FirebaseDatabase.getInstance().getReference().child("HelpersAvailable");
        GeoFire geoFire = new GeoFire(helperLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(requestLocation.latitude, requestLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            //*After the first helper was found , even if there's more in the area , he would be the choice.
            public void onKeyEntered(String key, GeoLocation location) {
                if (!helperFound && isRequesting) {
                    helperFound = true;
                    helperFoundId = key;
                    DatabaseReference helperRef = FirebaseDatabase.getInstance().getReference("Users").child("Helpers").child(helperFoundId);
                    HashMap hashMap = new HashMap();
                    hashMap.put("RequesterJobId", userId);
                    helperRef.updateChildren(hashMap);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Requesters").child(userId);
                    HashMap hmap1 = new HashMap();
                    hmap1.put("AssignedHelperIdentification", helperFoundId);
                    ref.updateChildren(hmap1);
                    getHelperLocation();
                    getHelperInfo();
                    listenForCancelation();
                    requesterMapActivity.changeRequestButtonText("found someone");
                    sendNotificatoin.listenForMessages(requesterMapActivity , userId , userId + helperFoundId);
                }
            }

            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
                if (!helperFound) {
                    radius++;
                    getClosestHelper();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    private void cancelHelper(int rating){
        isRequesting = false;
        geoQuery.removeAllListeners();
        if (helperFound) {
            helperLocRef.removeEventListener(helperLocationRefListener);
            if (helperFoundId != null) {
                // Delete chat
                DatabaseReference helperRef = FirebaseDatabase.getInstance().getReference("Chat").child(userId + helperFoundId);
                helperRef.removeValue();
                // Increase helper rating by 1
                helperRef = FirebaseDatabase.getInstance().getReference("Users").child("Helpers").child(helperFoundId).child("rating");
                helperRef.setValue(ServerValue.increment(rating));
                // Remove job
                helperRef = FirebaseDatabase.getInstance().getReference("Users").child("Helpers").child(helperFoundId).child("RequesterJobId");
                helperRef.removeValue();
                helperRef = FirebaseDatabase.getInstance().getReference("Users").child("Requesters").child(userId).child("AssignedHelperIdentification");
                helperRef.removeValue();
                helperFoundId = null;
            }
            helperFound = false;
        }
        radius = 1;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        requesterMapActivity.RemoveHelper();
    }



    private void listenForCancelation(){
        DatabaseReference ref = userDatabase.child("AssignedHelperIdentification");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    cancelHelper(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getHelperInfo() {
        DatabaseReference requesterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(helperFoundId);
        requesterDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    User Helper = new User();
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        Helper.setUserName(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        Helper.setPhoneNumber(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        Helper.setUserImageUrl(Objects.requireNonNull(map.get("profileImageUrl")).toString());
                    }
                    requesterMapActivity.ShowAssignedHelperInfo(Helper);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getHelperLocation() {
        helperLocRef = FirebaseDatabase.getInstance().getReference().child("HelpersBusy").child(helperFoundId).child("l");
        helperLocationRefListener = helperLocRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && isRequesting) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double helperLat = 0;
                    double helperLng = 0;
                    requesterMapActivity.changeRequestButtonText("Helper Found");
                    if (map.get(0) != null) {
                        helperLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        helperLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng helperLatLng = new LatLng(helperLat, helperLng);
                    if (requesterMapActivity.getHelperMarker() != null) {
                        requesterMapActivity.getHelperMarker().remove();
                    }
                    Location helperLocation = new Location("");
                    helperLocation.setLatitude(helperLatLng.latitude);
                    helperLocation.setLongitude(helperLatLng.longitude);
                    float distance = helperLocation.distanceTo(requesterMapActivity.getLastLocation());
                    if (distance < 100) {
                        requesterMapActivity.changeRequestButtonText("Reinforcements has arrived ;) .");
                    } else {
                        requesterMapActivity.changeRequestButtonText("Helper found: " + /*String.valueOf*/(distance));
                    }
                    requesterMapActivity.setHelperMarker(helperLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void removeListenersAndUnnecessaryData(){
        if (helperFoundId != null) {
            helperLocRef.removeEventListener(helperLocationRefListener);
            DatabaseReference helperRef = FirebaseDatabase.getInstance().getReference("Chat").child(userId + helperFoundId);
            helperRef.removeValue();
            helperRef = FirebaseDatabase.getInstance().getReference("Users").child("Helpers").child(helperFoundId).child("RequesterJobId");
            helperRef.removeValue();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Requesters").child(userId).child("AssignedHelperIdentification");
            ref.removeValue();
            helperFoundId = null;
        }
    }

    @Override
    public void LogOut() {
        removeListenersAndUnnecessaryData();
        super.LogOut();
    }

    @Override
    public void loadChat() {
        requesterMapActivity.loadChat(userId , helperFoundId);
    }

    public void disconnectRequester(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Requesters").child(userId).child("AssignedHelperIdentification");
        ref.removeValue();
        ref = FirebaseDatabase.getInstance().getReference("Request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }
}
