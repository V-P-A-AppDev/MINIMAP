package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.os.Bundle;

public class HelperLoginActivity extends UserLoginActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Helpers";
        super.onCreate(savedInstanceState);
    }

    @Override
    void loadActivity() {
        setContentView(R.layout.activity_helper_login);
    }

    @Override
    void changeScreen() {
        Intent intent = new Intent(HelperLoginActivity.this, HelperMapActivity.class);
        startActivity(intent);
        finish();
    }
}




