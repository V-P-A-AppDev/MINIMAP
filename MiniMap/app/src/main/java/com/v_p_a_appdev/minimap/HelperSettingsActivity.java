package com.v_p_a_appdev.minimap;


import android.os.Bundle;

public class HelperSettingsActivity extends UserSettingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Helpers";
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadActivity() {
        setContentView(R.layout.activity_helper_settings);
    }
}











