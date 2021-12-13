package com.v_p_a_appdev.minimap;


import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

public class WorkerMapActivity extends UserMapActivity {
    private Button logoutButton;
    private boolean isLoggingOut = false;
    private Marker jobMarker;
    private String customerId = "";
    private LinearLayout customerInfo;
    private ImageView customerIcon;
    private TextView customerName, customerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        logoutButton.setOnClickListener(v -> {
            isLoggingOut = true;
            disconnectwWorker();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        customerPhone.setOnClickListener(v -> {
           ShowDialer(customerPhone);
        });
        getAssignedCustomer();
    }

    private void initialize() {
        customerInfo = findViewById(R.id.customerInfo);
        customerIcon = findViewById(R.id.customerIcon);
        customerName = findViewById(R.id.customerName);
        customerPhone = findViewById(R.id.customerPhone);
        logoutButton = findViewById(R.id.logout);
    }

    private void getAssignedCustomer() {
        String workerID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference assignedCustRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Workers").child(workerID).child("CustomerJobId");

        assignedCustRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    customerId = Objects.requireNonNull(snapshot.getValue()).toString();
                    getAssignedCustomerLocation();
                    getAssignedCustomerInfo();
                } else {
                    customerId = "";
                    if (jobMarker != null) {
                        jobMarker.remove();
                    }
                    if (assignedCustLocationRef != null) {
                        assignedCustLocationRef.removeEventListener(assignedCustLocationRefListener);
                    }
                    customerInfo.setVisibility(View.GONE);
                    customerName.setText("");
                    customerPhone.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private DatabaseReference assignedCustLocationRef;
    private ValueEventListener assignedCustLocationRefListener;

    private void getAssignedCustomerLocation() {
        assignedCustLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustLocationRefListener = assignedCustLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !customerId.equals("")) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double CustLat = 0;
                    double CustLng = 0;
                    if (map.get(0) != null) {
                        CustLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(0) != null) {
                        CustLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng custLatLng = new LatLng(CustLat, CustLng);
                    if (jobMarker != null) {
                        jobMarker.remove();
                    }
                    jobMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(custLatLng).title("Customer location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.workermarker)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getAssignedCustomerInfo() {
        customerInfo.setVisibility(View.VISIBLE);
        DatabaseReference customerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        customerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        customerName.setText(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        customerPhone.setText(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    customerIcon.setImageResource(R.mipmap.ic_launcher_foreground);
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
        mapUtils.getmMap().animateCamera(CameraUpdateFactory.zoomTo(11));

        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("WorkersAvailable");
        DatabaseReference refBusy = FirebaseDatabase.getInstance().getReference("WorkersBusy");
        GeoFire geoFireAvailable = new GeoFire(refAvailable);
        GeoFire geoFireBusy = new GeoFire(refBusy);

        if ("".equals(customerId)) {//*Case the worker is available.
            geoFireBusy.removeLocation(userId);
            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
        } else {//*Case the worker is currently busy .
            geoFireAvailable.removeLocation(userId);
            geoFireBusy.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    private void disconnectwWorker() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mapUtils.getCurrentGoogleApiClient(), this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("WorkersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        ref = FirebaseDatabase.getInstance().getReference("WorkersBusy");
        geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isLoggingOut) {
            disconnectwWorker();
        }
    }

    @Override
    protected void loadSetting() {
        Intent intent = new Intent(this, WorkerSettingsActivity.class);
        startActivity(intent);
    }

    public void ShowDialer(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + customerPhone.getText().toString()));
        startActivity(intent);
    }

    @Override
    protected void loadActivity() {

        setContentView(R.layout.activity_worker_map);
    }
}





