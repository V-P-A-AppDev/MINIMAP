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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import com.google.android.gms.maps.model.MarkerOptions;
import com.v_p_a_appdev.minimap.databinding.ActivityCustomerMapBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomerMapActivity extends UserMapActivity{

    private Button logoutButton, requestButton;
    private LatLng requestLocation;
    private boolean isRequesting;
    private Marker workerMarker;
    private Marker customerMarker;
    private LinearLayout workerInfo;
    private ImageView workerIcon;
    private TextView workerName, workerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        //*When a click on the request button is being performed.
        requestButton.setOnClickListener(v -> {
            //*If the request button is already pressed then it means that the customer wants to cancel it .
            if (isRequesting) {
                isRequesting = false;
                geoQuery.removeAllListeners();
                workerLocRef.removeEventListener(workerLocationRefListener);
                if (workerFoundId != null) {
                    DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference("Users").child("Workers").child(workerFoundId).child("CustomerJobId");
                    workerRef.removeValue();
                    workerFoundId = null;
                }
                workerFound = false;
                radius = 1;
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId);

                if (customerMarker != null) {
                    customerMarker.remove();
                }
                if (workerMarker != null) {
                    workerMarker.remove();
                }
                requestButton.setText("Ask for help");
                workerInfo.setVisibility(View.GONE);
                workerName.setText("");
                workerPhone.setText("");

            } else {
                isRequesting = true;
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude()));
                requestLocation = new LatLng(userLocation.lastLocation.getLatitude(), userLocation.lastLocation.getLongitude());
                customerMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(requestLocation).title("Help Needed Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_foreground)));
                requestButton.setText("Cancel.");
                getClosestWorker();
            }
        });
    }

    private void initialize(){
        logoutButton = findViewById(R.id.logout);
        requestButton = findViewById(R.id.request);
        workerInfo = findViewById(R.id.workerInfo);
        workerIcon = findViewById(R.id.workerIcon);
        workerName = findViewById(R.id.workerName);
        workerPhone = findViewById(R.id.workerPhone);
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
                    String customerId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    HashMap hmap = new HashMap();
                    hmap.put("CustomerJobId", customerId);
                    workerRef.updateChildren(hmap);
                    getWorkerLocation();
                    getWorkerInfo();
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

    private void getWorkerInfo() {
        workerInfo.setVisibility(View.VISIBLE);
        DatabaseReference customerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Workers").child(workerFoundId);
        customerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        workerName.setText(Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        workerPhone.setText(Objects.requireNonNull(map.get("phone")).toString());
                    }
                    workerIcon.setImageResource(R.mipmap.workermarker);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    float distance = workerLocation.distanceTo(userLocation.lastLocation);

                    if (distance < 100) {
                        requestButton.setText("Reinforcements has arrived ;) .");
                    } else {
                        requestButton.setText("Worker found: " + /*String.valueOf*/(distance));
                    }
                    workerMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(workerLatLng).title("Your worker").icon(BitmapDescriptorFactory.fromResource(R.mipmap.workermarker)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mapUtils.getCurrentGoogleApiClient(), this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

    }

    @Override
    protected void loadSetting() {
        Intent intent = new Intent(this, CustomerSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void loadActivity() {
        setContentView(R.layout.activity_customer_map);
    }
}












