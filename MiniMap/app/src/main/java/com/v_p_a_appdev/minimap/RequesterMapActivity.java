package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;

import android.location.Location;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RequesterMapActivity extends UserMapActivity {

    private Button logoutButton, requestButton, openMenuButton, closeMenuButton, chatButton;
    private LatLng requestLocation;
    private boolean isRequesting;
    private Marker helperMarker;
    private Marker requesterMarker;
    private ConstraintLayout helperInfo, menuPopUp;
    private ImageView helperIcon;
    private TextView helperName, helperPhone;


    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    private DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Requesters").child(userId);


    User currentUser = new User();
    private ImageView userImage;
    private TextView userName, userPhone, userRating;
    private int radius = 1;
    private boolean helperFound = false;
    private String helperFoundId;
    GeoQuery geoQuery;
    private String helperImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();


        getUserInfo();
        logoutButton.setOnClickListener(v -> {
            removeListenersAndUnnecessaryData();
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
            intent.putExtra("ConnectionId", userId + helperFoundId);
            intent.putExtra("imageView", helperImageUrl);
            startActivity(intent);
        });

        //*When a click on the request button is being performed.
        requestButton.setOnClickListener(v -> {
            //*If the request button is already pressed then it means that the requester wants to cancel it .
            if (isRequesting) {
                cancelhelper(1);
            } else {
                isRequesting = true;
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude()));
                requestLocation = new LatLng(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude());
                //*requesterMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(requestLocation).title("Help Needed Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.logo_t_foreground)));
                requestButton.setText("Cancel.");
                getClosestHelper();
                sendNotificatoin.showNotification(this, "Help on the way", "Helper found",1);
            }
        });
    }

    private void getUserInfo() {
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (Objects.requireNonNull(map).get("name") != null) {
                        currentUser.setUserName("Name:\n" + Objects.requireNonNull(map.get("name")).toString());
                        userName.setText(currentUser.getUserName());
                    }
                    if (map.get("phone") != null) {
                        currentUser.setPhoneNumber("Phone Number :\n" + Objects.requireNonNull(map.get("phone")).toString());
                        userPhone.setText(currentUser.getPhoneNumber());
                    }
                    if (map.get("profileImageUrl") != null) {
                        String userImageUrl = (Objects.requireNonNull(map.get("profileImageUrl")).toString());
                        Glide.with(getApplication()).load(userImageUrl).into(userImage);
                    }
                    if (map.get("rating") != null) {
                        userRating.setText("Rating:\n" + Objects.requireNonNull(map.get("rating")).toString());
                    } else {
                        userRating.setText("Rating:\n 0");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
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
        userImage = findViewById(R.id.curProfileImage);
        userName = findViewById(R.id.curName);
        userPhone = findViewById(R.id.curPhoneNum);
        userRating = findViewById(R.id.curRating);
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
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Requesters").child(requesterId);
                    HashMap hmap1 = new HashMap();
                    hmap1.put("AssignedHelperIdentification", helperFoundId);
                    ref.updateChildren(hmap1);
                    requestButton.setText("Cancel");
                    getHelperLocation();
                    getHelperInfo();
                    listenForcancelation();
                    sendNotificatoin.listenForMessages(RequesterMapActivity.this, userId,userId + helperFoundId);
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

    private void cancelhelper(int rating){
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
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
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
        helperIcon.setImageResource(R.mipmap.ic_launcher_foreground);
    }



    private void listenForcancelation(){
        DatabaseReference ref = userDatabase.child("AssignedHelperIdentification");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    cancelhelper(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        helperImageUrl = (Objects.requireNonNull(map.get("profileImageUrl")).toString());
                        Glide.with(getApplication()).load(helperImageUrl).into(helperIcon);
                    } else {
                        helperIcon.setImageResource(R.mipmap.ic_launcher_foreground);
                        helperImageUrl = "";
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
                        requestButton.setText("Finish.");
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
    protected void onDestroy() {
        removeListenersAndUnnecessaryData();
        super.onDestroy();
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















