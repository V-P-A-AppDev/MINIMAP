package com.v_p_a_appdev.minimap.Activities;


import android.content.Intent;
import android.os.Bundle;

import com.v_p_a_appdev.minimap.R;

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

    @Override
    public void loadMap() {
        Intent intent = new Intent(this , HelperMapActivity.class);
        startActivity(intent);
        finish();
    }
}











