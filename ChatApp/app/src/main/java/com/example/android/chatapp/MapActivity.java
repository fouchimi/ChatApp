package com.example.android.chatapp;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        FetchPlacesTask.Listener, GoogleMap.OnMarkerClickListener,
        FetchDirectionTask.Listener{

    private static final String TAG = MapActivity.class.getSimpleName();
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LatLng mLatLng;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String BASE_DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private Coordinate mCoordinate;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        radioGroup = (RadioGroup) findViewById(R.id.rg_modes);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                StringBuilder builder = new StringBuilder(BASE_URL);
                builder.append("location=" + mCurrentLocation.getLatitude()+"," + mCurrentLocation.getLongitude());
                builder.append("&types=" + query.trim().toLowerCase());
                builder.append("&radius=3000");
                builder.append("&key="+ ParseConstants.API_KEY);
                String url = builder.toString();
                //Log.d("URL", url);
                FetchPlacesTask task = new FetchPlacesTask(MapActivity.this);
                task.execute(url);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    mGoogleMap.clear();
                    getCurrentLocation();
                }
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        Tracker tracker = ((MyApplication) getApplication()).getTracker();
        // Set screen name
        tracker.setScreenName(getString(R.string.mapActivityScreenName));

        //Send a screen view
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        mLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions()
                .position(mLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(getString(R.string.you_are_here)));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mGoogleMap == null) {
            mGoogleMap = googleMap;
            mGoogleMap.setOnMarkerClickListener(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onComplete(List<Place> placeList) {
        for(Place place : placeList){
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(place.getLatitude()), Double.valueOf(place.getLongitude())))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(place.getName()));
        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        StringBuilder builder = new StringBuilder(BASE_DIRECTION_URL);
        builder.append("origin=" + mCurrentLocation.getLatitude()+"," + mCurrentLocation.getLongitude());
        builder.append("&destination=" + marker.getPosition().latitude +"," + marker.getPosition().longitude);
        builder.append("&mode=" + getTravelMode().toLowerCase());
        builder.append("&key="+ ParseConstants.API_KEY);
        String url = builder.toString();
        FetchDirectionTask task = new FetchDirectionTask(this, getTravelMode());
        task.execute(url);
        return false;
    }

    @Override
    public void onCompleteFinished(Coordinate coordinate) {
        mCoordinate = coordinate;
    }

    private String getTravelMode(){
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton)  radioGroup.getChildAt(idx);
        return r.getText().toString();
    }
}
