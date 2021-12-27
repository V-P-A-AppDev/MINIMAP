package com.v_p_a_appdev.minimap.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.v_p_a_appdev.minimap.R;
import com.v_p_a_appdev.minimap.Controls.UserLoginActivityC;
import com.v_p_a_appdev.minimap.FireBase.UserLoginActivityFB;

public abstract class UserLoginActivity extends AppCompatActivity {
    protected UserLoginActivityC userLoginActivityC;
    protected UserLoginActivityFB FBAgent;
    public String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        FBAgent = new UserLoginActivityFB(firebaseAuth -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                changeScreen();
            }
        },
                this) ;
        userLoginActivityC = new UserLoginActivityC(
                findViewById(R.id.email),
                findViewById(R.id.password),
                findViewById(R.id.login),
                findViewById(R.id.registration),
                FBAgent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FBAgent.addAuthStateListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FBAgent.removeAuthStateListener();
    }

    abstract void loadActivity();

    abstract void changeScreen();

    public void registrationError(){
        Toast.makeText(this, "Something went wrong with the registration process.", Toast.LENGTH_SHORT).show();
    }
    public void passwordInputError(){
        Toast.makeText(this, "The password should be 6 characters at least .", Toast.LENGTH_SHORT).show();
    }
    public void authenticationError(){
        Toast.makeText(this, "Something went wrong with the authentication process.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
        finish();
    }
}
