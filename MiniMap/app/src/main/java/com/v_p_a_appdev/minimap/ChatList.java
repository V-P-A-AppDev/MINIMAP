package com.v_p_a_appdev.minimap;

public class ChatList {
    private String message;
    private boolean currUser;

    public ChatList(String message, boolean currUser) {
        this.message = message;
        this.currUser = currUser;
    }

    public boolean isCurrUser() {
        return currUser;
    }

    public void setCurrUser(boolean currUser) {
        this.currUser = currUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
