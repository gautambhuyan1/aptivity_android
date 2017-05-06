package com.spotizy.myapp;

/**
 * Created by Gautam_Bhuyan on 1/8/2016.
 */
public class MessageData {
    private String activityId;
    private String userId;
    private String message;

    public MessageData(String userId, String msg) {
        this.userId = userId;
        this.message = new String(msg);
    }

    public String getUserId() {
        return this.userId;
    }

    public String getMessage() {
        return this.message;
    }
}
