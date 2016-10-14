
package com.example.android.chatapp.data;

import com.example.android.chatapp.data.ChatContract.MemberEntry;
import com.example.android.chatapp.data.ChatContract.ChatsEntry;
import com.example.android.chatapp.data.ChatContract.FriendsEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "chats.db";

    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MEMBER_TABLE = "CREATE TABLE " + MemberEntry.TABLE_NAME + " (" +
                MemberEntry._ID + " INTEGER PRIMARY KEY, " +
                MemberEntry.COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                MemberEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                MemberEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                MemberEntry.COLUMN_DATE + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_FRIENDS_TABLE = "CREATE TABLE " + FriendsEntry.TABLE_NAME + " (" +

                FriendsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FriendsEntry.COLUMN_MEMBER_KEY + " INTEGER NOT NULL, " +
                FriendsEntry.COLUMN_FROM + " TEXT NOT NULL, " +
                FriendsEntry.COLUMN_TO + " TEXT NOT NULL," +
                FriendsEntry.COLUMN_DATE + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + FriendsEntry.COLUMN_MEMBER_KEY + ") REFERENCES " +
                MemberEntry.TABLE_NAME + " (" + MemberEntry._ID + ")" + ")";

        final String SQL_CREATE_CHATS_TABLE = "CREATE TABLE " + ChatsEntry.TABLE_NAME + " (" +

                ChatsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatsEntry.COLUMN_MEMBER_KEY + " INTEGER NOT NULL, " +
                ChatsEntry.COLUMN_FROM + " TEXT NOT NULL, " +
                ChatsEntry.COLUMN_TO + " TEXT NOT NULL," +
                ChatsEntry.COLUMN_CHAT_MESSAGE + " TEXT NOT NULL," +
                ChatsEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                ChatsEntry.COLUMN_LATEST + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + FriendsEntry.COLUMN_MEMBER_KEY + ") REFERENCES " +
                MemberEntry.TABLE_NAME + " (" + MemberEntry._ID + ")" + ")";

        sqLiteDatabase.execSQL(SQL_CREATE_MEMBER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIENDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CHATS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MemberEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FriendsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ChatsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
