package com.v_p_a_appdev.minimap;

import android.widget.Button;
import android.widget.EditText;

public class UserSettingActivityC {
    private EditText nameField, phoneField;
    private UserSettingActivityFB FBAgent;
    private Button submitButton , previousButton;

    public UserSettingActivityC(EditText nameField, EditText phoneField, Button submitButton, Button previousButton, UserSettingActivityFB FBAgent) {
        this.nameField = nameField;
        this.phoneField = phoneField;
        this.submitButton = submitButton;
        this.previousButton = previousButton;
        this.FBAgent = FBAgent;
        this.submitButton.setOnClickListener(v ->{
            if (nameField != null && phoneField != null){
                FBAgent.saveUserInformation(nameField.getText().toString() , phoneField.getText().toString());
            }
            });

        this.previousButton.setOnClickListener(v -> FBAgent.finishActivity());
    }

    public void showUserInfo() {
        FBAgent.getUserInfo(nameField , phoneField);
    }
}
