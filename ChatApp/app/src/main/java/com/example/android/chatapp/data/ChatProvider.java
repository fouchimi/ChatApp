/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.chatapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ChatProvider extends ContentProvider {
    private static final String TAG = ChatProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static  ChatDbHelper mOpenHelper;

    static final int MEMBER = 100;
    static final int MEMBERS = 200;
    static final int FRIENDS = 300;
    static final int CHATS = 400;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ChatContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ChatContract.PATH_MEMBER, MEMBERS);
        matcher.addURI(authority, ChatContract.PATH_FRIENDS, FRIENDS);
        matcher.addURI(authority, ChatContract.PATH_MEMBER + "/*", MEMBER);
        matcher.addURI(authority, ChatContract.PATH_CHATS, CHATS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ChatDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MEMBER:
                return ChatContract.MemberEntry.CONTENT_TYPE;
            case MEMBERS:
                return ChatContract.MemberEntry.CONTENT_TYPE;
             case FRIENDS:
                return ChatContract.FriendsEntry.CONTENT_TYPE;
            case CHATS:
                return ChatContract.ChatsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MEMBERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ChatContract.MemberEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FRIENDS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ChatContract.FriendsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case CHATS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ChatContract.ChatsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
       final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MEMBER: {
                long _id = db.insert(ChatContract.MemberEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ChatContract.MemberEntry.buildMemberUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MEMBERS : {
                long _id = db.insert(ChatContract.MemberEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ChatContract.MemberEntry.buildMemberUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FRIENDS : {
                long _id = db.insert(ChatContract.FriendsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ChatContract.FriendsEntry.buildFriendsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CHATS : {
                long _id = db.insert(ChatContract.ChatsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ChatContract.ChatsEntry.buildChatsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case FRIENDS:
                rowsDeleted = db.delete(
                        ChatContract.FriendsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return -1;
    }

    @Override
    public int update(
           Uri uri, ContentValues values, String selection, String[] selectionArgs) {
         final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CHATS:
                rowsUpdated = db.update(ChatContract.ChatsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FRIENDS:
                rowsUpdated = db.update(ChatContract.FriendsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
       /* final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }*/
        return 0;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }


}