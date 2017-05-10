package com.spotizy.myapp;

/**
 * Created by Gautam_Bhuyan on 1/8/2016.
 */
public class MessageData {
    private String activityId;
    private String username;
    private String message;

    public MessageData(String username, String msg) {
        this.username = username;
        this.message = new String(msg);
    }

    public String getUserName() {
        return this.username;
    }

    public String getMessage() {
        return this.message;
    }
}
