package com.v_p_a_appdev.minimap;

import android.widget.Button;

public class RequesterMapActivityC extends UserMapActivityC{
    private Button requestButton;
    private RequesterMapActivityMap MapAgent;

    public RequesterMapActivityC(Button settingButton, RequesterMapActivityMap MapAgent, Button logoutButton, Button openMenuButton, Button closeMenuButton, Button requestButton) {
        super(settingButton, MapAgent, logoutButton, openMenuButton, closeMenuButton);
        this.requestButton = requestButton;
        this.MapAgent = MapAgent;
        requestButton.setOnClickListener(v -> MapAgent.Request());
    }

    public Button getRequestButton() {
        return requestButton;
    }
}
