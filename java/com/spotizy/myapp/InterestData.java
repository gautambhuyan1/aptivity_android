package com.spotizy.myapp;

/**
 * Created by Gautam_Bhuyan on 1/15/2016.
 */
public class InterestData {
    private String interestId;
    private String interestName;

    public InterestData(String interestId, String name) {
        this.interestId = interestId;
        this.interestName = new String(name);
    }

    public String getInterestId() {
        return this.interestId;
    }

    public String getInterest() {
        return this.interestName;
    }
}
