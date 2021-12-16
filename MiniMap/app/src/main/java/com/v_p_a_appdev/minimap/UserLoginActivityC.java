package com.v_p_a_appdev.minimap;

import android.widget.Button;
import android.widget.EditText;


public class UserLoginActivityC {
    private EditText emailInput, passwordInput;
    private Button loginButton , registrationButton;
    private UserLoginActivityFB FBAgent;

    public UserLoginActivityC(EditText emailInput, EditText passwordInput, Button loginButton, Button registrationButton , UserLoginActivityFB FBAgent) {
        this.emailInput = emailInput;
        this.passwordInput = passwordInput;
        this.loginButton = loginButton;
        this.registrationButton = registrationButton;
        this.FBAgent = FBAgent;
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

    public void addAuthStateListener(){
        FBAgent.addAuthStateListener();
    }
    public void removeAuthStateListener(){
       FBAgent.removeAuthStateListener();
    }
}
