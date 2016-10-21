package com.example.android.chatapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by ousma on 10/20/2016.
 */

public class FetchPlacesTask extends AsyncTask<String, Void, List<Place>> {

    private final static String TAG = FetchPlacesTask.class.getSimpleName();
    private Context mContext;
    JSONObject jObject;
    private List<Place> placeList = null;

    public FetchPlacesTask(Context context) {
        mContext = context;
    }

    @Override
    protected List<Place> doInBackground(String... params) {

        String result =  new HttpManager().getData(params[0]);
        try {
            jObject = new JSONObject(result);
            Log.d("ParserTask",result);
            DataParser parser = new DataParser();
            Log.d("ParserTask", parser.toString());
            placeList = parser.parse(jObject);

        } catch (Exception e) {
            Log.d("ParserTask",e.toString());
            e.printStackTrace();
        }
        return placeList;
    }

    @Override
    protected void onPostExecute(List<Place> places) {
        super.onPostExecute(places);
        //Toast.makeText(mContext, "" + places.size(), Toast.LENGTH_LONG).show();
        if(places != null && places.size() > 0) {
            MapActivity activity = (MapActivity) mContext;
            activity.onComplete(places);
        }

    }

    public interface Listener {
        void onComplete(List<Place> placeList);
    }
}
