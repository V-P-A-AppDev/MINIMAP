package com.v_p_a_appdev.minimap.Controls;


import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.v_p_a_appdev.minimap.FireBase.UserMapActivityM;

public abstract class UserMapActivityC {
    private Button logoutButton, openMenuButton, closeMenuButton, chatButton , settingButton;

    public UserMapActivityC(Button logoutButton, Button openMenuButton, Button closeMenuButton, Button chatButton, Button settingButton , ConstraintLayout menuPopUp , UserMapActivityM mapAgent ) {
        this.logoutButton = logoutButton;
        this.openMenuButton = openMenuButton;
        this.closeMenuButton = closeMenuButton;
        this.chatButton = chatButton;
        this.settingButton = settingButton;
        this.logoutButton = logoutButton;
        this.openMenuButton.setOnClickListener(v -> {
            openMenuButton.setVisibility(View.GONE);
            menuPopUp.setVisibility(View.VISIBLE);
        });
        this.closeMenuButton.setOnClickListener(v -> {
            menuPopUp.setVisibility(View.GONE);
            openMenuButton.setVisibility(View.VISIBLE);
        });
        settingButton.setOnClickListener(v -> mapAgent.loadSetting());
        logoutButton.setOnClickListener(v -> mapAgent.LogOut() );
        chatButton.setOnClickListener(v -> mapAgent.loadChat());
    }
}
