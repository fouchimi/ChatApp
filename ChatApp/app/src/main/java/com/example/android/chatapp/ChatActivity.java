package com.example.android.chatapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.chatapp.data.ChatContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private ListView messageListView;
    private EditText mEditText;
    private Button mSendButton;
    protected String mCurrentUser;
    protected String receiver;
    private List<Message> messageList = new ArrayList<>();
    private String chat;
    protected MessageListAdapter mMessageListAdapter;
    private static final int CHAT_LOADER = 0;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences(getString(R.string.current_user), Context.MODE_PRIVATE);
        mCurrentUser = sharedPref.getString(ParseConstants.KEY_USERNAME, "");
        receiver = sharedPref.getString(ParseConstants.KEY_FRIEND, "");

        messageListView = (ListView) findViewById(R.id.messageHistoryList);
        mEditText = (EditText) findViewById(R.id.message);
        mSendButton = (Button) findViewById(R.id.sendMessageButton);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat = mEditText.getText().toString();
                String selection;
                String[] selectionArgs;
                String order;
                Cursor mCursor;
                if(!chat.isEmpty()){
                    messageList.add(new Message(mCurrentUser, receiver, chat));
                    mMessageListAdapter = new MessageListAdapter(ChatActivity.this, messageList, mCurrentUser);
                    messageListView.setAdapter(mMessageListAdapter);
                    mEditText.setText("");
                    ContentValues chatValues = new ContentValues();
                    selection = ChatContract.MemberEntry.COLUMN_USERNAME + " = ? ";
                    selectionArgs = new String[]{mCurrentUser};
                    mCursor = getBaseContext().getContentResolver().query(ChatContract.MemberEntry.CONTENT_URI,
                            new String[]{ChatContract.MemberEntry._ID},
                            selection, selectionArgs, null);
                    if(mCursor != null && mCursor.getCount() == 1 && mCurrentUser!= null && ! mCurrentUser.isEmpty()) {
                        chatValues.put(ChatContract.ChatsEntry.COLUMN_MEMBER_KEY, mCursor.getColumnIndex(ChatContract.MemberEntry._ID));
                        chatValues.put(ChatContract.ChatsEntry.COLUMN_FROM, mCurrentUser);
                        chatValues.put(ChatContract.ChatsEntry.COLUMN_TO, receiver);
                        chatValues.put(ChatContract.ChatsEntry.COLUMN_CHAT_MESSAGE, chat);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        chatValues.put(ChatContract.ChatsEntry.COLUMN_DATE, dateFormat.format(date));
                        chatValues.put(ChatContract.ChatsEntry.COLUMN_LATEST, "1");

                        selection = ChatContract.ChatsEntry.COLUMN_LATEST + "= ? AND " + ChatContract.ChatsEntry.COLUMN_FROM + "= ? AND "
                                + ChatContract.ChatsEntry.COLUMN_TO + "= ? ";
                        selectionArgs = new String[]{"1", mCurrentUser, receiver};
                        order = ChatContract.ChatsEntry._ID + " DESC LIMIT 1";
                        mCursor = getBaseContext().getContentResolver().query(ChatContract.ChatsEntry.CONTENT_URI,
                                new String[]{ChatContract.ChatsEntry._ID,
                                        ChatContract.ChatsEntry.COLUMN_CHAT_MESSAGE,
                                        ChatContract.ChatsEntry.COLUMN_LATEST}, selection, selectionArgs, order);
                        //Update latest row with value of 0
                        if(mCursor != null && mCursor.getCount() == 1) {
                            if(mCursor.moveToFirst()) {
                                String chat_id;
                                do {
                                    chat_id = mCursor.getString(mCursor.getColumnIndex(ChatContract.ChatsEntry._ID));
                                    Log.d(TAG, mCursor.getString(mCursor.getColumnIndex(ChatContract.ChatsEntry.COLUMN_CHAT_MESSAGE)));
                                } while (mCursor.moveToNext());
                                ContentValues updatedValues = new ContentValues();
                                updatedValues.put(ChatContract.ChatsEntry.COLUMN_LATEST, "0");
                                selection = ChatContract.ChatsEntry._ID + "= ?";
                                selectionArgs = new String[]{chat_id};
                                getBaseContext().getContentResolver().update(ChatContract.ChatsEntry.CONTENT_URI, updatedValues, selection, selectionArgs);
                            }
                        }
                        getBaseContext().getContentResolver().insert(ChatContract.ChatsEntry.CONTENT_URI, chatValues);
                        scrollDown();
                    }
                }
            }
        });
        getLoaderManager().initLoader(CHAT_LOADER, null,  this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.remove(ParseConstants.KEY_USERNAME);
                navigateToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mCurrentUser != null && receiver != null && !mCurrentUser.isEmpty() && !receiver.isEmpty()) {
            //Toast.makeText(this, "Sender: " + mCurrentUser + ", receiver: " + receiver, Toast.LENGTH_SHORT).show();
            String selection = ChatContract.ChatsEntry.COLUMN_FROM + "= ? AND " + ChatContract.ChatsEntry.COLUMN_TO + "= ?" +
                    " OR " + ChatContract.ChatsEntry.COLUMN_FROM + "= ? AND " + ChatContract.ChatsEntry.COLUMN_TO + "= ?";
            String[] selectionArgs = new String[]{mCurrentUser, receiver, receiver, mCurrentUser};
            return new CursorLoader(this, ChatContract.ChatsEntry.CONTENT_URI, null, selection, selectionArgs, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        messageList.clear();
        if(data != null && data.getCount() > 0) {
            if(data.moveToFirst()) {
                do {
                    String message_from = data.getString(data.getColumnIndex(ChatContract.ChatsEntry.COLUMN_FROM));
                    String message_to = data.getString(data.getColumnIndex(ChatContract.ChatsEntry.COLUMN_TO));
                    String chat_message = data.getString(data.getColumnIndex(ChatContract.ChatsEntry.COLUMN_CHAT_MESSAGE));
                    Message message = new Message(message_from, message_to, chat_message);
                    messageList.add(message);
                }while(data.moveToNext());
            }
            mMessageListAdapter = new MessageListAdapter(this, messageList, mCurrentUser);
            messageListView.setAdapter(mMessageListAdapter);
            data.close();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }

    private void scrollDown() {
        messageListView.post(new Runnable() {
            @Override
            public void run() {
                messageListView.setSelection(mMessageListAdapter.getCount() - 1);
            }
        });
    }
}
