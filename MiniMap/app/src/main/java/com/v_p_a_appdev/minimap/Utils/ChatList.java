package com.v_p_a_appdev.minimap.Utils;

public class ChatList {
    private String message;
    private boolean currUser;
    private String userName;
    private String userImageUrl;

    public ChatList(String message, boolean currUser) {
        this.message = message;
        this.currUser = currUser;
        this.userName = "";
        this.userImageUrl = "";
    }

    public ChatList(String message, boolean currUser, String userName, String userImageUrl) {
        this.message = message;
        this.currUser = currUser;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCurrUser() {
        return currUser;
    }

    public void setCurrUser(boolean currUser) {
        this.currUser = currUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
