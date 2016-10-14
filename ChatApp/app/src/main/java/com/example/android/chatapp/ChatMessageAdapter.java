package com.example.android.chatapp;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ousma on 10/12/2016.
 */

public class ChatMessageAdapter extends BaseAdapter {
    private List<Chat> chatMessageList;
    private Context mContext;
    private LayoutInflater inflater;

    public ChatMessageAdapter(Context context, List<Chat> chats) {
        this.mContext = context;
        this.chatMessageList = chats;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Chat getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_list_item, null, false);
            holder = new ViewHolder();

            holder.usernameField = (TextView) convertView.findViewById(R.id.usernameField);
            holder.dateField = (TextView) convertView.findViewById(R.id.dateField);
            holder.chatField = (TextView) convertView.findViewById(R.id.chatField);

            Chat chat = chatMessageList.get(position);
            holder.usernameField.setText(chat.getUsername());
            holder.dateField.setText(chat.getDate());
            holder.chatField.setText(chat.getMessage());

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        TextView usernameField, dateField, chatField;
    }
}
