package com.v_p_a_appdev.minimap;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public abstract class UserLoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private FirebaseAuth entranceAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;
    protected String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        entranceAuth = FirebaseAuth.getInstance();
        fireBaseAuthListener = firebaseAuth -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                changeScreen();
            }
        };

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        Button registrationButton = findViewById(R.id.registration);
        registrationButton.setOnClickListener(v -> {
            final String email = emailInput.getText().toString();
            final String password = passwordInput.getText().toString();
            if (password.length() < 6) {
                Toast.makeText(this, "The password should be 6 characters at least .", Toast.LENGTH_SHORT).show();
            }
            entranceAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Something went wrong with the registration process.", Toast.LENGTH_SHORT).show();
                } else {
                    String userId = Objects.requireNonNull(entranceAuth.getCurrentUser()).getUid();
                    DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(userId).child("name");
                    String name = email.toString().split("@")[0];
                    currentUserDB.setValue(name);
                }
            });
        });
        loginButton.setOnClickListener(v -> {
            final String email = emailInput.getText().toString();
            final String password = passwordInput.getText().toString();
            entranceAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Something went wrong with the authentication process.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        entranceAuth.addAuthStateListener(fireBaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        entranceAuth.removeAuthStateListener(fireBaseAuthListener);
    }

    abstract void loadActivity();

    abstract void changeScreen();


}
