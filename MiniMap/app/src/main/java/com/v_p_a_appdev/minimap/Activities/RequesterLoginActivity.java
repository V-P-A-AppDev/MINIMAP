package com.v_p_a_appdev.minimap.Activities;


import android.content.Intent;
import android.os.Bundle;

import com.v_p_a_appdev.minimap.R;

public class RequesterLoginActivity extends UserLoginActivity {
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Requesters";
        super.onCreate(savedInstanceState);
    }

    @Override
    void loadActivity() {
        setContentView(R.layout.activity_requester_login);
    }

    @Override
    void changeScreen() {
        Intent intent = new Intent(this, RequesterMapActivity.class);
        startActivity(intent);
        finish();
    }
}