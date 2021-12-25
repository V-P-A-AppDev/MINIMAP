package com.v_p_a_appdev.minimap;


import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public abstract class UserMapActivityM {
    protected boolean isLoggingOut = false ;
    private UserMapActivity userMapActivity;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    protected DatabaseReference userDatabase;
    User currentUser = new User();


    public UserMapActivityM(UserMapActivity userMapActivity) {
        this.userMapActivity = userMapActivity;
    }

    public void getUserInfo() {
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (Objects.requireNonNull(map).get("name") != null) {
                        currentUser.setUserName("Name:\n"+Objects.requireNonNull(map.get("name")).toString());
                    }
                    if (map.get("phone") != null) {
                        currentUser.setPhoneNumber("Phone Number :\n"+Objects.requireNonNull(map.get("phone")).toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        currentUser.setUserImageUrl (Objects.requireNonNull(map.get("profileImageUrl")).toString());

                    }
                    if (map.get("rating") != null) {
                        currentUser.setRating("Rating:\n"+Objects.requireNonNull(map.get("rating")).toString());
                    } else {
                        currentUser.setRating("Rating:\n 0");
                    }
                    userMapActivity.ChangeUserInfo(currentUser);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void loadSetting(){
        userMapActivity.loadSetting();
    }

    public void LogOut(){
        if (!isLoggingOut) {
            isLoggingOut = true;
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(userMapActivity, MainActivity.class);
            userMapActivity.startActivity(intent);
            userMapActivity.finish();
        }
    }
}
