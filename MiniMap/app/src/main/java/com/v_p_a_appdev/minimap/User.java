package com.v_p_a_appdev.minimap;

public class User {
    private String username;
    private String phonenumber;

    public User(){
        username = "";
        phonenumber = "";
    }

    public User(String username, String phonenumber)
    {
        this.username = username;
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
