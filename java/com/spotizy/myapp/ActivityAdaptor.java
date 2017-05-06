package com.spotizy.myapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Gautam_Bhuyan on 1/22/2016.
 */
public class ActivityAdaptor extends BaseAdapter implements View.OnClickListener {
    private Activity groupActivity;
    private ArrayList<ActivityData> activityList;
    private LayoutInflater layoutIflater;

    public ActivityAdaptor(Activity a, LayoutInflater lytIflator, ArrayList<ActivityData> aL) {
        this.groupActivity = a;
        this.activityList = aL;
        this.layoutIflater = lytIflator;
    }

    private class ActivityHolder {
        public String interestId;
        public String activityId;
        public double latitude;
        public double longitude;
        public String activityName;
        public Button selectedActivity;
    }

    public int getCount() {
        return this.activityList.size();
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public Object getItem(int arg) {
        return null;
    }

    public long getItemId(int pos) {
        return pos;
    }

    public View getView(int pos, View convert, ViewGroup parent) {
        ActivityHolder aHolder;
        if (convert == null) {
            convert = layoutIflater.inflate(R.layout.activity_row, parent, false);
            aHolder = new ActivityHolder();
            aHolder.selectedActivity = (Button)convert.findViewById(R.id.selected_activity);
            aHolder.selectedActivity.setTag(aHolder);
            //gHolder.selectedGroup.setOnClickListener(this);
            convert.setTag(aHolder);
        }
        else {
            aHolder = (ActivityHolder)convert.getTag();
        }


        convert.setOnClickListener(this);
        aHolder.selectedActivity.setOnClickListener(this);

        ActivityData a = activityList.get(pos);
        aHolder.interestId = a.getInterestId();
        aHolder.activityId = a.getActivityId();
        aHolder.activityName = a.getActivityName();
        aHolder.latitude = a.getLatitude();
        aHolder.longitude = a.getLongitude();
        aHolder.selectedActivity.setText(a.getActivityName());

        System.out.println("##### Holder ="+a.getActivityName()+" "+aHolder.toString());

        return convert;

    }

    public void onClick(View v) {
        //int groupId = ((gHolder.selectedGroup)v.getTag()).groupId;
        ActivityHolder a = (ActivityHolder)v.getTag();
        System.out.println("#####    Clicked on button"+a.toString()+" "+a.activityId);
        //Intent intent = new Intent(Intent.ACTION_VIEW);
        Intent intent = new Intent(groupActivity.getApplicationContext(), MessageActivity.class);
        //intent.setClass("com.spotizy.chat", "com.spotizy.chat.MessageActivity");
        intent.putExtra("interest", a.interestId);
        intent.putExtra("activityid", a.activityId);
        intent.putExtra("activityname", a.activityName);
        intent.putExtra("latitude", a.latitude);
        intent.putExtra("longitude", a.longitude);
        this.groupActivity.startActivity(intent);
    }
}
