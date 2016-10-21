package com.example.android.chatapp;

/**
 * Created by ousma on 10/20/2016.
 */

public class Place {

    private String name;
    private String latitude;
    private String longitude;
    private String placeId;

    public Place(String name, String placeId, String latitude, String longitude) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId(){
        return placeId;
    }

    public void setPlaceId(String placeId){
        this.placeId = placeId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
