package com.example.android.chatapp;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by ousma on 10/8/2016.
 */

public class EditFriendAdapter extends ArrayAdapter {

    public EditFriendAdapter(Context context, int resource, String[] usernames) {
        super(context, resource, usernames);
    }
}
