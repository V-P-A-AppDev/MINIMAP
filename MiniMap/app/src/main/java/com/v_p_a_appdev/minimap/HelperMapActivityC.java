package com.v_p_a_appdev.minimap;


import android.widget.Button;

public class HelperMapActivityC extends UserMapActivityC{
    private HelperMapActivityMap HelperMapAgent;

    public HelperMapActivityC(Button settingButton, HelperMapActivityMap MapAgent, Button logoutButton, Button openMenuButton, Button closeMenuButton) {
        super(settingButton, MapAgent , logoutButton , openMenuButton ,  closeMenuButton );
        this.HelperMapAgent =  MapAgent;

    }

    public void logOut(){
        HelperMapAgent.LogOut();
    }

    public void getAssignedRequester(){
        HelperMapAgent.getAssignedRequester();
    }

}
