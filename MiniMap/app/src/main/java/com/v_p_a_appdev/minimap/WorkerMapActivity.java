package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.location.LocationManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.v_p_a_appdev.minimap.databinding.ActivityWorkerMapBinding;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WorkerMapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private ActivityWorkerMapBinding binding;
    private MapUtilities maputils = new MapUtilities();


    Location lastLocation;
    LocationRequest locationRequest;
    private Button logoutButton, settingButton;
    String userId;
    private boolean isLoggingOut = false;
    private Marker jobMarker;
    private String customerId = "";
    private LinearLayout customerInfo;
    private ImageView customerIcon;
    private TextView customerName, customerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWorkerMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //*Obtain the SupportMapFragment and get notified when the map is ready to be used.
        maputils.setMapFragment((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WorkerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);//*
        } else {//*
            maputils.getMapFragment().getMapAsync(this);
        }//*

        logoutButton = findViewById(R.id.logout);
        settingButton = findViewById(R.id.settings);
        customerInfo = findViewById(R.id.customerInfo);
        customerIcon = findViewById(R.id.customerIcon);
        customerName = findViewById(R.id.customerName);
        customerPhone = findViewById(R.id.customerPhone);

        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerMapActivity.this, WorkerSettingsActivity.class);
            startActivity(intent);
            return;
        });
        logoutButton.setOnClickListener(v -> {
            isLoggingOut = true;
            disconnectwWorker();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(WorkerMapActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        getAssignedCustomer();
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
                    jobMarker = maputils.getmMap().addMarker(new MarkerOptions().position(custLatLng).title("Customer location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.workermarker)));
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        maputils.setmMap(googleMap);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WorkerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        buildGoogleApiClient();
        maputils.getmMap().setMyLocationEnabled(true);
        if (lastLocation == null)
            return;
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        maputils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    protected synchronized void buildGoogleApiClient() {
        maputils.setCurrentGoogleApiClient(new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build());
        maputils.getCurrentGoogleApiClient().connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lastLocation = location;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        maputils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latlng));
        //*Basically it goes in between 1 to 21 to i've chosen somewhere in the middle.
        maputils.getmMap().animateCamera(CameraUpdateFactory.zoomTo(11));

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
    public void onConnected(@Nullable Bundle bundle) {
//        locationRequest = com.google.android.gms.location.LocationRequest.create();//*new LocationRequest()
        locationRequest = new LocationRequest();
        //*Set an interval for 1 second.
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WorkerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        maputils.getCurrentGoogleApiClient().connect();
        LocationServices.FusedLocationApi.requestLocationUpdates(maputils.getCurrentGoogleApiClient(), locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void disconnectwWorker() {
        LocationServices.FusedLocationApi.removeLocationUpdates(maputils.getCurrentGoogleApiClient(), this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("WorkersAvailable");
        GeoFire geoFire = new GeoFire(ref);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                maputils.getMapFragment().getMapAsync(this);
            } else {
                Toast.makeText(getApplicationContext(), "Access to location is needed!", Toast.LENGTH_LONG).show();
            }
        }
    }


}





