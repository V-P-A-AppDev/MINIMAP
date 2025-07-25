package com.v_p_a_appdev.minimap.Controls;

import android.widget.Button;
import android.widget.EditText;

import com.v_p_a_appdev.minimap.FireBase.UserLoginActivityFB;


public  class UserLoginActivityC {


    public UserLoginActivityC(EditText emailInput, EditText passwordInput, Button loginButton, Button registrationButton , UserLoginActivityFB FBAgent) {
        registrationButton.setOnClickListener(v -> {
            final String email = emailInput.getText().toString();
            final String password = passwordInput.getText().toString();
            FBAgent.createUserWithEmailAndPassword(email , password);
        });
        loginButton.setOnClickListener(v -> {
            final String email = emailInput.getText().toString();
            final String password = passwordInput.getText().toString();
            FBAgent.signInWithEmailAndPassword(email , password);
        });
    }
}
