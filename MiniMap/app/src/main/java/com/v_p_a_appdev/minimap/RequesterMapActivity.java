package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequesterMapActivity extends UserMapActivity {

    private Button logoutButton, requestButton, openMenuButton, closeMenuButton, chatButton;
    private LatLng requestLocation;
    private boolean isRequesting;
    private Marker helperMarker;
    private Marker requesterMarker;
    private LinearLayout helperInfo, menuPopUp;
    private ImageView helperIcon;
    private TextView helperName, helperPhone;

    private int radius = 1;
    private boolean helperFound = false;
    private String helperFoundId;
    GeoQuery geoQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();


        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        openMenuButton.setOnClickListener(v -> {
            menuPopUp.setVisibility(View.VISIBLE);
            openMenuButton.setVisibility(View.GONE);
        });

        closeMenuButton.setOnClickListener(v -> {
            menuPopUp.setVisibility(View.GONE);
            openMenuButton.setVisibility(View.VISIBLE);
        });

        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("UserType", "helper");
            intent.putExtra("UserId", helperFoundId);
            intent.putExtra("UserName", helperName.getText());
            intent.putExtra("ConnectionId", userId+helperFoundId);
            startActivity(intent);
        });




        //*When a click on the request button is being performed.
        requestButton.setOnClickListener(v -> {
            //*If the request button is already pressed then it means that the requester wants to cancel it .
            if (isRequesting) {
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
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId);

                if (requesterMarker != null) {
                    requesterMarker.remove();
                }
                if (helperMarker != null) {
                    helperMarker.remove();
                }
                requestButton.setText("Ask for help");
                helperInfo.setVisibility(View.GONE);
                helperName.setText("");
                helperPhone.setText("");

            } else {
                isRequesting = true;
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude()));
                requestLocation = new LatLng(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude());
                requesterMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(requestLocation).title("Help Needed Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.logo_t_foreground)));
                requestButton.setText("Cancel.");
                getClosestHelper();
            }
        });

    }

    private void initialize() {
        logoutButton = findViewById(R.id.logout);
        requestButton = findViewById(R.id.request);
        helperInfo = findViewById(R.id.helperInfo);
        helperIcon = findViewById(R.id.helperIcon);
        helperName = findViewById(R.id.helperName);
        helperPhone = findViewById(R.id.helperPhone);
        openMenuButton = findViewById(R.id.openMenu);
        closeMenuButton = findViewById(R.id.closeMenu);
        menuPopUp = findViewById(R.id.requesterMenu);
        chatButton = findViewById(R.id.requesterChatButton);
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
                    requestButton.setText("Looking for someone");
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
        helperInfo.setVisibility(View.VISIBLE);
        DatabaseReference requesterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(helperFoundId);
        requesterDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        helperName.setText(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        helperPhone.setText(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        String profileImageUrl = (Objects.requireNonNull(map.get("profileImageUrl")).toString());
                        Glide.with(getApplication()).load(profileImageUrl).into(helperIcon);
                    } else {
                        helperIcon.setImageResource(R.mipmap.helpermarker);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private DatabaseReference helperLocRef;
    private ValueEventListener helperLocationRefListener;

    private void getHelperLocation() {
        helperLocRef = FirebaseDatabase.getInstance().getReference().child("HelpersBusy").child(helperFoundId).child("l");
        helperLocationRefListener = helperLocRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && isRequesting) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double helperLat = 0;
                    double helperLng = 0;
                    requestButton.setText("Helper Found");
                    if (map.get(0) != null) {
                        helperLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        helperLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng helperLatLng = new LatLng(helperLat, helperLng);
                    if (helperMarker != null) {
                        helperMarker.remove();
                    }
                    Location helperLocation = new Location("");
                    helperLocation.setLatitude(helperLatLng.latitude);
                    helperLocation.setLongitude(helperLatLng.longitude);
                    float distance = helperLocation.distanceTo(userLocation.lastLocation);
                    if (distance < 100) {
                        requestButton.setText("Reinforcements has arrived ;) .");
                    } else {
                        requestButton.setText("Helper found: " + /*String.valueOf*/(distance));
                    }
                    helperMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(helperLatLng).title("Your helper").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpermarker)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mapUtils.getCurrentGoogleApiClient(), this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

    }

    @Override
    protected void loadSetting() {
        Intent intent = new Intent(this, RequesterSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void loadActivity() {
        setContentView(R.layout.activity_requester_map);
    }
}















