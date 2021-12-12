package com.v_p_a_appdev.minimap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class UserSettingActivity extends AppCompatActivity {
    private EditText nameField, phoneField;
    private DatabaseReference UserDatabase;
    protected String userType;
    User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        nameField = findViewById(R.id.name);
        phoneField = findViewById(R.id.phone);
        Button submitButton = findViewById(R.id.submit);
        Button previousButton = findViewById(R.id.previous);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(userId);
        getUserInfo();
        submitButton.setOnClickListener(v -> saveUserInformation());

        previousButton.setOnClickListener(v -> {
            finish();
            return;
        });
    }

    private void saveUserInformation() {
        if (nameField != null && phoneField != null) {
            currentUser.setUserName(nameField.getText().toString());
            currentUser.setPhoneNumber(phoneField.getText().toString());
        }
        Map info = new HashMap();
        info.put("name", currentUser.getUserName());
        info.put("phone", currentUser.getPhoneNumber());
        UserDatabase.updateChildren(info);
        finish();
        return;
    }

    private void getUserInfo() {
        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (Objects.requireNonNull(map).get("name") != null) {
                        currentUser.setUserName(Objects.requireNonNull(map.get("name")).toString());
                        nameField.setText(currentUser.getUserName());
                    }
                    if (map.get("phone") != null) {
                        currentUser.setPhoneNumber(Objects.requireNonNull(map.get("phone")).toString());
                        phoneField.setText(currentUser.getPhoneNumber());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
            }
        });
    }

    protected abstract void loadActivity();
}
