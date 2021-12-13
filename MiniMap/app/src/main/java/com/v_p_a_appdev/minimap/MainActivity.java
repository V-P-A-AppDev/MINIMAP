package com.v_p_a_appdev.minimap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MapUtilities.LOCATION_REQUEST_CODE);
        }
        Places.initialize(getApplicationContext(), String.valueOf(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);

        Button workerButton = findViewById(R.id.worker);
        Button customerButton = findViewById(R.id.customer);
        workerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelperLoginActivity.class);
            startActivity(intent);
            finish();
        });
        customerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RequesterLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}




