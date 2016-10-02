package com.spotizy.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gautam_Bhuyan on 1/16/2016.
 */
public class WebApiDataTask extends AsyncTask<String, Integer, String> {

    private Activity activity;
    private Context context;
    public WebApiDataTask(Activity act ) {
        super();
        this.activity = act;
        this.context = this.activity.getApplicationContext();
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... str) {
        String url = str[0];
        System.out.println("#### Background Task URL ="+url);
        System.out.println("#####  in Background");
        try {
            String result = ServerDataRetriever.getFromServer(url);
            System.out.println("#####  doInBackground result = "+result);
            return result;
        } catch (Exception e) {
            return new String();
        }
    }

    protected void onPostExecute(String result) {
        System.out.println("#####  Done String = "+result);


        try {
            JSONObject respJson = new JSONObject(result);
            JSONObject jsonObject = respJson.getJSONObject("object");
            String type = (String)jsonObject.getString("type");
            if (type.equals("activity")) {
                ArrayList<ActivityData> activityData = new ArrayList<ActivityData>();
                JSONArray groupArray = jsonObject.getJSONArray("activities");

                for (int i = 0; i < groupArray.length(); i++) {
                    JSONObject group = groupArray.getJSONObject(i);
                    int interestId = Integer.parseInt(group.getString("interestid"));
                    int activityId = Integer.parseInt(group.getString("activityid"));
                    double latitude = Double.parseDouble(group.getString("latitude"));
                    double longitude = Double.parseDouble(group.getString("longitude"));
                    String activityName = group.getString("name");
                    activityData.add(new ActivityData(interestId, activityId, latitude, longitude, activityName));
                    System.out.println(latitude + " " + longitude + " " + activityId + " " + activityName);
                }
                ((MainActivity)(this.activity)).setActivities(activityData);
            }
            else if (type.equals("messages")) {
                ArrayList<MessageData> msgData = new ArrayList<MessageData>();
                int activityId = Integer.parseInt(jsonObject.getString("activityid"));
                JSONArray messageArray = jsonObject.getJSONArray("messages");

                for (int i = 0; i < messageArray.length(); i++) {
                    JSONObject message = messageArray.getJSONObject(i);
                    int userId = Integer.parseInt(message.getString("userid"));
                    String msg = message.getString("message");
                    msgData.add(new MessageData(userId, msg));
                    System.out.println("#### Message "+userId+" "+msg);
                }
                ((MessageActivity)this.activity).setMessages(msgData);
            }
            else if (type.equals("interest")) {
                ArrayList<InterestData> interestData = new ArrayList<InterestData>();
                int interestId;
                JSONArray interestArray = jsonObject.getJSONArray("interests");

                for (int i = 0; i < interestArray.length(); i++) {
                    JSONObject interest = interestArray.getJSONObject(i);
                    interestId = Integer.parseInt(interest.getString("interestid"));
                    String interestName = interest.getString("name");
                    interestData.add(new InterestData(interestId, interestName));
                    System.out.println("#### Message "+interestId+" "+interestName);
                }
                ((InterestActivity)this.activity).setInterests(interestData);
            }
            else if (type.equals("create")) {
                ArrayList<InterestData> interestData = new ArrayList<InterestData>();
                String ret = (String)jsonObject.getString("result");
                System.out.println("#####  Result = "+ret);
                ((CreateActivity)this.activity).creationCompleted(true);
                //finish();
            }
        } catch (Exception e) {
            System.out.println("JSON parse error"+e.toString());
        }

        //this.activity.setGroups(groupData);
    }
}
