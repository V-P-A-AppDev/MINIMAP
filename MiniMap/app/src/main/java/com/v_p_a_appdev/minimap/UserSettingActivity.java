package com.v_p_a_appdev.minimap;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class UserSettingActivity extends AppCompatActivity {
    private UserSettingActivityC userSettingActivityC;
    protected String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        UserSettingActivityFB FBAgent = new UserSettingActivityFB(this);
        userSettingActivityC = new UserSettingActivityC(
                findViewById(R.id.name),
                findViewById(R.id.phone),
                findViewById(R.id.submit),
                findViewById(R.id.previous),
                FBAgent);
        userSettingActivityC.showUserInfo();
    }

    protected abstract void loadActivity();
}
