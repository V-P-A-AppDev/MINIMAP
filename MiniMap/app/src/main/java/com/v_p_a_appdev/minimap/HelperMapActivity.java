package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HelperMapActivity extends UserMapActivity {
    private Marker jobMarker;
    private ConstraintLayout requesterInfo;
    private ImageView requesterIcon;
    private TextView requesterName, requesterPhone , userRating ;
    private HelperMapActivityC helperMapActivityC;
    private HelperMapActivityM mapAgent;
    private String requesterImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();

        requesterPhone.setOnClickListener(v -> ShowDialer(requesterPhone));
        mapAgent = new HelperMapActivityM(this);
        mapAgent.getUserInfo();
        helperMapActivityC = new HelperMapActivityC(
                findViewById(R.id.logout),
                findViewById(R.id.openMenu),
                findViewById(R.id.closeMenu),
                findViewById(R.id.helperChatButton),
                findViewById(R.id.settings),
                findViewById(R.id.helperMenu),
                findViewById(R.id.cancelJobButton),
                 findViewById(R.id.leaderboard),
                 mapAgent
                );
        mapAgent.getAssignedRequester();
    }

    private void initialize() {
        requesterInfo = findViewById(R.id.requesterInfo);
        requesterIcon = findViewById(R.id.requesterIcon);
        requesterName = findViewById(R.id.requesterName);
        requesterPhone = findViewById(R.id.requesterPhone);
        userRating = findViewById(R.id.curRating);
    }



    @Override
    protected void onStop() {
        if (!inSubScreen)
            mapAgent.disconnectHelper();
        super.onStop();
    }


    @Override
    protected void loadSetting() {
        inSubScreen = true;
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

    public ConstraintLayout getRequesterInfo() {
        return requesterInfo;
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
        inSubScreen = true;
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (mapAgent.isLoggingOut()) {
            return;
        }
        if (userLocation.lastLocation == null || userLocation.lastLocation.distanceTo(location) > 1) {
            userLocation.lastLocation = location;
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mapUtils.getmMap().moveCamera(CameraUpdateFactory.newLatLng(latlng));
            //*Basically it goes in between 1 to 21 to i've chosen somewhere in the middle.
        }
        //mapUtils.getmMap().animateCamera(CameraUpdateFactory.zoomTo(zoom));
        mapAgent.changeHelperAvailable(userLocation.lastLocation);

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
        requesterImageUrl = Requester.getUserImageUrl();
    }

    @Override
    public void loadChat(String userId, String otherId) {
        inSubScreen = true;
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("UserType", "Requester");
        intent.putExtra("UserId", otherId);
        intent.putExtra("UserName", requesterName.getText());
        intent.putExtra("ConnectionId", otherId + userId);
        intent.putExtra("imageView", requesterImageUrl);
        startActivity(intent);
    }

    @Override
    public void ChangeUserInfo(User currentUser) {
        super.ChangeUserInfo(currentUser);
        userRating.setText(currentUser.getRating());
    }
}


