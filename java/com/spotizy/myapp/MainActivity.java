package com.spotizy.myapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1;
    private ArrayList<ActivityData> activities;
    private Spinner interestList;
    //private LayoutInflater layoutIflator;
    //private ListView interestList;
    private Button showInterestList;
    private Button createActivity;
    private Button refreshActivities;
    private double latitude;
    private double longitude;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private ArrayList<String> interests;
    //private ListView interestList;
    //private Button cancelBtn;
    private String interestId;
    //private EditText debugInfo;
    //private WebView webView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  Before Main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //showInterestList = (Button)this.findViewById(R.id.interest_cancel);
        createActivity = (Button)this.findViewById(R.id.create_activity);
        //refreshActivities = (Button)this.findViewById(R.id.refresh_activities);
        interestList = (Spinner) this.findViewById(R.id.interest_list);
        //debugInfo = (EditText)this.findViewById(R.id.debug_info);
        //this.layoutIflator = LayoutInflater.from(this);

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
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_show);
        mapFragment.getMapAsync(this);
        //webView = (WebView)this.findViewById(R.id.json_display);
        /*
        Get current location.
         */
        System.out.println("#####  Done with page");
        GPSTracker tracker = new GPSTracker(this);
        if (!tracker.canGetLocation()) {
            System.out.println("#####   Did not find tracker");
            tracker.showSettingsAlert();
        } else {
            latitude = tracker.getLatitude();
            longitude = tracker.getLongitude();
            System.out.println("#### Found location latitude = "+latitude+" longitude = "+longitude);
        }
        //Intent intent = new Intent(getApplicationContext(), InterestActivity.class);
        WebApiDataTask interestDataFetcher = new WebApiDataTask(MainActivity.this);
        try {
            LinkedHashMap<String,String> getParams=new LinkedHashMap<>();
            //check whether the msg empty or not

            String getURL = ServerDataRetriever.createGetURL(getParams);
            //webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat="+latitude+"&long="+longitude);

            interestDataFetcher.execute("GET", "interests", getURL);
            //http://aptivity-nodeserver.44fs.preview.openshiftapps.com/activities/interest/0/lat/"+latitude+"/lng/"+longitude);
        } catch (Exception e) {
            //interestDataFetcher.cancel(true);
        }
        //startActivityForResult(intent, 0);
        WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
        try {
            LinkedHashMap<String,String> getParams=new LinkedHashMap<>();
            getParams.put("interest", "all");
            getParams.put("lat", Double.toString(latitude));
            getParams.put("lng", Double.toString(longitude));

            //check whether the msg empty or not

            String getURL = ServerDataRetriever.createGetURL(getParams);
            //webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat="+latitude+"&long="+longitude);

            webDataFetcher.execute("GET", "activities", getURL);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }

        interestList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

                String interestSelected = interestList.getSelectedItem().toString();
                getActivitiesOnInterest(interestSelected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Do nothing

            }

        });


        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                intent.putExtra("interestid", 0);
                startActivity(intent);
            }
        });
    }


    public void setActivities(ArrayList<ActivityData> activities) {

        this.activities = activities;
        ActivityData item;

        ListIterator<ActivityData> li = this.activities.listIterator();
        mMap.clear();

        LatLng activityLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(activityLocation).title("My location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(activityLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activityLocation, 12));
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }
        while (li.hasNext()) {
            item = li.next();
            activityLocation = new LatLng(item.getLatitude(), item.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(activityLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(item.getActivityName()));
            marker.setTag(item);
        }
    }

    protected void getActivitiesOnInterest(String interestId) {
        System.out.println("##### I am here");

        System.out.println("onActivityResult:interestId = "+interestId);
        WebApiDataTask webDataFetcher = new WebApiDataTask(MainActivity.this);
        try {
            //webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid="+interestId+"&phone=%221234%22&lat="+latitude+"&long="+longitude);
            LinkedHashMap<String,String> getParams=new LinkedHashMap<>();
            getParams.put("interest", interestId);
            getParams.put("lat", Double.toString(latitude));
            getParams.put("lng", Double.toString(longitude));

            //check whether the msg empty or not

            String getURL = ServerDataRetriever.createGetURL(getParams);
            //webDataFetcher.execute("http://hospitopedia.com/activity/get?interestid=0&phone=%221234%22&lat="+latitude+"&long="+longitude);

            webDataFetcher.execute("GET", "activities", getURL);
            //webDataFetcher.execute("http://aptivity-nodeserver.44fs.preview.openshiftapps.com/activities/interest/"+interestId+"/lat/"+latitude+"/lng/="+longitude);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }
        //super.onActivityResult(requestCode, resultCode, data);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        System.out.println("##### Map is ready now");
        // Add a marker to the activity location
        //latitude = 10;
        //longitude = 100;
        LatLng activityLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(activityLocation).title("My location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(activityLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activityLocation, 12));
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(this);
    }

    public void setInterests(ArrayList<String> interestArray) {
        this.interests = interestArray;
        System.out.println("#####  setData::Interests");
        //for (int i = 0; i < interestArray.size(); i++) {
          //  System.out.println(" Inside setMessages " + interestArray.get(i).getInterestId() + " " + interestArray.get(i).getInterest());
        //}
        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, interestArray);
        interestList.setAdapter(adaptor);
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {
        ActivityData item = (ActivityData)marker.getTag();
        if (item != null) {
            Intent intent = new Intent(this.getApplicationContext(), MessageActivity.class);
            //intent.setClass("com.spotizy.chat", "com.spotizy.chat.MessageActivity");
            intent.putExtra("interest", item.getInterestId());
            intent.putExtra("activityid", item.getActivityId());
            intent.putExtra("activityname", item.getActivityName());
            intent.putExtra("latitude", item.getLatitude());
            intent.putExtra("longitude", item.getLongitude());
            this.startActivity(intent);
        }
        return false;
    }
}
