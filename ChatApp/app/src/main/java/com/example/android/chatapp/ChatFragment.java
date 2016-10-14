package com.example.android.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.chatapp.data.ChatContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ousma on 10/8/2016.
 */

public class ChatFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ChatFragment.class.getSimpleName();
    private SharedPreferences sharedPref;
    private String mCurrentUser;
    private static final int CHAT_LOADER = 0;
    private ChatMessageAdapter mAdapter;
    private List<Chat> chatListMessage = new ArrayList<>();

    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.current_user), Context.MODE_PRIVATE);
        mCurrentUser = sharedPref.getString(ParseConstants.KEY_USERNAME, "");
       // Toast.makeText(getActivity(), "ChatFragment: " + mCurrentUser, Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(CHAT_LOADER, null,  this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        SharedPreferences.Editor editor  = sharedPref.edit();
        if(sharedPref.contains(ParseConstants.KEY_FRIEND)){
            editor.remove(ParseConstants.KEY_FRIEND);
        }
        Chat chat = (Chat) l.getItemAtPosition(position);
        editor.putString(ParseConstants.KEY_FRIEND, chat.getUsername());
        editor.commit();
        getActivity().startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = ChatContract.ChatsEntry.COLUMN_FROM + "= ? AND " + ChatContract.ChatsEntry.COLUMN_LATEST + "= ? ";
        String[] selectionArgs = new String[]{mCurrentUser, "1"};
        return new CursorLoader(getActivity(), ChatContract.ChatsEntry.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        chatListMessage.clear();
        if(data != null && data.getCount() > 0) {
            int i=0;
            if(data.moveToLast()){
                do{
                    String receiver = data.getString(data.getColumnIndex(ChatContract.ChatsEntry.COLUMN_TO));
                    String chatMessage = data.getString(data.getColumnIndex(ChatContract.ChatsEntry.COLUMN_CHAT_MESSAGE));
                    String date = formatDateTime(getActivity(), data.getString(data.getColumnIndex(ChatContract.ChatsEntry.COLUMN_DATE)));
                    chatListMessage.add(new Chat(receiver, date, chatMessage));
                    Log.d(TAG, chatMessage);
                    Log.d(TAG, date);
                    Log.d(TAG, receiver);
                    i++;
                }while (data.moveToPrevious());
            }
            mAdapter = new ChatMessageAdapter(getActivity(), chatListMessage);
            getListView().setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }

    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
//                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
//                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;
                // TimeZone.getDefault().getOffset(when)

                finalDateTime = android.text.format.DateUtils.formatDateTime(context, when , flags);
            }
        }
        return finalDateTime;
    }

}
