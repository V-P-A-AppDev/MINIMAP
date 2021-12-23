package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

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

    @Override
    void setInfo(String email, FirebaseAuth entranceAuth) {
        super.setInfo(email, entranceAuth);
        String userId = Objects.requireNonNull(entranceAuth.getCurrentUser()).getUid();
        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(userId).child("rating");
        currentUserDB.setValue(0);
    }
}