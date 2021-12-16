package com.v_p_a_appdev.minimap;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.ThemedSpinnerAdapter;

import com.google.firebase.auth.FirebaseAuth;

public class HelperMapActivityC extends UserMapActivityC{
    private Button logoutButton, openMenuButton, closeMenuButton;
    private HelperMapActivityMap HelperMapAgent;

    public HelperMapActivityC(Button settingButton, HelperMapActivityMap MapAgent, Button logoutButton, Button openMenuButton, Button closeMenuButton) {
        super(settingButton, MapAgent);
        this.logoutButton = logoutButton;
        this.openMenuButton = openMenuButton;
        this.closeMenuButton = closeMenuButton;
        this.HelperMapAgent =  MapAgent;
        logoutButton.setOnClickListener(v -> {
            HelperMapAgent.LogOut();
        });
        openMenuButton.setOnClickListener(v -> {
            openMenuButton.setVisibility(View.GONE);
            HelperMapAgent.openMenu();
        });
        closeMenuButton.setOnClickListener(v -> {
            openMenuButton.setVisibility(View.VISIBLE);
            HelperMapAgent.closeMenu();
        });
    }

    public void logOut(){
        HelperMapAgent.LogOut();
    }

    public void getAssignedRequester(){
        HelperMapAgent.getAssignedRequester();
    }

}
