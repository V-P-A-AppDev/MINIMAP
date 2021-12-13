package com.v_p_a_appdev.minimap;


import android.os.Bundle;

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
}































