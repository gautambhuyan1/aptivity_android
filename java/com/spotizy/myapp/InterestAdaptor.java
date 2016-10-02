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
 * Created by Gautam_Bhuyan on 1/15/2016.
 */
public class InterestAdaptor extends BaseAdapter implements View.OnClickListener {
    private Activity interestActivity;
    private ArrayList<InterestData> interestList;
    private LayoutInflater layoutIflater;

    public InterestAdaptor(Activity a, LayoutInflater lytIflator, ArrayList<InterestData> iL) {
        this.interestActivity = a;
        this.interestList = iL;
        this.layoutIflater = lytIflator;
    }

    private class InterestHolder {
        public int interestId;
        public Button selectedInterest;
    }

    public int getCount() {
        return this.interestList.size();
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
        InterestHolder iHolder;
        if (convert == null) {
            convert = layoutIflater.inflate(R.layout.interest_row, parent, false);
            iHolder = new InterestHolder();
            iHolder.selectedInterest = (Button)convert.findViewById(R.id.selected_interest);
            iHolder.selectedInterest.setTag(iHolder);
            //gHolder.selectedGroup.setOnClickListener(this);
            convert.setTag(iHolder);
        }
        else {
            iHolder = (InterestHolder)convert.getTag();
        }


        convert.setOnClickListener(this);
        iHolder.selectedInterest.setOnClickListener(this);

        InterestData a = interestList.get(pos);
        iHolder.interestId = a.getInterestId();
        iHolder.selectedInterest.setText(a.getInterest());

        System.out.println("##### Holder ="+a.getInterest()+" "+iHolder.toString());

        return convert;

    }

    public void onClick(View v) {
        //int groupId = ((gHolder.selectedGroup)v.getTag()).groupId;
        InterestHolder i = (InterestHolder)v.getTag();
        System.out.println("#####    Clicked on button"+i.toString()+" "+i.interestId);

        Intent intent = new Intent();
        //intent.setClass("com.spotizy.chat", "com.spotizy.chat.MessageActivity");
        System.out.println("#####  onClick:interestId = "+i.interestId);
        intent.putExtra("interestid", i.interestId);
        this.interestActivity.setResult(Activity.RESULT_OK, intent);
        //this.interestActivity.startActivity(intent);
        this.interestActivity.finish();
    }


}
