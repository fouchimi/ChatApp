package com.example.android.chatapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ousma on 10/10/2016.
 */

public class MessageListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Message> messageList;
    private String mCurrentUser;

    public MessageListAdapter(Context context, List<Message> messageList, String currentUser) {
        this.mContext = context;
        this.messageList = messageList;
        mCurrentUser = currentUser;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);

        LayoutInflater mInflater  = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (messageList.get(position).getCurrentUser().equals(mCurrentUser)) {
            convertView = mInflater.inflate(R.layout.list_row_layout_right, null);
        } else {
            convertView = mInflater.inflate(R.layout.list_row_layout_left, null);
        }

        TextView messageView = (TextView)  convertView.findViewById(R.id.message_text_view);
        messageView.setText(message.getMessage());

        return convertView;
    }
}
