package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationServices;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequesterMapActivity extends UserMapActivity {
    private Marker helperMarker;
    private Marker requesterMarker;
    private LinearLayout helperInfo, menuPopUp;
    private ImageView helperIcon;
    private TextView helperName, helperPhone;
    private RequesterMapActivityC requesterMapActivityC;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        RequesterMapActivityMap MapAgent = new RequesterMapActivityMap(this);
        requesterMapActivityC = new RequesterMapActivityC(
                findViewById(R.id.settings),
                MapAgent,
                findViewById(R.id.logout),
                findViewById(R.id.openMenu),
                findViewById(R.id.closeMenu),
                findViewById(R.id.request));
    }

    private void initialize() {
        helperInfo = findViewById(R.id.helperInfo);
        helperIcon = findViewById(R.id.helperIcon);
        helperName = findViewById(R.id.helperName);
        helperPhone = findViewById(R.id.helperPhone);
        menuPopUp = findViewById(R.id.requesterMenu);
    }



    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void loadSetting() {
        Intent intent = new Intent(this, RequesterSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void loadActivity() {
        setContentView(R.layout.activity_requester_map);
    }


    public Marker getHelperMarker() {
        return helperMarker;
    }

    public Marker getRequesterMarker() {
        return requesterMarker;
    }

    public LinearLayout getHelperInfo() {
        return helperInfo;
    }

    public LinearLayout getMenuPopUp() {
        return menuPopUp;
    }

    public ImageView getHelperIcon() {
        return helperIcon;
    }

    public TextView getHelperName() {
        return helperName;
    }

    public TextView getHelperPhone() {
        return helperPhone;
    }

    public void setRequesterMarker(Marker requesterMarker) {
        this.requesterMarker = requesterMarker;
    }

    public void setHelperMarker(Marker helperMarker) {
        this.helperMarker = helperMarker;
    }
    public void changeRequestButtonText(String text){
        requesterMapActivityC.getRequestButton().setText(text);
    }

    public void openMenu(){
        menuPopUp.setVisibility(View.VISIBLE);
    }
    public void closeMenu(){
        menuPopUp.setVisibility(View.GONE);
    }

}















