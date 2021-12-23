package com.v_p_a_appdev.minimap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class UserSettingActivity extends AppCompatActivity {
    private UserSettingActivityC userSettingActivityC;
    protected String userType;
    ImageView profileImage;
    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        UserSettingActivityFB FBAgent = new UserSettingActivityFB(this);
        profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        userSettingActivityC = new UserSettingActivityC(
                findViewById(R.id.name),
                findViewById(R.id.phone),
                findViewById(R.id.submit),
                findViewById(R.id.previous),
                FBAgent);
        userSettingActivityC.showUserInfo();
    }

    protected abstract void loadActivity();

    protected abstract void loadMap();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImage.setImageURI(resultUri);
        }
    }
}
