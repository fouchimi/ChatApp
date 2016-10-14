package com.example.android.chatapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.chatapp.data.ChatContract;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email  = mEmail.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();

                if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
                   showAlertDialog(getString(R.string.signup_error_message));
                }else {
                    // Create new user
                    //Check for duplicates username
                    Cursor mCursor = getUsernameCursor(username);
                    if(mCursor == null) {
                        showAlertDialog(getString(R.string.server_error));
                        Log.v(TAG, "server error");
                    }else if(mCursor.getCount() ==  1){
                        showAlertDialog(getString(R.string.duplicate_username));
                        Log.v(TAG, "No matches found");
                    }else {
                        ContentValues memberValue = new ContentValues();
                        memberValue.put(ChatContract.MemberEntry.COLUMN_USERNAME, username);
                        memberValue.put(ChatContract.MemberEntry.COLUMN_PASSWORD, password);
                        memberValue.put(ChatContract.MemberEntry.COLUMN_EMAIL, email);
                        memberValue.put(ChatContract.MemberEntry.COLUMN_DATE, new Date().toString());
                        getBaseContext().getContentResolver().insert(ChatContract.MemberEntry.CONTENT_URI, memberValue);
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //Toast.makeText(SignUpActivity.this, username, Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.current_user), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(ParseConstants.KEY_USERNAME, username);
                        editor.commit();
                        startActivity(intent);
                    }
                }

            }
        });
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setMessage(message);
        builder.setTitle(R.string.login_error_title);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Cursor getUsernameCursor(String username) {
        String selection = ChatContract.MemberEntry.COLUMN_USERNAME + " = ?" ;
        String[] selectionArgs = new String[]{username};
        Cursor mCursor = getBaseContext().getContentResolver().query(ChatContract.MemberEntry.CONTENT_URI,
                new String[]{ChatContract.MemberEntry.COLUMN_USERNAME},
                selection, selectionArgs, null);

        return mCursor;
    }
}

