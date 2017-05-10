package com.spotizy.myapp;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Gautam_Bhuyan on 1/8/2016.
 */
public class ServerDataRetriever {

    private static final int HTTP_STATUS_OK = 200;

    public static class ServerException extends Exception {
        public ServerException(String msg) {
            super(msg);
        }

        public ServerException(String msg, Throwable thr) {
            super(msg, thr);
        }
    }

    public static String createPostURL(LinkedHashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String createGetURL(LinkedHashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("/");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("/");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public  static synchronized String invokePost(String strURL, String params) {
        //check whether the msg empty or not
        String retVal = "";
        byte[] buf = new byte[1024];

        try {
            URL url = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(params);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            conn.disconnect();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream ist = new BufferedInputStream(conn.getInputStream());
                //ByteArrayOutputStream out = new ByteArrayOutputStream();
                int readcnt = 0;
                int strlen = 0;
                while ((readcnt = ist.read(buf)) != -1) {
                    System.out.println("#####  Count = "+readcnt);
                    strlen += readcnt;
                    //out.write(buf, 0, readcnt);
                }
                if (strlen >= 0) {
                    buf[strlen] = '\0';
                }
                retVal = new String(buf, 0, strlen);
                System.out.println("#### returned bytes "+retVal+" "+retVal.length());
                return retVal;
            }

            else
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
            return "";
    }

    public  static synchronized String invokeGet(String strURL, String params) {
        //check whether the msg empty or not

        String retVal = "";
        byte[] buf = new byte[1024];

        try {
            strURL += params;
            System.out.println("#### invokeGET URL = "+strURL);
            URL url = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            InputStream ist = new BufferedInputStream(conn.getInputStream());
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            int readcnt = 0;
            int strlen = 0;
            while ((readcnt = ist.read(buf)) != -1) {
                System.out.println("#####  Count = "+readcnt);
                strlen += readcnt;
                //out.write(buf, 0, readcnt);
            }
            if (strlen >= 0) {
                buf[strlen] = '\0';
            }
            retVal = new String(buf, 0, strlen);
            System.out.println("#### returned bytes "+retVal+" "+retVal.length());
            return retVal;
        } catch (Exception e) {
            System.out.println("#### Failed " + e.getMessage());
            //throw new ServerException("Improper response from server" + e.getMessage(), e);
        }
        return "";
    }
}