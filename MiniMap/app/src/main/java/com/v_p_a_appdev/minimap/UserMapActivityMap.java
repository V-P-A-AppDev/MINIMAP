package com.v_p_a_appdev.minimap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public abstract class UserMapActivityMap implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener  {
    MapUtilities mapUtils ;
    UserLocation userLocation;
    protected boolean isLoggingOut = false;
    private UserMapActivity userMapActivity;
    protected String userId;

    public UserMapActivityMap(UserMapActivity userMapActivity) {
        this.userMapActivity = userMapActivity;
        userLocation = new UserLocation();
        mapUtils = new MapUtilities();
        mapUtils.setMapFragment((SupportMapFragment) userMapActivity.getSupportFragmentManager().findFragmentById(R.id.map));
        if (ActivityCompat.checkSelfPermission(userMapActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(userMapActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(userMapActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapUtilities.LOCATION_REQUEST_CODE);//*
        } else {
            mapUtils.getMapFragment().getMapAsync(this);
        }
        LocationManager locationManager = (LocationManager) userMapActivity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        userLocation.lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapUtils.setmMap(googleMap);
        if (ActivityCompat.checkSelfPermission(userMapActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(userMapActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(userMapActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapUtilities.LOCATION_REQUEST_CODE);
        }
        buildGoogleApiClient();
        mapUtils.getmMap().setMyLocationEnabled(true);
        if (userLocation.lastLocation == null)
            return;
        LatLng latLng = new LatLng(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude());
        mapUtils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    protected synchronized void buildGoogleApiClient() {
        mapUtils.setCurrentGoogleApiClient(new GoogleApiClient.Builder(userMapActivity).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build());
        mapUtils.getCurrentGoogleApiClient().connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLocation.lastLocation = location;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        mapUtils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latlng));
        //*Basically it goes in between 1 to 21 to i've chosen somewhere in the middle.
        mapUtils.getmMap().animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        userLocation.CreateLocationRequest();
        if (!CheckConnected()) {
            Toast.makeText(userMapActivity.getApplicationContext(), "Can't connect to Google. Try again Later.", Toast.LENGTH_LONG).show();
        }
        if (ActivityCompat.checkSelfPermission(userMapActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(userMapActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(userMapActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapUtilities.LOCATION_REQUEST_CODE);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        userMapActivity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MapUtilities.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapUtils.getMapFragment().getMapAsync(this);
            } else {
                Toast.makeText(userMapActivity.getApplicationContext(), "Access to location is needed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void loadSetting(){
        userMapActivity.loadSetting();
    }

    public void LogOut(){
        if (!isLoggingOut) {
            isLoggingOut = true;
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(userMapActivity, MainActivity.class);
            userMapActivity.startActivity(intent);
            userMapActivity.finish();
        }
    }
    public void openMenu(){
        userMapActivity.openMenu();
    }

    public void closeMenu(){
        userMapActivity.closeMenu();
    }
}
