package com.example.android.chatapp;

/**
 * Created by ousma on 10/20/2016.
 */

public class Coordinate {
    private String distance;
    private String duration;

    public Coordinate(String distance, String duration){
        this.distance = distance;
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
