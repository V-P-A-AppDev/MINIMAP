package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.Marker;

public class HelperMapActivity extends UserMapActivity {
    private Marker jobMarker;
    private LinearLayout requesterInfo;
    private ImageView requesterIcon;
    private TextView requesterName, requesterPhone;
    private HelperMapActivityC helperMapActivityC;
    private HelperMapActivityMap MapAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();

        requesterPhone.setOnClickListener(v -> ShowDialer(requesterPhone));
        MapAgent = new HelperMapActivityMap(this);
        helperMapActivityC = new HelperMapActivityC(findViewById(R.id.settings),
                MapAgent,
                findViewById(R.id.logout),
                findViewById(R.id.openMenu),
                findViewById(R.id.closeMenu),
                findViewById(R.id.leader_board));
        MapAgent.getAssignedRequester();
    }

    private void initialize() {
        requesterInfo = findViewById(R.id.requesterInfo);
        requesterIcon = findViewById(R.id.requesterIcon);
        requesterName = findViewById(R.id.requesterName);
        requesterPhone = findViewById(R.id.requesterPhone);
    }

    @Override
    protected void onStop() {
        MapAgent.stop();
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

    public void openLeaderBoard(){
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
        finish();
    }
}


