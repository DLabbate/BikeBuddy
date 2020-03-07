package com.example.bikebuddy;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;


public class GPSFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "GPSfragment";
    private static final long INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 1000;

    MapView gMapView;
    private GoogleMap gMap = null;


    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    LatLng lastKnownLatLng;

    double oldLat = 0.0;
    double oldLon = 0.0;
    public static double WORKOUT_DISTANCE = 0.0;
    public static double SPEED_RT; //Speed (km/h)
    long DURATION_SAMPLING_TIME = 10000; //Sample the distance every 10 seconds
    long lastTimeMillis = System.currentTimeMillis();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps,container,false);

        Log.d(TAG, ": ONcreateview");
        gMapView = (MapView) view.findViewById(R.id.mapView2);

        gMapView.onCreate(savedInstanceState);
        gMapView.onResume();

        gMapView.getMapAsync(this);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Log.d(TAG,"oncreate distance: " + WORKOUT_DISTANCE);
        WORKOUT_DISTANCE = 0.0;

        return view;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49,-124), 20));


        googleMap.setMyLocationEnabled(true);


    }


    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation() method");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if (mGoogleApiClient.isConnected()) {
            createLocationRequest();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        updateValues(location);
        //incrementWorkoutDistance(location);
        Log.d(TAG,"Latitude: " + lastKnownLatLng.latitude + " Longitude" + lastKnownLatLng.longitude);
        Log.d(TAG,"Workout Distance: " + WORKOUT_DISTANCE);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateTextViewSpeed();
                updateTextViewDistance();

            }
        });

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /*
    This method updates the values (speed and distance in real time)
    Note that the GPS sampling is done every 10 seconds and only updated if it is greater than 1 metre to minimize inaccuracies
    This can be done using Latitude and Longitude coordinates
    For more information, see:
    //https://stackoverflow.com/questions/20398898/how-to-get-speed-in-android-app-using-location-or-accelerometer-or-some-other-wa
     */
    private void updateValues(Location location)
    {
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();

        //Check if the location has a speed
        if(location.hasSpeed()) {
            float speed = location.getSpeed();
            SPEED_RT = speed * 3.6; //Convert to km/h
            Log.d(TAG,Float.toString(speed));


            /*
                We update distance every sample of time to avoid inaccuracies in the GPS
                Check if more than 10 seconds has elapsed
            */
            if ((System.currentTimeMillis() - lastTimeMillis) > DURATION_SAMPLING_TIME)
            {
                /*
                The following few lines calculates distance between old location and new location
                using latitude and longitudes
                 */
                float[] distanceResults = new float[1];
                Location.distanceBetween(oldLat, oldLon,
                        newLat, newLon, distanceResults);

                Log.d(TAG, "Distance between old location and new location: " + distanceResults[0]);


                if (oldLon != 0 && oldLat != 0)
                {
                    if (distanceResults[0] > 1.0) //Only update if distance is greater than a metre
                    {
                        WORKOUT_DISTANCE += distanceResults[0]; //Increment distance
                    }
                }
                oldLat = newLat;
                oldLon = newLon;
                lastTimeMillis = System.currentTimeMillis();
            }
        }
    }

    private void updateTextViewSpeed()
    {
        TextView speedTextView = getActivity().findViewById(R.id.text_speed_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (speedTextView != null)
        {
            speedTextView.setText(dec_0.format(SPEED_RT)); //Update the Heart Rate TextView (Real Time)
        }
    }

    private void updateTextViewDistance()
    {
        TextView distanceTextView = getActivity().findViewById(R.id.text_distance_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (distanceTextView != null)
        {
            distanceTextView.setText(dec_0.format(WORKOUT_DISTANCE)); //Update the Heart Rate TextView (Real Time)
        }
    }

}
