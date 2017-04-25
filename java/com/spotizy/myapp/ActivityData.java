package com.spotizy.myapp;

/**
 * Created by Gautam_Bhuyan on 1/20/2016.
 */
public class ActivityData {
    private String interestId;
    private String activityId;
    private double latitude;
    private double longitude;
    private String name;

    public ActivityData (String interestId, String activityId, double latitude, double longitude, String name) {
        this.interestId = interestId;
        this.activityId = activityId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = new String(name);
    }

    public String getInterestId() {
        return this.interestId;
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
}
