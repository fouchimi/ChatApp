package com.example.android.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.chatapp.data.ChatContract;

/**
 * Created by ousma on 10/8/2016.
 */

public class FriendFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FriendFragment.class.getSimpleName();
    private static final int FRIENDS_LOADER = 0;
    private EditFriendAdapter mAdapter;
    private String[] friends;
    private static final int INDEX_TO_ID = 0;
    private SharedPreferences sharedPref;
    private String mCurrentUser;

    public FriendFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.current_user), Context.MODE_PRIVATE);
        mCurrentUser = sharedPref.getString(ParseConstants.KEY_USERNAME, "");
       // Toast.makeText(getActivity(), mCurrentUser, Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(FRIENDS_LOADER, null,  this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);
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
        editor.putString(ParseConstants.KEY_FRIEND, friends[position]);
        editor.commit();
        getActivity().startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = new String[]{ChatContract.FriendsEntry.COLUMN_TO};
        String selection = ChatContract.FriendsEntry.COLUMN_FROM + "= ?";
        String[] selectionArgs = new String[]{mCurrentUser};
        return new CursorLoader(getActivity(), ChatContract.FriendsEntry.CONTENT_URI, columns, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0) {
            friends = new String[data.getCount()];
            int i=0;
            while (data.moveToNext()) {
                friends[i] = data.getString(INDEX_TO_ID);
                i++;
            }
            mAdapter = new EditFriendAdapter(getActivity(), android.R.layout.simple_list_item_1, friends);
            getListView().setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }


}
