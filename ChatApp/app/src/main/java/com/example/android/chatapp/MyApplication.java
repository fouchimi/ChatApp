package com.example.android.chatapp;

/**
 * Created by ousma on 10/2/2016.
 */

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;


public class MyApplication extends Application {

    /*@Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("LHkNxyEd8sT=QmUtNxyEdsBJaUv")
                .clientKey(null)
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("http://linkedsocial.herokuapp.com/parse/")
                .build()
        );


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        //defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        Stetho.initializeWithDefaults(this);

    } */

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

    public Tracker mTracker;

    // Get the tracker associated with this app
    public void startTracking() {

        // Initialize an Analytics tracker using a Google Analytics property ID.

        // Does the Tracker already exist?
        // If not, create it

        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);

            // Get the config data for the tracker
            mTracker = ga.newTracker(R.xml.track_app);

            // Enable tracking of activities
            ga.enableAutoActivityReports(this);

            // Set the log level to verbose.
            ga.getLogger()
                    .setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker() {
        // Make sure the tracker exists
        startTracking();

        // Then return the tracker
        return mTracker;
    }
}
