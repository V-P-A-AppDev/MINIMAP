package com.v_p_a_appdev.minimap;


import android.view.View;
import android.widget.Button;

public abstract class UserMapActivityC {
    private Button settingButton;
    private UserMapActivityMap MapAgent;
    private Button logoutButton, openMenuButton, closeMenuButton;

    public UserMapActivityC(Button settingButton, UserMapActivityMap MapAgent, Button logoutButton, Button openMenuButton, Button closeMenuButton) {
        this.settingButton = settingButton;
        this.MapAgent = MapAgent;
        this.logoutButton = logoutButton;
        this.openMenuButton = openMenuButton;
        this.closeMenuButton = closeMenuButton;
        settingButton.setOnClickListener(v -> MapAgent.loadSetting());
        logoutButton.setOnClickListener(v -> {
            MapAgent.LogOut();
        });
        openMenuButton.setOnClickListener(v -> {
            openMenuButton.setVisibility(View.GONE);
            MapAgent.openMenu();
        });
        closeMenuButton.setOnClickListener(v -> {
            openMenuButton.setVisibility(View.VISIBLE);
            MapAgent.closeMenu();
        });
    }

    public abstract void stop();
}
