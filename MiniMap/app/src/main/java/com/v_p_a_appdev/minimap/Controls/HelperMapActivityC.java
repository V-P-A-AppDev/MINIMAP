package com.v_p_a_appdev.minimap.Controls;


import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.v_p_a_appdev.minimap.FireBase.HelperMapActivityM;

public class HelperMapActivityC extends UserMapActivityC {
    public HelperMapActivityC(Button logoutButton, Button openMenuButton, Button closeMenuButton, Button chatButton, Button settingButton, ConstraintLayout menuPopUp, Button cancelJobButton, Button leaderBoardButton,  HelperMapActivityM mapAgent) {
        super(logoutButton, openMenuButton, closeMenuButton, chatButton,  settingButton, menuPopUp , mapAgent);
        cancelJobButton.setOnClickListener(v -> mapAgent.cancelJob());
        leaderBoardButton.setOnClickListener(v ->mapAgent.openLeaderBoard());

    }
}
