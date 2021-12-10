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

public class WorkerMapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private ActivityWorkerMapBinding binding;
    GoogleApiClient currentGoogleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    SupportMapFragment mapFragment;
    private Button logoutButton;
    String userId;

    private Marker JobMarker;
    private String customerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWorkerMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //*Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WorkerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);//*
        } else {//*
            mapFragment.getMapAsync(this);
        }//*
        logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(WorkerMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        getAssignedCustomer();
    }

    private void getAssignedCustomer(){
        String workerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Workers").child(workerID).child("CustomerJobId");

        assignedCustRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    customerId = snapshot.getValue().toString();
                    getAssignedCustomerLocation();
                }
                else{
                    customerId = "";
                    if(JobMarker != null){
                        JobMarker.remove();
                    }
                    if(assignedCustLocationRef != null) {
                        assignedCustLocationRef.removeEventListener(assignedCustLocationRefListener);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private DatabaseReference assignedCustLocationRef;
    private ValueEventListener assignedCustLocationRefListener;
    private void getAssignedCustomerLocation(){
        assignedCustLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustLocationRefListener = assignedCustLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double CustLat = 0;
                    double CustLng = 0;
                    if(map.get(0) != null){
                        CustLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(0) != null){
                        CustLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng CustLatLng = new LatLng(CustLat, CustLng);
                    if(JobMarker != null)
                    {
                        JobMarker.remove();
                    }
                    JobMarker = mMap.addMarker(new MarkerOptions().position(CustLatLng).title("Customer location"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WorkerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        buildGooogleApiClient();
        mMap.setMyLocationEnabled(true);
        if (lastLocation == null)
            return;
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    protected synchronized void buildGooogleApiClient() {
        currentGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        currentGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;
        lastLocation = location;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        //*Basically it goes in between 1 to 21 to i've choosen somewhere in the middle.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("WorkersAvailable");
        DatabaseReference refbusy = FirebaseDatabase.getInstance().getReference("WorkersBusy");
        GeoFire geoFireAvailable = new GeoFire(refAvailable);
        GeoFire geoFireBusy = new GeoFire(refbusy);

        switch (customerId){
            case "":
                geoFireBusy.removeLocation(userId);
                geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                break;
            default:
                geoFireAvailable.removeLocation(userId);
                geoFireBusy.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                break;
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
        currentGoogleApiClient.connect();
        LocationServices.FusedLocationApi.requestLocationUpdates(currentGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("WorkersAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(getApplicationContext(), "Access to location is needed!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

}





