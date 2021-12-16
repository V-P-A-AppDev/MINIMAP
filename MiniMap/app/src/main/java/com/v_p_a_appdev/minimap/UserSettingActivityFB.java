package com.v_p_a_appdev.minimap;

import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserSettingActivityFB {
    private DatabaseReference UserDatabase;
    private UserSettingActivity userSettingActivity;
    private User currentUser;

    public UserSettingActivityFB(UserSettingActivity userSettingActivity) {
        currentUser = new User();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        this.userSettingActivity = userSettingActivity;
        this.UserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSettingActivity.userType).child(userId);
    }

    public void saveUserInformation(String nameField , String phoneField) {
        currentUser.setUserName(nameField);
        currentUser.setPhoneNumber(phoneField);
        Map info = new HashMap();
        info.put("name", currentUser.getUserName());
        info.put("phone", currentUser.getPhoneNumber());
        UserDatabase.updateChildren(info);
        finishActivity();
    }

    public void getUserInfo(EditText nameField, EditText phoneField) {
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
                finishActivity();
            }
        });
    }

    public void finishActivity(){
        userSettingActivity.finish();
    }
}
