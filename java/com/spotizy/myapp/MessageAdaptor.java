package com.spotizy.myapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gautam_Bhuyan on 1/10/2016.
 */
public class MessageAdaptor extends BaseAdapter implements View.OnClickListener {
    private Activity msgActivity;
    private ArrayList<MessageData> messages;
    private LayoutInflater layoutIflater;

    public MessageAdaptor(Activity m, LayoutInflater lytIflator, ArrayList<MessageData> msg) {
        this.msgActivity = m;
        this.messages = msg;
        this.layoutIflater = lytIflator;
    }

    private class MessageHolder {
        public int userId;
        public String message;
        TextView userArea;
        TextView messageArea;
    }

    public int getCount() {
        return this.messages.size();
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
        MessageHolder mHolder;
        if (convert == null) {
            convert = layoutIflater.inflate(R.layout.message_row, parent, false);
            mHolder = new MessageHolder();
            mHolder.userArea = (TextView)convert.findViewById(R.id.user_id);
            mHolder.messageArea = (TextView)convert.findViewById(R.id.message_item);
            mHolder.userArea.setTag(mHolder);
            mHolder.messageArea.setTag(mHolder);
            //gHolder.selectedGroup.setOnClickListener(this);
            convert.setTag(mHolder);
        }
        else {
            mHolder = (MessageHolder)convert.getTag();
        }


        //convert.setOnClickListener(this);
        //gHolder.selectedGroup.setOnClickListener(this);

        MessageData  this_message = messages.get(pos);
        mHolder.userId = this_message.getUserId();
        mHolder.message = new String(this_message.getMessage());
        mHolder.userArea.setText(Integer.toString(mHolder.userId));
        mHolder.messageArea.setText(mHolder.message);

        System.out.println("##### Message ="+mHolder.userId+" "+mHolder.message);

        return convert;

    }

    public void onClick(View v) {
        //int groupId = ((gHolder.selectedGroup)v.getTag()).groupId;
        MessageHolder g = (MessageHolder)v.getTag();
        System.out.println("#####    Clicked on button"+g.userId+" "+g.message);

    }
}
