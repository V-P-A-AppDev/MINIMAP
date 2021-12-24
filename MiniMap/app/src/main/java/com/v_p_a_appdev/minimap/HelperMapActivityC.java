package com.v_p_a_appdev.minimap;


import android.widget.Button;
import android.widget.Switch;

public class HelperMapActivityC extends UserMapActivityC{
    private HelperMapActivityMap HelperMapAgent;
    private Button leaderBoardButton;
    private Switch backgroundSwitch;

    public HelperMapActivityC(Button settingButton, HelperMapActivityMap MapAgent, Button logoutButton, Button openMenuButton, Button closeMenuButton , Button leaderBoardButton ,  Switch backgroundSwitch) {
        super(settingButton, MapAgent , logoutButton , openMenuButton ,  closeMenuButton );
        this.HelperMapAgent =  MapAgent;
        this.leaderBoardButton = leaderBoardButton;
        leaderBoardButton.setOnClickListener(v -> HelperMapAgent.openLeaderBoard());
        this.backgroundSwitch = backgroundSwitch;
        this.backgroundSwitch.setChecked(HelperMapAgent.backGround);
        this.backgroundSwitch.setOnClickListener(v -> HelperMapAgent.BackGround());

    }
}
