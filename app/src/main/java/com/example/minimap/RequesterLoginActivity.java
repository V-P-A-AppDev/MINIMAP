package com.example.minimap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RequesterLoginActivity extends AppCompatActivity {
    private EditText etEmail , etPassword;
    private Button bLogin , bSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_login);

        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);

        bLogin = (Button) findViewById(R.id.login);
        bSignup = (Button) findViewById(R.id.signup);


    }
}