package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WorkerLoginActivity extends AppCompatActivity {
    private EditText emailInput,passwordInput;
    private FirebaseAuth entranceAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_login);
        entranceAuth=FirebaseAuth.getInstance();
        fireBaseAuthListener = firebaseAuth -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user!=null){
                Intent intent = new Intent(WorkerLoginActivity.this,WorkerMapActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };

        emailInput= findViewById(R.id.email);
        passwordInput= findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        Button registrationButton = findViewById(R.id.registration);
        registrationButton.setOnClickListener(v -> {
            final String email = emailInput.getText().toString();
            final String password = passwordInput.getText().toString();
            if(password.length()<6){
                Toast.makeText(WorkerLoginActivity.this,"The password should be 6 charecters at least .",Toast.LENGTH_SHORT).show();
            }
            entranceAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(WorkerLoginActivity.this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(WorkerLoginActivity.this, "Something went wrong with the registration process.", Toast.LENGTH_SHORT).show();
                } else {
                    String userId = entranceAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Workers").child(userId);
                    currentUserDB.setValue(true);
                }
            });
        });
        loginButton.setOnClickListener(v -> {
            final String email = emailInput.getText().toString();
            final String password = passwordInput.getText().toString();
            entranceAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(WorkerLoginActivity.this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(WorkerLoginActivity.this, "Something went wrong with the authentication process.", Toast.LENGTH_SHORT).show();
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
}















