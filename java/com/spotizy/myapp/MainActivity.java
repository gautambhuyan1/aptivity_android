package com.spotizy.myapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;
    private ArrayList<ActivityData> activities;
    private ListView activityList;
    private LayoutInflater layoutIflator;
    private Button showInterestList;
    private Button createActivity;
    private Button refreshActivities;
    private double latitude;
    private double longitude;
    //private EditText debugInfo;
    //private WebView webView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  Before Main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showInterestList = (Button)this.findViewById(R.id.interest_cancel);
        createActivity = (Button)this.findViewById(R.id.create_activity);
        refreshActivities = (Button)this.findViewById(R.id.refresh_activities);
        activityList = (ListView)this.findViewById(R.id.interest_list);
        //debugInfo = (EditText)this.findViewById(R.id.debug_info);
        this.layoutIflator = LayoutInflater.from(this);

        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }

        //webView = (WebView)this.findViewById(R.id.json_display);
        System.out.println("#####  Main");

        latitude = longitude = -1;

        /*
        Get current location.
         */


        updateLocation();


        showInterestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InterestActivity.class);
                /*
                WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
                try {
                    webDataFetcher.execute("http://hospitopedia.com/interest/get");
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
                */
                startActivityForResult(intent, 0);
            }
        });

        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                intent.putExtra("interestid", 0);
                startActivity(intent);
                /*
                WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
                try {
                    webDataFetcher.execute("http://hospitopedia.com/interest/get");
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
                */
            }
        });

        refreshActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();

                WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
                try {
                    webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat="+latitude+"&long="+longitude);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
            }
        });

        WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
        try {
            webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat="+latitude+"&long="+longitude);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }
        /*
        showInterestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
                try {
                    webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat=1&long=1");
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
            }
        });
        */
/*
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageToSend.getText().toString();
                WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
                try {
                    System.out.println("#####  URL : "+msg);
                    String urlTmp = URLEncoder.encode("\""+msg+"\"", "utf-8");
                    System.out.println("#####  URL 1: "+urlTmp);
                    String url = "http://hospitopedia.com/message/create?phone=%221234%22&groupid=1&msg="+urlTmp;
                    webDataFetcher.execute(url);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
            }
        });
        */
    }


    public void setActivities(ArrayList<ActivityData> activities) {
        this.activities = activities;
        System.out.println("#####  setData");
        this.activityList.setAdapter(new ActivityAdaptor(this, this.layoutIflator, this.activities));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int interestId = 0;


        interestId = data.getIntExtra("interestid", 0);
        System.out.println("onActivityResult:interestId = "+interestId);
        WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
        try {
            webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid="+interestId+"&phone=%221234%22&lat="+latitude+"&long="+longitude);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateLocation() {
        System.out.println("#####  Get own position");
        GPSTracker tracker = new GPSTracker(this);
        String debugText = null;
        if (!tracker.canGetLocation()) {
            System.out.println("#####   Did not find tracker");
            debugText = "No Text Found";
            tracker.showSettingsAlert();
        } else {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            debugText = "Latitude " + latitude+" longitude "+longitude;
            System.out.println("#### Found location latitude = "+latitude+" longitude = "+longitude);
        }
        //debugInfo.setText(debugText);
        //latitude = 39;
        //longitude = -74;
    }
}
