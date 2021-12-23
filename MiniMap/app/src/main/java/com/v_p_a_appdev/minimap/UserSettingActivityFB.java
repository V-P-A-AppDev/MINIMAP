package com.v_p_a_appdev.minimap;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserSettingActivityFB {
    private DatabaseReference userDatabase;
    private UserSettingActivity userSettingActivity;
    private User currentUser;
    private String userId;

    public UserSettingActivityFB(UserSettingActivity userSettingActivity) {
        currentUser = new User();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        this.userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        this.userSettingActivity = userSettingActivity;
        this.userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSettingActivity.userType).child(userId);
    }

    public void saveUserInformation(String nameField , String phoneField) {
        currentUser.setUserName(nameField);
        currentUser.setPhoneNumber(phoneField);
        Map info = new HashMap();
        info.put("name", currentUser.getUserName());
        info.put("phone", currentUser.getPhoneNumber());
        userDatabase.updateChildren(info);
        if (userSettingActivity.resultUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(userSettingActivity.getApplication().getContentResolver(), userSettingActivity.resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(e -> {
                finishActivity();
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                userDatabase.updateChildren(newImage);
                finishActivity();
            }).addOnFailureListener(exception -> {
                finishActivity();
            }));
        } else {
            finishActivity();
        }

    }

    public void getUserInfo(EditText nameField, EditText phoneField) {
        userDatabase.addValueEventListener(new ValueEventListener() {
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
                    if (map.get("profileImageUrl") != null) {
                        String profileImageUrl = (Objects.requireNonNull(map.get("profileImageUrl")).toString());
                        Glide.with(userSettingActivity.getApplication()).load(profileImageUrl).into(userSettingActivity.profileImage);
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
        userSettingActivity.loadMap();
    }
}
