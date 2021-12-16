package com.v_p_a_appdev.minimap;

import android.location.Location;
import android.view.View;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequesterMapActivityMap extends UserMapActivityMap{
    private RequesterMapActivity requesterMapActivity;
    private int radius = 1;
    private boolean helperFound = false;
    private String helperFoundId;
    GeoQuery geoQuery;
    private LatLng requestLocation;
    private boolean isRequesting;
    private DatabaseReference helperLocRef;
    private ValueEventListener helperLocationRefListener;


    public RequesterMapActivityMap(RequesterMapActivity requesterMapActivity) {
        super(requesterMapActivity);
        this.requesterMapActivity = requesterMapActivity;
    }
    public void Request(){
        if (isRequesting) {
            createRequest();
        }
        else {
            cancelRequest();
        }
    }

    private void createRequest(){
        isRequesting = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userId, new GeoLocation(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude()));
        requestLocation = new LatLng(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude());
        requesterMapActivity.setRequesterMarker(mapUtils.getmMap().addMarker(new MarkerOptions().position(requestLocation).title("Help Needed Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.logo_t_foreground))));
        requesterMapActivity.changeRequestButtonText("Cancel.");
        getClosestHelper();
    }

    private void cancelRequest(){
        isRequesting = false;
        geoQuery.removeAllListeners();
        if (helperFound) {
            helperLocRef.removeEventListener(helperLocationRefListener);
            if (helperFoundId != null) {
                DatabaseReference helperRef = FirebaseDatabase.getInstance().getReference("Users").child("Helpers").child(helperFoundId).child("RequesterJobId");
                helperRef.removeValue();
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
                    String requesterId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    HashMap hmap = new HashMap();
                    hmap.put("RequesterJobId", requesterId);
                    helperRef.updateChildren(hmap);
                    getHelperLocation();
                    getHelperInfo();
                    requesterMapActivity.changeRequestButtonText("Looking for someone");
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

    private void getHelperInfo() {
        requesterMapActivity.getHelperInfo().setVisibility(View.VISIBLE);
        DatabaseReference requesterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(helperFoundId);
        requesterDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        requesterMapActivity.getHelperName().setText(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        requesterMapActivity.getHelperPhone().setText(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    requesterMapActivity.getHelperIcon().setImageResource(R.mipmap.helpermarker);
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
                    float distance = helperLocation.distanceTo(userLocation.lastLocation);
                    if (distance < 100) {
                        requesterMapActivity.changeRequestButtonText("Reinforcements has arrived ;) .");
                    } else {
                        requesterMapActivity.changeRequestButtonText("Helper found: " + /*String.valueOf*/(distance));
                    }
                    requesterMapActivity.setHelperMarker(mapUtils.getmMap().addMarker(new MarkerOptions().position(helperLatLng).title("Your helper").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpermarker))));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void openMenu() {
        requesterMapActivity.openMenu();
    }

    @Override
    public void closeMenu() {
        requesterMapActivity.closeMenu();
    }

    @Override
    public void LogOut() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mapUtils.getCurrentGoogleApiClient(), this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        super.LogOut();
    }
}
