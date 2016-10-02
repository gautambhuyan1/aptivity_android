package com.spotizy.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MessageActivity extends ActionBarActivity implements OnMapReadyCallback {

    private ArrayList<MessageData> messages;
    private ListView msgList;
    private LayoutInflater layoutIflator;
    private TextView activityName;
    private String activityNameStr;
    private EditText message_content;
    private Button sendBtn;
    private Button createActivity;
    private int activityId;
    private int interestId;
    private double latitude;
    private double longitude;
    private Intent intent;
    private GoogleMap mMap;
    //private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  Before Message Main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent triggerData = this.getIntent();
        if (triggerData != null) {
            this.activityId = triggerData.getIntExtra("activityid", 0);
            this.interestId = triggerData.getIntExtra("interestid", 0);
        }
        else {
            System.out.println("##### No intent data");
        }
        createActivity = (Button)this.findViewById(R.id.create_activity);
        sendBtn = (Button)this.findViewById(R.id.send_message);
        message_content = (EditText)this.findViewById(R.id.message_content);
        activityName = (TextView)this.findViewById(R.id.activity_name);
        msgList = (ListView)this.findViewById(R.id.message_list);
        this.layoutIflator = LayoutInflater.from(this);
        intent = getIntent();
        activityId = intent.getIntExtra("activityid", 0);
        activityNameStr = intent.getStringExtra("activityname");
        latitude = intent.getDoubleExtra("latitude", 100);
        longitude = intent.getDoubleExtra("longitude", 100);

        System.out.println("#### INSIDE Activity latlong "+latitude+" "+longitude);
        activityName.setText(activityNameStr);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_show);
        mapFragment.getMapAsync(this);

        //webView = (WebView)this.findViewById(R.id.json_display);
        System.out.println("#####  Main");

        WebApiDataTask webDataFetcher = new WebApiDataTask(MessageActivity.this);
        try {
            String url = "http://hospitopedia.com/message/get?activityid="+activityId+"&phone=%221234%22";
            //System.out.println("#### Messages: Onclick url = "+url);
            webDataFetcher.execute(url);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }

        createActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("#### Entering activity create");
                Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                intent.putExtra("interestid", interestId);
                startActivity(intent);
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message_content.getText().toString();
                WebApiDataTask webDataFetcher = new WebApiDataTask(MessageActivity.this);
                try {
                    System.out.println("#####  URL : "+msg);
                    String urlTmp = URLEncoder.encode("\"" + msg + "\"", "utf-8");
                    System.out.println("#####  URL 1: "+urlTmp);
                    String url = "http://hospitopedia.com/message/create?phone=%221234%22&activityid="+activityId+"&msg="+urlTmp;
                    //System.out.println("#### Messages: Onclick url = "+url);
                    webDataFetcher.execute(url);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
            }
        });

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
