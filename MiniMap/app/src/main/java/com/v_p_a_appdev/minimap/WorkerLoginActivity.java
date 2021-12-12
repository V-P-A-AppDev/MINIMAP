package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.os.Bundle;

public class WorkerLoginActivity extends UserLoginActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Workers";
        super.onCreate(savedInstanceState);
    }

    @Override
    void loadActivity() {
        setContentView(R.layout.activity_worker_login);
    }

    @Override
    void changeScreen() {
        Intent intent = new Intent(WorkerLoginActivity.this, WorkerMapActivity.class);
        startActivity(intent);
        finish();
    }
}