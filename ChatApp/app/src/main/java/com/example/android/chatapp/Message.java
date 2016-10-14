package com.example.android.chatapp;

/**
 * Created by ousma on 10/10/2016.
 */

public class Message {
    private String currentUser;
    private String receiver;
    private String message;

    public Message(){}

    public Message(String currentUser, String receiver, String message) {
        this.currentUser = currentUser;
        this.receiver = receiver;
        this.message = message;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
