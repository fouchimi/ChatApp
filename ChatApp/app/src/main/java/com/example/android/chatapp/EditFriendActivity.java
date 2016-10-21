package com.example.android.chatapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.chatapp.data.ChatContract;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditFriendActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final String TAG = EditFriendActivity.class.getSimpleName();
    private ListView listView;
    private EditFriendAdapter mAdapter;
    private static final int FRIENDS_LOADER = 0;

    private static final int INDEX_USERNAME_ID = 0;
    private static final int INDEX_MEMBER_ID = 0;
    private String[] usernames;
    private String mCurrentUser;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sharedPref = getSharedPreferences(getString(R.string.current_user), Context.MODE_PRIVATE);
        mCurrentUser = sharedPref.getString(ParseConstants.KEY_USERNAME, "");

        getLoaderManager().initLoader(FRIENDS_LOADER, null,  this);

        listView = (ListView) findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!listView.isItemChecked(position)){
                    // Remove Friend
                    String selection = ChatContract.FriendsEntry.COLUMN_FROM + " = ? " + " AND " + ChatContract.FriendsEntry.COLUMN_TO + " = ?" ;
                    String[] selectionArgs = new String[]{mCurrentUser, usernames[position]};
                    Cursor mCursor = getBaseContext().getContentResolver().query(ChatContract.FriendsEntry.CONTENT_URI,
                            new String[]{ChatContract.FriendsEntry.COLUMN_FROM, ChatContract.FriendsEntry.COLUMN_TO},
                            selection, selectionArgs, null);
                    if(mCursor == null) {
                        showAlertDialog(getString(R.string.server_error));
                    }else {
                        if(mCursor.getCount() > 0) {
                            getBaseContext().getContentResolver().delete(ChatContract.FriendsEntry.CONTENT_URI, selection, selectionArgs);
                            Toast.makeText(EditFriendActivity.this, usernames[position] + " has been removed from your friend list", Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    // add a friend
                    ContentValues friendValue = new ContentValues();
                    String selection = ChatContract.MemberEntry.COLUMN_USERNAME + " = ? ";
                    String[] selectionArgs = new String[]{usernames[position]};
                    Cursor mCursor = getBaseContext().getContentResolver().query(ChatContract.MemberEntry.CONTENT_URI,
                            new String[]{ChatContract.MemberEntry._ID},
                            selection, selectionArgs, null);
                    if(mCursor == null) {
                        showAlertDialog(getString(R.string.server_error));
                    }else if(mCursor.getCount() < 1){
                        showAlertDialog(getString(R.string.empty_cursor));
                    }else {
                        if((mCursor.moveToFirst()) && mCursor.getCount() == 1) {
                            friendValue.put(ChatContract.FriendsEntry.COLUMN_MEMBER_KEY, mCursor.getString(INDEX_MEMBER_ID));
                            friendValue.put(ChatContract.FriendsEntry.COLUMN_FROM, mCurrentUser);
                            friendValue.put(ChatContract.FriendsEntry.COLUMN_TO, usernames[position]);
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            friendValue.put(ChatContract.FriendsEntry.COLUMN_DATE, dateFormat.format(date));
                            getBaseContext().getContentResolver().insert(ChatContract.FriendsEntry.CONTENT_URI, friendValue);
                            Toast.makeText(EditFriendActivity.this, usernames[position] + " has been added to your friend list", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tracker tracker = ((MyApplication) getApplication()).getTracker();
        // Set screen name
        tracker.setScreenName(getString(R.string.editFriendActivityScreenName));

        //Send a screen view
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ParseConstants.KEY_USERNAME, mCurrentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFriendCheckMarks(){
        if(usernames != null && usernames.length > 0){
            for(int i=0; i < usernames.length; i++) {
                String selection = ChatContract.FriendsEntry.COLUMN_FROM + " = ? " + " AND " + ChatContract.FriendsEntry.COLUMN_TO + " = ?" ;
                String[] selectionArgs = null;
                if(mCurrentUser != null && usernames[i] != null){
                    selectionArgs = new String[]{mCurrentUser, usernames[i]};
                    Cursor mCursor = getBaseContext().getContentResolver().query(ChatContract.FriendsEntry.CONTENT_URI,
                            new String[]{ChatContract.FriendsEntry.COLUMN_FROM, ChatContract.FriendsEntry.COLUMN_TO},
                            selection, selectionArgs, null);
                    if(mCursor == null){
                        showAlertDialog(getString(R.string.server_error));
                    }else {
                        if(mCursor.getCount() == 1) {
                            listView.setItemChecked(i, true);
                        }
                    }
                }

            }
        }
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendActivity.this);
        builder.setMessage(message);
        builder.setTitle(R.string.login_error_title);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = new String[]{ChatContract.MemberEntry.COLUMN_USERNAME};
        return new CursorLoader(this, ChatContract.MemberEntry.CONTENT_URI, columns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0) {
            usernames = new String[data.getCount()];
            int i=0;
            while (data.moveToNext()) {
                usernames[i] = data.getString(INDEX_USERNAME_ID);
                i++;
            }
            mAdapter = new EditFriendAdapter(EditFriendActivity.this, android.R.layout.simple_list_item_checked, usernames);
            listView.setAdapter(mAdapter);
            addFriendCheckMarks();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }
}
