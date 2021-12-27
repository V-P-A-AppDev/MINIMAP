package com.v_p_a_appdev.minimap.Controls;

import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.v_p_a_appdev.minimap.FireBase.RequesterMapActivityM;

public class RequesterMapActivityC extends UserMapActivityC {
    private Button requestButton;
    public RequesterMapActivityC(Button logoutButton, Button openMenuButton, Button closeMenuButton, Button chatButton, Button settingButton, ConstraintLayout menuPopUp, Button requestButton , RequesterMapActivityM mapAgent) {
        super(logoutButton, openMenuButton, closeMenuButton, chatButton,  settingButton, menuPopUp , mapAgent);
        requestButton.setOnClickListener(v -> mapAgent.Request());
        this.requestButton = requestButton;
    }

    public void changeRequestButtonText(String text){
        requestButton.setText(text);
    }

}
