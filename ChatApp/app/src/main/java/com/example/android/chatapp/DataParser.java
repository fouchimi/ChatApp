package com.example.android.chatapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ousma on 10/5/2016.
 */

public class DataParser {

    public List<Place> parse(JSONObject jObject){

        List<Place> places = new ArrayList<>() ;
        JSONArray jsonArray = null;
        try {
            jsonArray = jObject.getJSONArray("results");
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject objectItem = ( (JSONObject)jsonArray.get(i)).getJSONObject("geometry");
                JSONObject jsonLocation = objectItem.getJSONObject("location");
                String latitude = jsonLocation.getString("lat");
                String longitude = jsonLocation.getString("lng");
                String name = ((JSONObject)jsonArray.get(i)).getString("name");
                String placeId = ((JSONObject)jsonArray.get(i)).getString("place_id");
                places.add(new Place(name, placeId, latitude, longitude));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return places;
    }

    public Coordinate parseCoordinates(JSONObject jObject){

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        String duration="";
        String distance="";
        try {
            jRoutes = jObject.getJSONArray("routes");
            for(int i= 0; i < jRoutes.length(); i++) {
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                for(int j=0;j<jLegs.length();j++){
                    distance =  ( (JSONObject)jLegs.get(j)).getJSONObject("distance").getString("text");
                    duration =  ( (JSONObject)jLegs.get(j)).getJSONObject("duration").getString("text");
                    return new Coordinate(distance, duration);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e) {

        }
        return null;
    }


}
