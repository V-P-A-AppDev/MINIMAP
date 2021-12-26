package com.v_p_a_appdev.minimap;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class RequesterMapActivity extends UserMapActivity {
    private Marker helperMarker;
    private Marker requesterMarker;
    private ConstraintLayout helperInfo ;
    private ImageView helperIcon;
    private TextView helperName, helperPhone;
    private RequesterMapActivityC requesterMapActivityC;
    private RequesterMapActivityM mapAgent;
    private String helperImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
        mapAgent = new RequesterMapActivityM(this);
        mapAgent.getUserInfo();
        requesterMapActivityC = new RequesterMapActivityC(
                findViewById(R.id.logout),
                findViewById(R.id.openMenu),
                findViewById(R.id.closeMenu),
                findViewById(R.id.requesterChatButton),
                findViewById(R.id.settings),
                findViewById(R.id.requesterMenu),
                findViewById(R.id.request),
                mapAgent
        );
    }

    private void initialize() {
        helperInfo = findViewById(R.id.helperInfo);
        helperIcon = findViewById(R.id.helperIcon);
        helperName = findViewById(R.id.helperName);
        helperPhone = findViewById(R.id.helperPhone);
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

    public ConstraintLayout getHelperInfo() {
        return helperInfo;
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

    public void setRequesterMarker(LatLng requestLocation) {
        this.requesterMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(requestLocation).title("Help Needed Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.logo_t_foreground)));
    }

    public void setHelperMarker(LatLng helperLatLng) {
        this.helperMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(helperLatLng).title("Your helper").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpermarker)));
    }

    public void changeRequestButtonText(String text){
        requesterMapActivityC.changeRequestButtonText(text);
    }

    public void ShowAssignedHelperInfo(User Helper){
        helperInfo.setVisibility(View.VISIBLE);
        helperName.setText(Helper.getUserName());
        helperName.setText(Helper.getPhoneNumber());
        if (!Helper.getUserImageUrl().equals(""))
            Glide.with(getApplication()).load(Helper.getUserImageUrl()).into(helperIcon);
        else
            helperIcon.setImageResource(R.mipmap.ic_launcher_foreground);
        helperImageUrl = Helper.getUserImageUrl();
    }

    public void RemoveHelper() {
        if (requesterMarker != null) {
            requesterMarker.remove();
        }
        if (helperMarker != null) {
            helperMarker.remove();
        }
        changeRequestButtonText("Ask for help");
        helperInfo.setVisibility(View.GONE);
        helperName.setText("");
        helperPhone.setText("");
        helperIcon.setImageResource(R.mipmap.ic_launcher_foreground);
    }

    public void sendNotification(String userId , String helperFoundId){
        //sendNotificatoin.listenForMessages(RequesterMapActivity.this, userId,userId + helperFoundId);
    }

    @Override
    protected void onDestroy() {
        if (mapAgent.isLoggingOut())
            mapAgent.LogOut();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (mapAgent.isLoggingOut())
            mapAgent.LogOut();
        super.onStop();
    }

    @Override
    public void loadChat(String userId, String otherId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("UserType", "helper");
        intent.putExtra("UserId", otherId);
        intent.putExtra("UserName", helperName.getText());
        intent.putExtra("ConnectionId", userId + otherId);
        intent.putExtra("imageView", helperImageUrl);
        startActivity(intent);
    }
}















