package com.spotizy.myapp;

import org.bson.types.ObjectId;

/**
 * Created by Gautam_Bhuyan on 1/8/2016.
 */
public class MessageData {
    private ObjectId activityId;
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
