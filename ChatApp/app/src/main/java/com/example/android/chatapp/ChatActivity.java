package com.example.android.chatapp;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    public static final int TAKE_PHOTO_REQUEST_CODE = 0;
    public static final int TAKE_VIDEO_REQUEST_CODE = 1;
    public static final int PICK_PHOTO_REQUEST_CODE = 2;
    public static final int PICK_VIDEO_REQUEST_CODE = 3;

    private static final int MEDIA_TYPE_IMAGE = 4;
    private static final int MEDIA_TYPE_VIDEO = 5;

    protected Uri mMediaUri;

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0: //Take picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri == null) {
                        //Display an error
                        Toast.makeText(ChatActivity.this, getString(R.string.error_external_storage), Toast.LENGTH_LONG).show();
                    }else{
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA) ==
                                    PackageManager.PERMISSION_GRANTED){
                                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE);
                            }else {
                                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                    Toast.makeText(ChatActivity.this, "This app required access to camera", Toast.LENGTH_SHORT).show();
                                }
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO_REQUEST_CODE);
                            }

                        }else {
                            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE);
                        }
                    }
                    break;
                case 1: //Take video
                    break;

                case 2: //Choose picture
                    break;

                case 3: //Choose video
                    break;

            }
        }
    };

    private Uri getOutputMediaFileUri(int mediaType) {
        if(isExternalStorageAvailable()){
            //1. Get External Storage Directory
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getString(R.string.app_name));
            //2. Create subdirectory
            if(! mediaStorageDir.exists()){
                if(!mediaStorageDir.mkdirs()){
                    mediaStorageDir.mkdirs();
                    Log.e(TAG, "Failed to create directory. ");
                }
            }

            //3. Create a file name
            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            //path += mCurrentUser  + "_" + receiver + "_";
            if(mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }else if(mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(path + "VID_" + timestamp + ".mp4");
            }else {
                return null;
            }
            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
            return Uri.fromFile(mediaFile);
        }else {
            return null;
        }
    }

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = getSharedPreferences(getString(R.string.current_user), Context.MODE_PRIVATE);
        mCurrentUser = sharedPref.getString(ParseConstants.KEY_USERNAME, "");
        receiver = sharedPref.getString(ParseConstants.KEY_FRIEND, "");

        setTitle(receiver);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == TAKE_PHOTO_REQUEST_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Application will not run without camera services",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_logout:
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.remove(ParseConstants.KEY_USERNAME);
                navigateToLogin();
                return true;
            /*case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true; */
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
