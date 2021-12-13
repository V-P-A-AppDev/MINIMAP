package com.v_p_a_appdev.minimap;


import android.content.Intent;
import android.os.Bundle;

public class CustomerLoginActivity extends UserLoginActivity {
    protected void onCreate(Bundle savedInstanceState) {
        userType = "Customers";
        super.onCreate(savedInstanceState);
    }

    @Override
    void loadActivity() {
        setContentView(R.layout.activity_customer_login);
    }

    @Override
    void changeScreen() {
        Intent intent = new Intent(CustomerLoginActivity.this, CustomerMapActivity.class);
        startActivity(intent);
        finish();
    }
}