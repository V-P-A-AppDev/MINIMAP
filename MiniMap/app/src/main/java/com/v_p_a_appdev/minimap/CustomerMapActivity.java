package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.v_p_a_appdev.minimap.databinding.ActivityCustomerMapBinding;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class CustomerMapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private ActivityCustomerMapBinding binding;
    GoogleApiClient currentGoogleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    SupportMapFragment mapFragment;
    String userId;

    private Button logoutButton, requestButton;
    private LatLng requestLocation;
    private boolean isRequesting;
    private Marker workerMarker;
    private Marker custMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //*Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);//*
        } else {//*
            mapFragment.getMapAsync(this);
        }
        logoutButton = (Button) findViewById(R.id.logout);
        requestButton = (Button) findViewById(R.id.request);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            mapFragment.getMapAsync(this);
        }
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequesting) {
                    isRequesting = false;
                    geoQuery.removeAllListeners();
                    workerLocRef.removeEventListener(workerLocationRefListener);

                    if (workerFoundId != null) {
                        DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference("Users").child("Workers").child(workerFoundId);
                        workerRef.setValue(true);
                        workerFoundId = null;
                    }
                    workerFound = false;
                    radius = 1;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);

                    if (custMarker != null) {
                        custMarker.remove();
                    }
                    if (workerMarker != null) {
                        workerMarker.remove();
                    }
                    requestButton.setText("Ask for help");


                } else {
                    isRequesting = true;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    requestLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    custMarker = mMap.addMarker(new MarkerOptions().position(requestLocation).title("Help Needed Here")/*.icon(BitmapDescriptorFactory.fromResource(R.mipmap.customermarker))*/);
                    requestButton.setText("Searching for someone in the area.");
                    getClosestWorker();
                }
            }
        });
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private int radius = 1;
    private boolean workerFound = false;
    private String workerFoundId;
    GeoQuery geoQuery;

    private void getClosestWorker() {
        DatabaseReference workerLocation = FirebaseDatabase.getInstance().getReference().child("WorkersAvailable");
        GeoFire geoFire = new GeoFire(workerLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(requestLocation.latitude, requestLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            //*After the first worker was found , even if there's more in the area , he would be the choice.
            public void onKeyEntered(String key, GeoLocation location) {
                if (!workerFound && isRequesting) {
                    workerFound = true;
                    workerFoundId = key;
                    DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference("Users").child("Workers").child(workerFoundId);
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap hmap = new HashMap();
                    hmap.put("CustomerJobId", customerId);
                    workerRef.updateChildren(hmap);
                    getWorkerLocation();
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
                if (!workerFound) {
                    radius++;
                    getClosestWorker();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private DatabaseReference workerLocRef;
    private ValueEventListener workerLocationRefListener;

    private void getWorkerLocation() {
        workerLocRef = FirebaseDatabase.getInstance().getReference().child("WorkersBusy").child(workerFoundId).child("l");
        workerLocationRefListener = workerLocRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && isRequesting) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double workerLat = 0;
                    double workerLng = 0;
                    requestButton.setText("Worker Found");
                    if (map.get(0) != null) {
                        workerLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        workerLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng workerLatLng = new LatLng(workerLat, workerLng);
                    if (workerMarker != null) {
                        workerMarker.remove();
                    }
                    Location workerLocation = new Location("");
                    workerLocation.setLatitude(workerLatLng.latitude);
                    workerLocation.setLongitude(workerLatLng.longitude);
                    float distance = workerLocation.distanceTo(lastLocation);

                    if (distance < 100) {
                        requestButton.setText("Worker is here");
                    } else {
                        requestButton.setText("Worker found: " + String.valueOf(distance));
                    }
                    workerMarker = mMap.addMarker(new MarkerOptions().position(workerLatLng).title("Your worker").icon(BitmapDescriptorFactory.fromResource(R.mipmap.workermarker)));
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
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);//*
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
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        //*Set an interval for 1 second.
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);//*
        }
        while (!currentGoogleApiClient.isConnected()) {
            buildGooogleApiClient();
            System.out.println(currentGoogleApiClient.isConnected());
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(currentGoogleApiClient, locationRequest, this);
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

    }
}












