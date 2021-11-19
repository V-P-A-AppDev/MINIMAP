package com.example.minimap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HelperLoginActivity extends AppCompatActivity {
    private EditText etEmail , etPassword;
    private Button bLogin , bSignup;

    private FirebaseAuth Auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_login);

        Auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(HelperLoginActivity.this , HelperMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);

        bLogin = (Button) findViewById(R.id.login);
        bSignup = (Button) findViewById(R.id.signup);

        bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                Auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(HelperLoginActivity.this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(HelperLoginActivity.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String id = Auth.getCurrentUser().getUid();
                            DatabaseReference user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Helper").child(id);
                            user_db.setValue(true);
                        }
                    }
                });


            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                Auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(HelperLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(HelperLoginActivity.this, "Sign in Error", Toast.LENGTH_SHORT).show();
                        }
                        else{

                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Auth.removeAuthStateListener(authStateListener);
    }
}