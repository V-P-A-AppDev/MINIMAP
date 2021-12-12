package com.v_p_a_appdev.minimap;


import android.os.Bundle;

public class WorkerSettingsActivity extends UserSettingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Workers";
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadActivity() {
        setContentView(R.layout.activity_worker_settings);
    }
}



