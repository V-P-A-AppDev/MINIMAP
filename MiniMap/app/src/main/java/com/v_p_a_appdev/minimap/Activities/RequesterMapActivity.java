package com.v_p_a_appdev.minimap.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.v_p_a_appdev.minimap.R;
import com.v_p_a_appdev.minimap.Controls.RequesterMapActivityC;
import com.v_p_a_appdev.minimap.FireBase.RequesterMapActivityM;
import com.v_p_a_appdev.minimap.Utils.User;


public class RequesterMapActivity extends UserMapActivity {
    private Marker helperMarker;
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
        helperPhone.setOnClickListener(v -> ShowDialer(helperPhone));
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
    public void loadSetting() {
        inSubScreen = true;
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

    public void setHelperMarker(LatLng helperLatLng) {
        this.helperMarker = mapUtils.getmMap().addMarker(new MarkerOptions().position(helperLatLng).title("Your helper").icon(BitmapDescriptorFactory.fromResource(R.mipmap.helpermarker)));
    }

    public void changeRequestButtonText(String text){
        requesterMapActivityC.changeRequestButtonText(text);
    }

    public void ShowAssignedHelperInfo(User Helper){
        helperInfo.setVisibility(View.VISIBLE);
        helperName.setText(Helper.getUserName());
        helperPhone.setText(Helper.getPhoneNumber());
        if (!Helper.getUserImageUrl().equals(""))
            Glide.with(getApplication()).load(Helper.getUserImageUrl()).into(helperIcon);
        else
            helperIcon.setImageResource(R.mipmap.ic_launcher_foreground);
        helperImageUrl = Helper.getUserImageUrl();
    }

    public void RemoveHelper() {
        if (helperMarker != null) {
            helperMarker.remove();
        }
        changeRequestButtonText("Ask for help");
        helperInfo.setVisibility(View.GONE);
        helperName.setText("");
        helperPhone.setText("");
        helperIcon.setImageResource(R.mipmap.ic_launcher_foreground);
    }


    public void ShowDialer(View view) {
        inSubScreen = true;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + helperPhone.getText().toString()));
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        if(!inSubScreen)
            mapAgent.disconnectRequester();
        super.onStop();
    }

    @Override
    public void loadChat(String userId, String otherId) {
        inSubScreen = true;
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("UserType", "helper");
        intent.putExtra("UserId", otherId);
        intent.putExtra("UserName", helperName.getText());
        intent.putExtra("ConnectionId", userId + otherId);
        intent.putExtra("imageView", helperImageUrl);
        startActivity(intent);
    }
}















