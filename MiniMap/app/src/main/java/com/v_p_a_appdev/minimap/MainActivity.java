package com.v_p_a_appdev.minimap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        Button workerButton = findViewById(R.id.worker);
        Button customerButton = findViewById(R.id.customer);
        workerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WorkerLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        });
        customerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        });
    }
}



