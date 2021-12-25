package com.v_p_a_appdev.minimap;


import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

public class HelperMapActivityC extends UserMapActivityC{
    public HelperMapActivityC(Button logoutButton, Button openMenuButton, Button closeMenuButton, Button chatButton, Button settingButton, ConstraintLayout menuPopUp, Button cancelJobButton,  HelperMActivityM mapAgent) {
        super(logoutButton, openMenuButton, closeMenuButton, chatButton,  settingButton, menuPopUp , mapAgent);
        cancelJobButton.setOnClickListener(v -> mapAgent.cancelJob());

    }
}
