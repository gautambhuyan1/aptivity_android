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
        String strURL = "http://10.0.0.4:3030";
        String method = str[0];
        String methodName = str[1];
        String params = str[2];
        String result = "";


        //System.out.println("#### Background Task URL ="+url);
        System.out.println("#####  in doInBackground "+str[0]+" "+str[1]+" "+str[2]);
        try {
            switch (method) {
                case "GET": {
                    //call get method
                    System.out.println("#####  methodname = "+methodName);
                    switch (methodName) {
                        case "interests":
                            //strURL.concat("/interests");
                            strURL += "/interests";
                            break;
                        case "activities":
                            //strURL.concat("/activities");
                            strURL += "/activities/";
                            System.out.println("#####  Here "+strURL);
                            break;
                        case "messages":
                            //strURL.concat("/messages");
                            strURL += "/messages/";
                            break;
                        default:
                    }

                    result = ServerDataRetriever.invokeGet(strURL, params);
                }
                    break;
                case "POST": {
                    //call post method
                    switch (methodName) {
                        case "activity":
                            //strURL.concat("/activity");
                            strURL += "/activity";
                            break;
                        case "message":
                            //strURL.concat("/message");
                            strURL += "/message";
                            break;
                        default:
                    }

                    result = ServerDataRetriever.invokePost(strURL, params);
                }
                    break;
                default:
            }
            //String result = ServerDataRetriever.getFromServer(method, methodName, url);
            System.out.println("@@@@ Value = "+result+" Done");
            System.out.println("@@@@ Value = "+result+" Done");
            return result;
        } catch (Exception e) {
            System.out.println("Hello world");
            return new String();
        }
    }

    protected void onPostExecute(String result) {
        System.out.println("#####  Done String = "+result);


        try {
            //JSONObject respJson = new JSONObject(result);
            JSONObject jsonObject = new JSONObject(result);//respJson.getJSONObject("object");
            String type = (String)jsonObject.getString("type");
            if (type.equals("activity")) {
                ArrayList<ActivityData> activityData = new ArrayList<ActivityData>();
                JSONArray groupArray = jsonObject.getJSONArray("activities");

                for (int i = 0; i < groupArray.length(); i++) {
                    JSONObject group = groupArray.getJSONObject(i);
                    String interestId = group.getString("interest");
                    String activityId = group.getString("activityid");
                    String activity = group.getString("activity");
                    JSONArray location = group.getJSONArray("location");
                    double latitude = location.getDouble(0);
                    double longitude = location.getDouble(1);
                    activityData.add(new ActivityData(interestId, activity, latitude, longitude, activity));
                    //System.out.println(latitude + " " + longitude + " " + activity + " " + activity);
                    System.out.println("Done here");
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
                ArrayList<String> interestData = new ArrayList<String>();
                String interestId;
                JSONArray interestArray = jsonObject.getJSONArray("interests");

                for (int i = 0; i < interestArray.length(); i++) {
                    JSONObject interest = interestArray.getJSONObject(i);
                    interestId = interest.getString("interestid");
                    String interestName = interest.getString("interest");
                    interestData.add(interestName);
                    System.out.println("#### Message "+interestId+" "+interestName);
                }
                ((MainActivity)this.activity).setInterests(interestData);
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
