package com.v_p_a_appdev.minimap;


import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public abstract class UserMapActivityC {
    private Button settingButton;
    private Button logoutButton, openMenuButton, closeMenuButton;


    public UserMapActivityC(Button settingButton, UserMapActivityMap MapAgent, Button logoutButton, Button openMenuButton, Button closeMenuButton  ) {
        this.settingButton = settingButton;
        this.logoutButton = logoutButton;
        this.openMenuButton = openMenuButton;
        this.closeMenuButton = closeMenuButton;
        this.settingButton.setOnClickListener(v -> MapAgent.loadSetting());
        this.logoutButton.setOnClickListener(v -> {
            MapAgent.LogOut();
        });
        this.openMenuButton.setOnClickListener(v -> {
            openMenuButton.setVisibility(View.GONE);
            MapAgent.openMenu();
        });
        this.closeMenuButton.setOnClickListener(v -> {
            openMenuButton.setVisibility(View.VISIBLE);
            MapAgent.closeMenu();
        });

    }
}
