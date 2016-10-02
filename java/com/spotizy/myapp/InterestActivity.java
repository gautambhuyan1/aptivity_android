package com.spotizy.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.net.URLEncoder;
import java.util.ArrayList;


public class InterestActivity extends ActionBarActivity {

    private ArrayList<InterestData> interests;
    private ListView interestList;
    private LayoutInflater layoutIflator;
    //private Button cancelBtn;
    private int interestId;
    //private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("#####  In InterestActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest_main);
        Intent triggerData = this.getIntent();
        if (triggerData != null) {
            this.interestId = triggerData.getIntExtra("interestid", 0);
        }
        else {
            System.out.println("##### No intent data");
        }
        //Button cancelBtn = (Button)this.findViewById(R.id.interest_cancel);
        interestList = (ListView)this.findViewById(R.id.interest_list);
        this.layoutIflator = LayoutInflater.from(this);


        //webView = (WebView)this.findViewById(R.id.json_display);
        System.out.println("#####  Interest Main");

        WebApiDataTask webDataFetcher = new WebApiDataTask(InterestActivity.this);
        try {
            String url = "http://hospitopedia.com/interest/get";
            //System.out.println("#### Messages: Onclick url = "+url);
            webDataFetcher.execute(url);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }
/*
        WebApiDataTask webDataFetcher = new WebApiDataTask(MessageActivity.this);
        try {
            String url = "http://hospitopedia.com/message/get?phone=%221234%22&groupid="+groupId;
            //System.out.println("#### Messages: Onclick url = "+url);
            webDataFetcher.execute(url);
        } catch (Exception e) {
            webDataFetcher.cancel(true);
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            //End the activity and go back to the previous screen
        });
*/
        /*
        selectedInterest.setOnClickListener(new View.OnClickListener() {
         */
        /*
            @Override
            public void onClick(View v) {
                String msg = message_content.getText().toString();
                WebApiDataTask webDataFetcher = new WebApiDataTask(InterestActivity.this);
                try {
                    System.out.println("#####  URL : "+msg);
                    String urlTmp = URLEncoder.encode("\"" + msg + "\"", "utf-8");
                    System.out.println("#####  URL 1: "+urlTmp);
                    String url = "http://hospitopedia.com/activity/get?interestid="+interestId+"&phone=%221234%22&lat=1&long=1";
                    //System.out.println("#### Messages: Onclick url = "+url);
                    webDataFetcher.execute(url);
                } catch (Exception e) {
                    webDataFetcher.cancel(true);
                }
            }
        });
*/


    }

    public void setInterests(ArrayList<InterestData> interestArray) {
        this.interests = interestArray;
        System.out.println("#####  setData::Interests");
        for (int i=0; i<interestArray.size();i++) {
            System.out.println(" Inside setMessages "+interestArray.get(i).getInterestId()+" "+interestArray.get(i).getInterest());
        }
        this.interestList.setAdapter(new InterestAdaptor(this, this.layoutIflator, this.interests));
    }
}
