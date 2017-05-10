package com.spotizy.myapp;

/**
 * Created by gautam on 5/10/17.
 */

public class UserCredentials {
    private static String userid;
    private static String imsi;
    private static String name;

    public static void setUserId(String user) {
        userid = user;
    }

    public static void setIMSI(String mobile) {
        imsi = mobile;
    }

    public static void setName(String user) {
        name = user;
    }

    public static String getUserName() {
        return name;
    }

    public static String getUserId() {
        return userid;
    }

    public static String getIMSI() {
        return imsi;
    }


}
