package com.v_p_a_appdev.minimap;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class UserLoginActivity extends AppCompatActivity {
    private UserLoginActivityC userLoginActivityC;
    protected String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        UserLoginActivityFB FBAgent = new UserLoginActivityFB(firebaseAuth -> {
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
        userLoginActivityC.addAuthStateListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userLoginActivityC.removeAuthStateListener();
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


}
