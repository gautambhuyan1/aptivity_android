package com.spotizy.myapp;

/**
 * Created by Gautam_Bhuyan on 1/20/2016.
 */
public class ActivityData {
    private String interestId;
    private String activityId;
    private String userid;
    private String username;
    private String date;
    private double latitude;
    private double longitude;
    private String name;

    public ActivityData (String interestId, String userid, String username, String activityId, double latitude, double longitude, String name, String date) {
        this.interestId = interestId;
        this.activityId = activityId;
        this.userid = userid;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = new String(name);
        this.date = new String(date);
    }

    public String getInterestId() {
        return this.interestId;
    }

    public String getUserId() {
        return this.userid;
    }

    public String getUserName() {
        return this.username;
    }

    public String getActivityId() {
        return this.activityId;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getActivityName() {
        return this.name;
    }

    public String getDate() {return this.date;}
}