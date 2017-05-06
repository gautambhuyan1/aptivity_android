package com.spotizy.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MessageActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList<MessageData> messages;
    private ListView msgList;
    private LayoutInflater layoutIflator;
    private TextView activityName;
    private TextView activityDate;
    private String activityNameStr;
    private EditText message_content;
    private Button sendBtn;
    //private Button createActivity;
    private String activityId;
    private String interestId;
    private double latitude;
    private double longitude;
    private Intent intent;
    private GoogleMap mMap;
    private String date;
    //private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  Before Message Main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent triggerData = this.getIntent();
        if (triggerData != null) {
            this.activityId = triggerData.getStringExtra("activityid");
            this.interestId = triggerData.getStringExtra("interestid");
        }
        else {
            System.out.println("##### No intent data");
        }
        //createActivity = (Button)this.findViewById(R.id.create_activity);
        sendBtn = (Button)this.findViewById(R.id.send_message);
        message_content = (EditText)this.findViewById(R.id.message_content);
        activityName = (TextView)this.findViewById(R.id.activity_name);
        activityDate = (TextView)this.findViewById(R.id.activity_date);
        msgList = (ListView)this.findViewById(R.id.message_list);
        this.layoutIflator = LayoutInflater.from(this);
        intent = getIntent();
        activityId = intent.getStringExtra("activityid");
        activityNameStr = intent.getStringExtra("activityname");
        latitude = intent.getDoubleExtra("latitude", 100);
        longitude = intent.getDoubleExtra("longitude", 100);
        date = intent.getStringExtra("date");
        System.out.println("#### INSIDE Activity latlong "+latitude+" "+longitude);
        activityName.setText(activityNameStr);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_show);
        mapFragment.getMapAsync(this);

        //webView = (WebView)this.findViewById(R.id.json_display);
        System.out.println("#####  Main");

        WebApiDataTask webDataFetcher = new WebApiDataTask(MessageActivity.this);
        try {
            //String url = "http://hospitopedia.com/message/get?activityid="+activityId+"&phone=%221234%22";
            //System.out.println("#### Messages: Onclick url = "+url);
            LinkedHashMap<String,String> getParams=new LinkedHashMap<>();
            getParams.put("activity", activityId);
            String getURL = ServerDataRetriever.createGetURL(getParams);
            webDataFetcher.execute("GET", "messages", getURL);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message_content.getText().toString();
                WebApiDataTask webDataFetcher = new WebApiDataTask(MessageActivity.this);
                try {
                    System.out.println("#####  URL : "+msg);
                    String urlTmp = URLEncoder.encode("\"" + msg + "\"", "utf-8");
                    System.out.println("#####  URL 1: "+urlTmp);
                    LinkedHashMap<String,String> postParams=new LinkedHashMap<>();
                    postParams.put("activity", activityId);
                    postParams.put("message", msg);
                    String postURL = ServerDataRetriever.createPostURL(postParams);

                    //System.out.println("#### Messages: Onclick url = "+url);
                    webDataFetcher.execute("POST", "message", postURL);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
                WebApiDataTask messageDataFetcher = new WebApiDataTask(MessageActivity.this);
                try {
                    //String url = "http://hospitopedia.com/message/get?activityid="+activityId+"&phone=%221234%22";
                    //System.out.println("#### Messages: Onclick url = "+url);
                    LinkedHashMap<String,String> getParams=new LinkedHashMap<>();
                    getParams.put("activity", activityId);
                    String getURL = ServerDataRetriever.createGetURL(getParams);
                    messageDataFetcher.execute("GET", "messages", getURL);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        System.out.println("##### Map is ready now");
        // Add a marker to the activity location
        //latitude = 10;
        //longitude = 100;
        System.out.println("#### INSIDE onMapReady "+latitude+" "+longitude);
        LatLng activityLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(activityLocation).title("Marker in location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(activityLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activityLocation, 12));
    }

    public void setMessages(ArrayList<MessageData> msgs) {
        this.messages = msgs;
        System.out.println("#####  setData::Messages");
        for (int i=0; i<msgs.size();i++) {
            System.out.println(" Inside setMessages "+msgs.get(i).getUserId()+" "+msgs.get(i).getMessage());
        }
        this.msgList.setAdapter(new MessageAdaptor(this, this.layoutIflator, this.messages));
    }
}
