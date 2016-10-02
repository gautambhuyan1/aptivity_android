package com.spotizy.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
//import android.support.v7.app.AppCompactActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;


public class CreateActivity extends ActionBarActivity implements OnMapReadyCallback {

    /*private ArrayList<MessageData> messages;
    private ListView msgList;
    private LayoutInflater layoutIflator;
    private EditText activityName;
    private String activityNameStr;
    private EditText message_content;
    private Button sendBtn;
    private int activityId;
    //private WebView webView;
    */
    private Context context;
    private Button showInterestList;
    private Button createActivity;
    private Button searchAddress;
    private EditText activityAddress;
    private EditText activityName;
    private EditText activityDateTime;
    private int interestId;
    private double latitude;
    private double longitude;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  In Create Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        context = this.getApplicationContext();
        Intent triggerData = this.getIntent();
        if (triggerData != null) {
            this.interestId = triggerData.getIntExtra("interestid", 0);
            System.out.println("##### Interest ID is"+this.interestId);
        }
        else {
            System.out.println("##### No intent data");
        }
        showInterestList = (Button)this.findViewById(R.id.select_interest);
        createActivity = (Button)this.findViewById(R.id.create_activity);
        searchAddress = (Button)this.findViewById(R.id.address_search);
        activityAddress = (EditText)this.findViewById(R.id.activity_address);
        activityName = (EditText)this.findViewById(R.id.activity_name);
        activityDateTime = (EditText)this.findViewById(R.id.activity_datetime);
        //interestId = getIntent().getIntExtra("interestId", 0);
        //activityName.setText(activityNameStr);

        latitude = longitude = -1;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
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

        showInterestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InterestActivity.class);

                startActivityForResult(intent, 0);
            }
        });

        searchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = activityAddress.getText().toString();
                List<Address> addressList = null;
                Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
                try {
                    addressList = geoCoder.getFromLocationName(address, 1);
                }
                catch (Exception e) {

                }
                Address myAddress = addressList.get(0);
                latitude = myAddress.getLatitude();
                longitude = myAddress.getLongitude();
                LatLng location = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(location).title("Marker in location"));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
            }
        });

        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = activityName.getText().toString();
                String dateAndTime = activityDateTime.getText().toString();
                WebApiDataTask webDataFetcher = new WebApiDataTask(CreateActivity.this);
                try {
                    name = URLEncoder.encode("\""+name+"\"", "utf-8");
                    dateAndTime = URLEncoder.encode("\""+dateAndTime+"\"", "utf-8");
                    System.out.println("#####  In CreateActivity");
                    //String urlTmp = URLEncoder.encode("\"" + msg + "\"", "utf-8");
                    //System.out.println("#####  URL 1: "+urlTmp);
                    String url = "http://hospitopedia.com/activity/create?interestid="+interestId+"&name="+name+"&lat="+latitude+"&long="+longitude+"&datetime="+dateAndTime;

                    System.out.println("##### URL for CREATE = "+url);
                    //String urlTmp = URLEncoder.encode(url, "utf-8");
                    //System.out.println("#### Create activity url = "+urlTmp);
                    webDataFetcher.execute(url);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        interestId = data.getIntExtra("interestid", 0);

        System.out.println("#### onActivityResult::InterestId = "+interestId);

        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        System.out.println("##### Map is ready now");
        // Add a marker to the activity location
        //latitude = 10;
        //longitude = 100;
        LatLng activityLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(activityLocation).title("Marker in location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(activityLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activityLocation, 12));
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void creationCompleted(boolean status) {
        System.out.println("####   Completed creation of task");
        finish();
    }
}
