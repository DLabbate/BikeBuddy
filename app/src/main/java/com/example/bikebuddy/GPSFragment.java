package com.example.bikebuddy;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;



public class GPSFragment extends Fragment implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "GPSfragment";


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

    //The following fields are used for onLocationChanged
    //*****************************************************************************
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 1000;
    //*****************************************************************************



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


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return view;

    }

    /**
     *Callback interface for when map is ready to be used
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);

        getDeviceLocation();

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
        //mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        //stopLocationUpdates();
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

        Log.d(TAG,"onLocationChanged");

        //Update Map
        //**********************************************************************************************
        LatLng myCoordinates = new LatLng(location.getLatitude(),location.getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
        //Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
        //**********************************************************************************************

        //Update speed and distance UI
        //**********************************************************************************************
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

    private void updateTextViewSpeed()
    {
        TextView speedTextView = getActivity().findViewById(R.id.text_speed_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (speedTextView != null)
        {
            Log.d(TAG,"updateTextViewSpeed: " + LocationService.SPEED_RT);
            speedTextView.setText(dec_0.format(LocationService.SPEED_RT)); //Update the Heart Rate TextView (Real Time)
        }
    }

    private void updateTextViewDistance()
    {
        TextView distanceTextView = getActivity().findViewById(R.id.text_distance_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (distanceTextView != null)
        {
            Log.d(TAG,"updateTextViewDistance: " + LocationService.WORKOUT_DISTANCE);
            distanceTextView.setText(dec_0.format(LocationService.WORKOUT_DISTANCE)); //Update the Heart Rate TextView (Real Time)
        }
    }



}
