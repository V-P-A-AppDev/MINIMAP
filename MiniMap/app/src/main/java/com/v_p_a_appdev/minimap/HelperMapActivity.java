package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class HelperMapActivity extends UserMapActivity {
    private Button logoutButton, openMenuButton, closeMenuButton, chatButton;
    private boolean isLoggingOut = false;
    private Marker jobMarker;
    private String requesterId = "";
    private ConstraintLayout requesterInfo, menuPopUp;
    private ImageView requesterIcon;
    private TextView requesterName, requesterPhone;
    private String requesterImageUrl;
    private ImageView userImage;
    private TextView userName, userPhone, userRating;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    private DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(userId);
    User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();


        getUserInfo();
        logoutButton.setOnClickListener(v -> {
            isLoggingOut = true;
            disconnectwHelper();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        requesterPhone.setOnClickListener(v -> ShowDialer(requesterPhone));
        getAssignedRequester();
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
            intent.putExtra("UserType", "Requester");
            intent.putExtra("UserId", requesterId);
            intent.putExtra("UserName", requesterName.getText());
            intent.putExtra("ConnectionId", requesterId + userId);
            intent.putExtra("imageView", requesterImageUrl);
            startActivity(intent);
        });


    }

    private void initialize() {
        requesterInfo = findViewById(R.id.requesterInfo);
        requesterIcon = findViewById(R.id.requesterIcon);
        requesterName = findViewById(R.id.requesterName);
        requesterPhone = findViewById(R.id.requesterPhone);
        logoutButton = findViewById(R.id.logout);
        openMenuButton = findViewById(R.id.openMenu);
        closeMenuButton = findViewById(R.id.closeMenu);
        menuPopUp = findViewById(R.id.helperMenu);
        chatButton = findViewById(R.id.helperChatButton);
        userImage = findViewById(R.id.curProfileImage);
        userName = findViewById(R.id.curName);
        userPhone = findViewById(R.id.curPhoneNum);
        userRating = findViewById(R.id.curRating);

    }

    private void getUserInfo() {
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (Objects.requireNonNull(map).get("name") != null) {
                        currentUser.setUserName("Name:\n"+Objects.requireNonNull(map.get("name")).toString());
                        userName.setText(currentUser.getUserName());
                    }
                    if (map.get("phone") != null) {
                        currentUser.setPhoneNumber("Phone Number :\n"+Objects.requireNonNull(map.get("phone")).toString());
                        userPhone.setText(currentUser.getPhoneNumber());
                    }
                    if (map.get("profileImageUrl") != null) {
                        String userImageUrl = (Objects.requireNonNull(map.get("profileImageUrl")).toString());
                        Glide.with(getApplication()).load(userImageUrl).into(userImage);
                    }
                    if (map.get("rating") != null) {
                        userRating.setText("Rating:\n"+Objects.requireNonNull(map.get("rating")).toString());
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

    private void getAssignedRequester() {
        String helperID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference assignedReqRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Helpers").child(helperID).child("RequesterJobId");
        assignedReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    requesterId = Objects.requireNonNull(snapshot.getValue()).toString();
                    getAssignedRequesterLocation();
                    getAssignedRequesterInfo();
                } else {
                    requesterId = "";
                    if (jobMarker != null) {
                        jobMarker.remove();
                    }
                    if (assignedReqLocationRef != null) {
                        assignedReqLocationRef.removeEventListener(assignedReqLocationRefListener);
                    }
                    requesterInfo.setVisibility(View.GONE);
                    requesterName.setText("");
                    requesterPhone.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private DatabaseReference assignedReqLocationRef;
    private ValueEventListener assignedReqLocationRefListener;

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
                    if (jobMarker != null) {
                        jobMarker.remove();
                    }
                    jobMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(ReqLatLng).title("requester location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customermarker)));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getAssignedRequesterInfo() {
        requesterInfo.setVisibility(View.VISIBLE);
        DatabaseReference requesterDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Requesters").child(requesterId);
        requesterDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        requesterName.setText(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        requesterPhone.setText(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        requesterImageUrl = (Objects.requireNonNull(map.get("profileImageUrl")).toString());
                        Glide.with(getApplication()).load(requesterImageUrl).into(requesterIcon);
                    } else {
                        requesterIcon.setImageResource(R.mipmap.ic_launcher_foreground);
                        requesterImageUrl = "";
                    }
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
        userLocation.lastLocation = location;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        mapUtils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latlng));
        //*Basically it goes in between 1 to 21 to i've chosen somewhere in the middle.
        mapUtils.getmMap().animateCamera(CameraUpdateFactory.zoomTo(14));

        String userId = FirebaseAuth.getInstance().getUid();
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
    public void onConnectionSuspended(int i) {

    }


    private void disconnectwHelper() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mapUtils.getCurrentGoogleApiClient(), this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("HelpersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        ref = FirebaseDatabase.getInstance().getReference("HelpersBusy");
        geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

    }


    @Override
    protected void onDestroy() {
        if (!isLoggingOut) {
            disconnectwHelper();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void loadSetting() {
        Intent intent = new Intent(this, HelperSettingsActivity.class);
        startActivity(intent);
    }

    public void ShowDialer(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + requesterPhone.getText().toString()));
        startActivity(intent);
    }

    @Override
    protected void loadActivity() {

        setContentView(R.layout.activity_helper_map);
    }
}


