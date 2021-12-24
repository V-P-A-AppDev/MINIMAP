package com.v_p_a_appdev.minimap;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HelperMapActivityMap extends UserMapActivityMap {
    private DatabaseReference assignedReqLocationRef;
    private ValueEventListener assignedReqLocationRefListener;
    private String requesterId = "";
    private HelperMapActivity helperMapActivity;


    public HelperMapActivityMap(HelperMapActivity userMapActivity) {
        super(userMapActivity);
//        NotificationChannel channel= new NotificationChannel("My Notification","My Notification",NotificationManager.IMPORTANCE_DEFAULT);
//        NotificationManager manager =helperMapActivity.getSystemService(NotificationManager.class);
//        manager.createNotificationChannel(channel);
        helperMapActivity = userMapActivity;
    }

    public void getAssignedRequester() {
        DatabaseReference assignedReqRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(userId).child("RequesterJobId");
        assignedReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    requesterId = Objects.requireNonNull(snapshot.getValue()).toString();
//                    String message="A request was found!";
//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(helperMapActivity,"My Notification");
//                    builder.setContentTitle("REQUEST");
//                    builder.setContentText(message);
//                    builder.setSmallIcon(R.mipmap.ic_launcher_foreground);
//                    builder.setAutoCancel(true);
//                    NotificationManagerCompat managerCompat=NotificationManagerCompat.from(helperMapActivity);
//                    managerCompat.notify(1,builder.build());
                    getAssignedRequesterLocation();
                    getAssignedRequesterInfo();
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
                changeAvailable();
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
                    helperMapActivity.setJobMarker( mapUtils.getmMap().addMarker(new MarkerOptions().position(ReqLatLng).title("requester location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpermarker))));
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
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        helperMapActivity.getRequesterName().setText(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        helperMapActivity.getRequesterPhone().setText(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    helperMapActivity.getRequesterIcon().setImageResource(R.mipmap.ic_launcher_foreground);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (isLoggingOut) {
            return;
        }
        super.onLocationChanged(location);
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    public void LogOut(){
        disconnectHelper();
        super.LogOut();
    }

    public void openLeaderBoard(){
        helperMapActivity.openLeaderBoard();
    }

    public void stop() {
        disconnectHelper();
    }

    private void disconnectHelper() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mapUtils.getCurrentGoogleApiClient(), this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(userId).child("RequesterJobId");
        ref.removeValue();
        ref = FirebaseDatabase.getInstance().getReference("HelpersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        ref = FirebaseDatabase.getInstance().getReference("HelpersBusy");
        geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

    private void changeAvailable(){
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("HelpersAvailable");
        DatabaseReference refBusy = FirebaseDatabase.getInstance().getReference("HelpersBusy");
        GeoFire geoFireAvailable = new GeoFire(refAvailable);
        GeoFire geoFireBusy = new GeoFire(refBusy);
        if ("".equals(requesterId)) {//*Case the helper is available.
            geoFireBusy.removeLocation(userId);
            geoFireAvailable.setLocation(userId, new GeoLocation(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude()));
        } else {//*Case the helper is currently busy .
            geoFireAvailable.removeLocation(userId);
            geoFireBusy.setLocation(userId, new GeoLocation(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude()));
        }

    }
}
