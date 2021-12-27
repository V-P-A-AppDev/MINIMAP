package com.v_p_a_appdev.minimap.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.v_p_a_appdev.minimap.R;
import com.v_p_a_appdev.minimap.Controls.UserSettingActivityC;
import com.v_p_a_appdev.minimap.FireBase.UserSettingActivityFB;

public abstract class UserSettingActivity extends AppCompatActivity {
    protected UserSettingActivityFB FBAgent;
    public String userType;
    public Uri resultUri;
    public ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();

        FBAgent = new UserSettingActivityFB(this);
        profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
        EditText nameField = findViewById(R.id.name);
        EditText phoneField = findViewById(R.id.phone);
        UserSettingActivityC userSettingActivityC = new UserSettingActivityC(
                nameField,
                phoneField,
                findViewById(R.id.submit),
                findViewById(R.id.previous),
                FBAgent);
        FBAgent.getUserInfo(nameField , phoneField);
    }

    protected abstract void loadActivity();

    public abstract void loadMap();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImage.setImageURI(imageUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loadMap();
    }
}
