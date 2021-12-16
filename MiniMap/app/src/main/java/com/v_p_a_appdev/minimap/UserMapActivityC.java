package com.v_p_a_appdev.minimap;

import android.widget.Button;

public class UserMapActivityC {
    private Button settingButton;
    private UserMapActivityMap MapAgent;

    public UserMapActivityC(Button settingButton , UserMapActivityMap MapAgent ) {
        this.settingButton = settingButton;
        this.MapAgent = MapAgent;
        settingButton.setOnClickListener(v -> MapAgent.loadSetting());
    }
}
