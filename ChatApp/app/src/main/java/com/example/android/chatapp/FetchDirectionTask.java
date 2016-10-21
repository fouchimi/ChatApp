package com.example.android.chatapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by ousma on 10/20/2016.
 */

public class FetchDirectionTask extends AsyncTask<String, Void, Coordinate> {

    private static final String TAG = FetchDirectionTask.class.getSimpleName();
    private Context mContext;
    private JSONObject jObject;
    private Coordinate coordinate = null;
    private String mode;

    public FetchDirectionTask(Context context, String mode){
        this.mContext = context;
        this.mode = mode;
    }

    @Override
    protected Coordinate doInBackground(String... params) {
        String result =  new HttpManager().getData(params[0]);
        try {
            jObject = new JSONObject(result);
            Log.d("ParserTask",result);
            DataParser parser = new DataParser();
            Log.d("ParserTask", parser.toString());
            coordinate = parser.parseCoordinates(jObject);

        } catch (Exception e) {
            Log.d("ParserTask",e.toString());
            e.printStackTrace();
        }
        return coordinate;
    }

    @Override
    protected void onPostExecute(Coordinate coordinate) {
        super.onPostExecute(coordinate);
        if(coordinate != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.travel_info));
            builder.setMessage("Distance: " + coordinate.getDistance() + "\n" +
                    "Duration: " + coordinate.getDuration() + "\n" +
                    "Travel Mode: " + mode.toUpperCase());
            builder.setPositiveButton(mContext.getString(R.string.ok), null);
            builder.create().show();
        }

    }

    public interface Listener {
        void onCompleteFinished(Coordinate coordinate);
    }
}
