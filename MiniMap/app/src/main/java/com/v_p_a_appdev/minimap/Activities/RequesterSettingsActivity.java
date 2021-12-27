package com.v_p_a_appdev.minimap.Activities;


import android.content.Intent;
import android.os.Bundle;

import com.v_p_a_appdev.minimap.R;

public class RequesterSettingsActivity extends UserSettingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Requesters";
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadActivity() {
        setContentView(R.layout.activity_requester_settings);
    }

    @Override
    public void loadMap() {
        Intent intent = new Intent(this , RequesterMapActivity.class);
        startActivity(intent);
        finish();
    }
}































