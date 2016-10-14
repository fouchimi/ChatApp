package com.example.android.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.chatapp.data.ChatContract;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;

    private TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                username = username.trim();
                password = password.trim();

                if(username.isEmpty() || password.isEmpty()){
                    showAlertDialog(getString(R.string.login_error_message));
                }else {
                    //Login
                    String selection = ChatContract.MemberEntry.COLUMN_USERNAME + " = ?" ;
                    String[] selectionArgs = new String[]{username};
                    Cursor mCursor = getBaseContext().getContentResolver().query(ChatContract.MemberEntry.CONTENT_URI,
                            new String[]{ChatContract.MemberEntry.COLUMN_USERNAME},
                            selection, selectionArgs, null);
                    if(mCursor == null){
                        Log.v(TAG, "Server Error");
                        showAlertDialog(getString(R.string.server_error));
                    }else if (mCursor.getCount() < 1) {
                        showAlertDialog(getString(R.string.empty_cursor));
                    }else{
                        if((mCursor.moveToFirst()) && mCursor.getCount() == 1){
                            String name = mCursor.getString(mCursor.getColumnIndex(ChatContract.MemberEntry.COLUMN_USERNAME));
                            //Toast.makeText(LoginActivity.this, name, Toast.LENGTH_LONG).show();
                            Log.d(TAG, name);
                            if(!name.isEmpty() && name != ""){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.current_user), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(ParseConstants.KEY_USERNAME, name);
                                editor.commit();
                                startActivity(intent);
                            }
                        }
                    }
                    mUsername.setText("");
                    mPassword.setText("");
                }

            }
        });
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message);
        builder.setTitle(R.string.login_error_title);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
