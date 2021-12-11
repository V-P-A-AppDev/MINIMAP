package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WorkerSettingsActivity extends AppCompatActivity {

    private EditText nameField, phoneField;
    private DatabaseReference workersDatabase;
    private String userName;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_settings);
        nameField = findViewById(R.id.name);
        phoneField = findViewById(R.id.phone);
        Button submitButton = findViewById(R.id.submit);
        Button previousButton = findViewById(R.id.previous);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        workersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Workers").child(userId);
        getUserInfo();
        submitButton.setOnClickListener(v -> saveUserInformation());

        previousButton.setOnClickListener(v -> {
            finish();
            return;
        });
    }

    private void saveUserInformation() {
        if (nameField != null && phoneField != null) {
            userName = nameField.getText().toString();
            userPhone = phoneField.getText().toString();
        }
        Map info = new HashMap();
        info.put("name", userName);
        info.put("phone", userPhone);
        workersDatabase.updateChildren(info);
    }

    private void getUserInfo() {
        workersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("name") != null) {
                        userName = map.get("name").toString();
                        nameField.setText(userName);
                    }
                    if (map.get("phone") != null) {
                        userPhone = map.get("phone").toString();
                        phoneField.setText(userPhone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}



