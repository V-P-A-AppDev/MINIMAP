package com.v_p_a_appdev.minimap.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.v_p_a_appdev.minimap.Activities.MainActivity;
import com.v_p_a_appdev.minimap.R;
import com.v_p_a_appdev.minimap.Utils.MapUtilities;
import com.v_p_a_appdev.minimap.Utils.User;
import com.v_p_a_appdev.minimap.Utils.UserLocation;


public abstract class UserMapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    protected MapUtilities mapUtils = new MapUtilities();
    protected ImageView userImage;
    protected TextView userName, userPhone;
    public UserLocation userLocation;
    protected boolean inSubScreen;
    protected int zoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLocation = new UserLocation();
        zoom = 15;
        loadActivity();
        initialize();
        inSubScreen = false;

        //*Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapUtils.setMapFragment((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapUtilities.LOCATION_REQUEST_CODE);//*
        } else {//*
            mapUtils.getMapFragment().getMapAsync(this);
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        userLocation.lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void initialize() {
        userImage = findViewById(R.id.curProfileImage);
        userName = findViewById(R.id.curName);
        userPhone = findViewById(R.id.curPhoneNum);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapUtils.setmMap(googleMap);
        System.out.println("Map Ready");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapUtilities.LOCATION_REQUEST_CODE);
        }
        buildGoogleApiClient();
        mapUtils.getmMap().setMyLocationEnabled(true);
        if (userLocation.lastLocation == null)
            return;
        LatLng latLng = new LatLng(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude());
        mapUtils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    protected synchronized void buildGoogleApiClient() {
        mapUtils.setCurrentGoogleApiClient(new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build());
        mapUtils.getCurrentGoogleApiClient().connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (mapUtils.getmMap() == null)
            return;
        userLocation.lastLocation = location;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        mapUtils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latlng));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        userLocation.CreateLocationRequest();
        if (!CheckConnected()) {
            Toast.makeText(getApplicationContext(), "Can't connect to Google. Try again Later.", Toast.LENGTH_LONG).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapUtilities.LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mapUtils.getCurrentGoogleApiClient(), userLocation.locationRequest, this);
    }

    private boolean CheckConnected() {
        int count = 0;
        while (!mapUtils.getCurrentGoogleApiClient().isConnected() && count < 3) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            buildGoogleApiClient();
            ++count;
        }
        return mapUtils.getCurrentGoogleApiClient().isConnected();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MapUtilities.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapUtils.getMapFragment().getMapAsync(this);
            } else {
                Toast.makeText(getApplicationContext(), "Access to location is needed!", Toast.LENGTH_LONG).show();
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
    }

    public abstract void loadSetting();

    protected abstract void loadActivity();

    public void ChangeScreenToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ChangeUserInfo(User currentUser) {
        userName.setText(currentUser.getUserName());
        userPhone.setText(currentUser.getPhoneNumber());
        Glide.with(getApplication()).load(currentUser.getUserImageUrl()).into(userImage);
    }

    public void removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mapUtils.getCurrentGoogleApiClient(), this);
    }

    public Location getLastLocation() {
        if (userLocation.lastLocation == null) {
            userLocation.lastLocation = new Location(LocationManager.GPS_PROVIDER);
        }
        return userLocation.lastLocation;

    }

    public abstract void loadChat(String userId, String otherId);

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChangeScreenToMain();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inSubScreen = false;
    }
}
