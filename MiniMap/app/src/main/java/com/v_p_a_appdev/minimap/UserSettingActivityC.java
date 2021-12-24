package com.v_p_a_appdev.minimap;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UserSettingActivityC {
    private Button submitButton , previousButton;

    public UserSettingActivityC(EditText nameField, EditText phoneField, Button submitButton, Button previousButton, UserSettingActivityFB FBAgent) {
        this.submitButton = submitButton;
        this.previousButton = previousButton;
        this.submitButton.setOnClickListener(v ->{
            if (nameField != null && phoneField != null){
                FBAgent.saveUserInformation(nameField.getText().toString() , phoneField.getText().toString());
            }
            });

        this.previousButton.setOnClickListener(v -> FBAgent.finishActivity());
    }
}
