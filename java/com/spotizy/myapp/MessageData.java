package com.spotizy.myapp;

/**
 * Created by Gautam_Bhuyan on 1/8/2016.
 */
public class MessageData {
    private int activityId;
    private int userId;
    private String message;

    public MessageData(int userId, String msg) {
        this.userId = userId;
        this.message = new String(msg);
    }

    public int getUserId() {
        return this.userId;
    }

    public String getMessage() {
        return this.message;
    }
}
