package com.example.bikebuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.location.Location;

/*
Replace "import android.location.Location" with "import com.google.android.gms.location.LocationListener"
See https://stackoverflow.com/questions/28954421/android-cannot-resolve-method-requestlocationupdates-fusedlocationproviderapi
For more details
import android.location.LocationListener;
 */

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import java.text.DecimalFormat;



public class GPSFragment extends Fragment implements
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "GPSfragment";
    private Context mContext;


    MapView gMapView;
    private GoogleMap gMap = null;
    FusedLocationProviderClient fusedLocationClient;
    private Location mLastKnownLocation;

    private final float DEFAULT_ZOOM = 15;

    //Location Request Services (for real time map updates)
    //******************************************************************************
    LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    LatLng lastKnownLatLng;

    private LocationCallback locationCallback;
    //******************************************************************************

    //The following fields are used for computing speed and distance
    //*****************************************************************************
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 1000;
    double oldLat = 0.0;
    double oldLon = 0.0;
    public static double WORKOUT_DISTANCE = 0.0;
    public static double SPEED_RT; //Speed (km/h)
    long DURATION_SAMPLING_TIME = 10000; //Sample the distance every 10 seconds
    long lastTimeMillis = System.currentTimeMillis();
    //*****************************************************************************

    //Google Directions Api
    private GeoApiContext mGeoApiContext = null;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps,container,false);
        Log.d(TAG, ": ONcreateview");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null){
                    return;
                }
                for (Location location: locationResult.getLocations()){
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                }
            }
        };



        gMapView = (MapView) view.findViewById(R.id.mapView2);

        gMapView.onCreate(savedInstanceState);
        gMapView.onResume();

        gMapView.getMapAsync(this);

        if (mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_API_key))
                    .build();
        }


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Log.d(TAG,"oncreate distance: " + WORKOUT_DISTANCE);
        WORKOUT_DISTANCE = 0.0;

        //mLastKnownLocation =


        return view;

    }

    /**
     *Callback interface for when map is ready to be used
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setMyLocationEnabled(true);
        //gMap.setOnMyLocationButtonClickListener(this);
        //gMap.setOnMyLocationClickListener(this);

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                calculateDirections(marker);
                System.out.println("*****************ON MARKER CLICK");
                return false;
            }
        });
        gMap.getUiSettings().setMapToolbarEnabled(false);

        getDeviceLocation();


        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                gMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                gMap.addMarker(markerOptions);

                Marker marker = gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(latLng.latitude + " : " + latLng.longitude));

            }
        });

    }



    /*
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
     */

    /**
     *Retrieves the device's location
     */
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation() method");
        try {
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                //Sets map's position to location of device
                                mLastKnownLocation = task.getResult();
                                if (mLastKnownLocation != null) {
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                }
                                else{
                                    Log.d(TAG, "Current location is null");
                                    Log.e(TAG, "Exception: ", task.getException());
                                }

                            }
                        }
                    });
        } catch (SecurityException e){
            Log.e("Exception: ", e.getMessage());
            }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(getActivity(),"MyLocationButtonClicked", Toast.LENGTH_SHORT).show();
        return false;
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
        startLocationUpdates();
    }

    private void startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
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
        //Update Map
        //**********************************************************************************************
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLatLng));
        Toast.makeText(getActivity(), "Current location:\n" + lastKnownLatLng, Toast.LENGTH_LONG).show();
        //**********************************************************************************************

        //Update speed and distance
        //**********************************************************************************************
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
        //**********************************************************************************************
    }

    /**
     *Sets location request parameters
     */
    protected void createLocationRequest() {

        //Map
        //*******************************************************************************
        Log.d(TAG,"createLocationRequest method");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //*******************************************************************************

        //Speed and Distance
        //****************************************************************************************************************
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //****************************************************************************************************************
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

    /**
     *Retrieves route data from google maps from user location to destination (based on marker)
     */
    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(

                        //mLastKnownLocation.getGeo_point().getLatitude(),
                        //mLastKnownLocation.getGeo_point().getLongitude()
                        //if (lastKnownLatLng.latitude != null && lastKnownLatLng.longitude != null) {
                            mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()

                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

}
