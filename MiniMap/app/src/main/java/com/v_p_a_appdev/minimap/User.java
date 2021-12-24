package com.v_p_a_appdev.minimap;

public class User {
    private String userName;
    private String phoneNumber;

    public User() {
        userName = "";
        phoneNumber = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
