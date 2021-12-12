package com.v_p_a_appdev.minimap;


import android.os.Bundle;

public class CustomerSettingsActivity extends UserSettingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Customers";
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadActivity() {
        setContentView(R.layout.activity_customer_settings);
    }
}



