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

public class CustomerLoginActivity extends AppCompatActivity {
    private EditText emailInput,passwordInput;
    private Button loginButton,registrationButton;
    private FirebaseAuth entranceAuth;
    private FirebaseAuth.AuthStateListener fireBaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        entranceAuth=FirebaseAuth.getInstance();
        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    Intent intent = new Intent(CustomerLoginActivity.this,CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        emailInput=(EditText) findViewById(R.id.email);
        passwordInput=(EditText) findViewById(R.id.password);
        loginButton=(Button) findViewById(R.id.login);
        registrationButton=(Button) findViewById(R.id.registration);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();
                if(password.length()<6){
                    Toast.makeText(CustomerLoginActivity.this,"The password should be 6 characters at least .",Toast.LENGTH_SHORT).show();
                }
                entranceAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerLoginActivity.this,"Something went wrong with the registration process.",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String userId=entranceAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDB= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);
                            currentUserDB.setValue(true);
                        }
                    }
                });
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();
                entranceAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerLoginActivity.this,"Something went wrong with the authentication process.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
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