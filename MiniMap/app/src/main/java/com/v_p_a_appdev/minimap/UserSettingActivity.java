package com.v_p_a_appdev.minimap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public abstract class UserSettingActivity extends AppCompatActivity {
    private EditText nameField, phoneField;
    private DatabaseReference userDatabase;
    protected String userType;


    private Uri resultUri;

    private ImageView profileImage;


    private String profileImageUrl;
    User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        nameField = findViewById(R.id.name);
        phoneField = findViewById(R.id.phone);
        Button submitButton = findViewById(R.id.submit);
        Button previousButton = findViewById(R.id.previous);
        profileImage = findViewById(R.id.profileImage);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userType).child(userId);
        getUserInfo();
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        submitButton.setOnClickListener(v -> saveUserInformation(userId));
        previousButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void saveUserInformation(String userId) {
        if (nameField != null && phoneField != null) {
            currentUser.setUserName(nameField.getText().toString());
            currentUser.setPhoneNumber(phoneField.getText().toString());
        }
        Map info = new HashMap();
        info.put("name", currentUser.getUserName());
        info.put("phone", currentUser.getPhoneNumber());
        userDatabase.updateChildren(info);

        if (resultUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(e -> {
                finish();
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                userDatabase.updateChildren(newImage);
                finish();
            }).addOnFailureListener(exception -> {
                finish();
            }));
        } else {
            finish();
        }
    }

    private void getUserInfo() {
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
                        profileImageUrl = (Objects.requireNonNull(map.get("profileImageUrl")).toString());
                        Glide.with(getApplication()).load(profileImageUrl).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImage.setImageURI(resultUri);
        }
    }

    protected abstract void loadActivity();
}
