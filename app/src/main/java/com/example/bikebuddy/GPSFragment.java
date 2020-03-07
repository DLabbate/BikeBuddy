package com.example.bikebuddy;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


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
    double curTime= 0;
    double oldLat = 0.0;
    double oldLon = 0.0;
    double workout_distance = 0.0;
    long EARTH_RADIUS = 6356 *1000;

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

        Log.d(TAG,"oncreate distance: " + workout_distance);
        workout_distance = 0.0;

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
        Log.d(TAG,"Workout Distance: " + workout_distance);

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
    We want to get the speed and distance of a workout.
    This can be done using Latitude and Longitude coordinates
    //https://stackoverflow.com/questions/20398898/how-to-get-speed-in-android-app-using-location-or-accelerometer-or-some-other-wa
     */
    private void updateValues(Location location){

        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        if(location.hasSpeed()) {
            float speed = location.getSpeed();
            Toast.makeText(getActivity(), "SPEED : " + String.valueOf(speed) + "m/s", Toast.LENGTH_SHORT).show();

            double distance = calculationBydistance(newLat, newLon, oldLat, oldLon);
            Log.d(TAG, "DELTA DISTANCE: " + distance);


            if ((System.currentTimeMillis() - lastTimeMillis) > DURATION_SAMPLING_TIME)
            {
                Toast.makeText(getActivity(),"DISTANCE: " + workout_distance,Toast.LENGTH_SHORT).show();
                float[] distanceResults = new float[1];
                Location.distanceBetween(oldLat, oldLon,
                        newLat, newLon, distanceResults);

                Log.d(TAG, "DISTANCE BETWEEN: " + distanceResults[0]);
                /*
                We update distance every sample of time to avoid inaccuracies in the GPS
                 */
                if (oldLon != 0 && oldLat != 0)
                {
                    if (distanceResults[0] > 1.0)
                    {
                        workout_distance += distanceResults[0];
                    }
                }
                oldLat = newLat;
                oldLon = newLon;
                lastTimeMillis = System.currentTimeMillis();
            }
        }

        /*
        else
            {
            double distance = calculationBydistance(newLat,newLon,oldLat,oldLon);
            Log.d(TAG,"DELTA DISTANCE: " + distance);


                float[] distanceResults = new float[1];
                Location.distanceBetween(oldLat, oldLon,
                        newLat, newLon, distanceResults);

                Log.d(TAG,"DISTANCE BETWEEN: " + distanceResults[0]);

            if (oldLon != 0 && oldLat != 0)
                workout_distance += distanceResults[0];
            double timeDifferent = newTime - curTime;
            double speed = distance/timeDifferent;
            curTime = newTime;
            oldLat = newLat;
            oldLon = newLon;
            Toast.makeText(getActivity(),"SPEED 2 : "+String.valueOf(speed)+"m/s",Toast.LENGTH_SHORT).show();


        }

         */
    }


    public double calculationBydistance(double lat1, double lon1, double lat2, double lon2){
        double radius = EARTH_RADIUS;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return radius * c;
    }

    private void incrementWorkoutDistance(Location location)
    {
        double lat2 = location.getLatitude();
        double lon2 = location.getLongitude();

        double delta = calculationBydistance(oldLat,oldLon,lat2,lon2);
        oldLat = lat2; //Update old values
        oldLon = lon2;
        workout_distance += delta;
    }



}
