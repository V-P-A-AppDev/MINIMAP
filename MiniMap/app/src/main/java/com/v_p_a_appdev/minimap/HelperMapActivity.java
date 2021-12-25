package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HelperMapActivity extends UserMapActivity {
    private Marker jobMarker;
    private ConstraintLayout requesterInfo;
    private ImageView requesterIcon;
    private TextView requesterName, requesterPhone;
    private HelperMapActivityC helperMapActivityC;
    private HelperMActivityM MapAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();

        requesterPhone.setOnClickListener(v -> ShowDialer(requesterPhone));
        MapAgent = new HelperMActivityM(this);
        helperMapActivityC = new HelperMapActivityC(
                findViewById(R.id.logout),
                findViewById(R.id.openMenu),
                findViewById(R.id.settings),
                findViewById(R.id.closeMenu),
                findViewById(R.id.settings),
                findViewById(R.id.helperMenu),
                findViewById(R.id.cancelJobButton),
                MapAgent
                );
        MapAgent.getAssignedRequester();
    }

    private void initialize() {
        requesterInfo = findViewById(R.id.requesterInfo);
        requesterIcon = findViewById(R.id.requesterIcon);
        requesterName = findViewById(R.id.requesterName);
        requesterPhone = findViewById(R.id.requesterPhone);
    }

    @Override
    protected void onDestroy() {
        if (!MapAgent.isLoggingOut()) {
            MapAgent.disconnectHelper();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void loadSetting() {
        Intent intent = new Intent(this, HelperSettingsActivity.class);
        startActivity(intent);
        finish();
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

    public ConstraintLayout getRequesterInfo() {
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

    public void setJobMarker(LatLng reqLatLng) {

        this.jobMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(reqLatLng).title("requester location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpermarker)));
    }

    public void openLeaderBoard(){
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (MapAgent.isLoggingOut()) {
            return;
        }
        userLocation.lastLocation = location;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        mapUtils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latlng));
        //*Basically it goes in between 1 to 21 to i've chosen somewhere in the middle.
        mapUtils.getmMap().animateCamera(CameraUpdateFactory.zoomTo(14));
        MapAgent.changeHelperAvailable(location);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    public void ShowAssignedRequesterInfo(User Requester){
        requesterInfo.setVisibility(View.VISIBLE);
        requesterName.setText(Requester.getUserName());
        requesterPhone.setText(Requester.getPhoneNumber());
        if (!Requester.getUserImageUrl().equals(""))
            Glide.with(getApplication()).load(Requester.getUserImageUrl()).into(requesterIcon);
        else
            requesterIcon.setImageResource(R.mipmap.ic_launcher_foreground);

    }
}


