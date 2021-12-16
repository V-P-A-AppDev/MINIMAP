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

public class HelperMapActivity extends UserMapActivity {
    private Marker jobMarker;
    private LinearLayout requesterInfo;
    private ImageView requesterIcon;
    private TextView requesterName, requesterPhone;
    private HelperMapActivityC helperMapActivityC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();

        requesterPhone.setOnClickListener(v -> {
            ShowDialer(requesterPhone);
        });
        HelperMapActivityMap MapAgent = new HelperMapActivityMap(this);
        helperMapActivityC = new HelperMapActivityC(findViewById(R.id.settings),
                MapAgent,
                findViewById(R.id.logout),
                findViewById(R.id.openMenu),
                findViewById(R.id.closeMenu));
        helperMapActivityC.getAssignedRequester();
    }

    private void initialize() {
        requesterInfo = findViewById(R.id.requesterInfo);
        requesterIcon = findViewById(R.id.requesterIcon);
        requesterName = findViewById(R.id.requesterName);
        requesterPhone = findViewById(R.id.requesterPhone);
    }




    @Override
    protected void onStop() {
        super.onStop();
        helperMapActivityC.logOut();
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

    public Marker getJobMarker() {
        return jobMarker;
    }

    public LinearLayout getRequesterInfo() {
        return requesterInfo;
    }

    public ImageView getRequesterIcon() {
        return requesterIcon;
    }

    public TextView getRequesterName() {
        return requesterName;
    }

    public TextView getRequesterPhone() {
        return requesterPhone;
    }

    public void setJobMarker(Marker jobMarker) {
        this.jobMarker = jobMarker;
    }
}


