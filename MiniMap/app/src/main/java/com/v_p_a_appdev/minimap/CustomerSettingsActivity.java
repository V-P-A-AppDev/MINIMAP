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
import java.util.Objects;

public class CustomerSettingsActivity extends AppCompatActivity {

    private EditText nameField, phoneField;
    private DatabaseReference customerDatabase;
    User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_settings);
        nameField = findViewById(R.id.name);
        phoneField = findViewById(R.id.phone);
        Button submitButton = findViewById(R.id.submit);
        Button previousButton = findViewById(R.id.previous);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        customerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);
        getUserInfo();
        submitButton.setOnClickListener(v -> saveUserInformation());

        previousButton.setOnClickListener(v -> {
            finish();
            return;
        });
    }

    private void saveUserInformation() {
        if (nameField != null && phoneField != null) {
            currentUser.setUsername(nameField.getText().toString());
            currentUser.setPhonenumber(phoneField.getText().toString());
        }
        Map info = new HashMap();
        info.put("name", currentUser.getUsername());
        info.put("phone", currentUser.getPhonenumber());
        customerDatabase.updateChildren(info);
    }

    private void getUserInfo() {
        customerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (Objects.requireNonNull(map).get("name") != null) {
                        currentUser.setUsername(Objects.requireNonNull(map.get("name")).toString());
                        nameField.setText(currentUser.getUsername());
                    }
                    if (map.get("phone") != null) {
                        currentUser.setPhonenumber(Objects.requireNonNull(map.get("phone")).toString());
                        phoneField.setText(currentUser.getPhonenumber());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}



