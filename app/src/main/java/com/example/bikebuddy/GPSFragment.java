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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.bikebuddy.Models.PolylineData;
import com.example.bikebuddy.Services.LocationService;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class GPSFragment extends Fragment implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnPolylineClickListener {

    public static final String TAG = "GPSfragment";

    private ImageButton cameraUpdatesButton;
    private MapView gMapView;
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

    //Google Directions Api
    private GeoApiContext mGeoApiContext = null;
    private Marker marker;

    //Flag for camera updates after Start button is clicked
    private boolean cameraUpdates = false;

    //Array list of polyline data for every polyline shown on google map (is reset every time a new marker is added)
    private ArrayList<PolylineData> mPolylines = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps, container, false);
        Log.d(TAG, ": ONcreateview");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                    }
                }
            };

        cameraUpdatesButton = view.findViewById(R.id.cameraUpdatesButton);
        cameraUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cameraUpdates){
                    cameraUpdates = true;
                    Toast.makeText(getActivity(),"Real time Updates: Enabled", Toast.LENGTH_SHORT).show();
                    cameraUpdatesButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                }
                else{
                    cameraUpdates = false;
                    Toast.makeText(getActivity(),"Real time Updates: Disabled", Toast.LENGTH_SHORT).show();
                    cameraUpdatesButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_light ));
                }
            }
        });

        gMapView = view.findViewById(R.id.mapView2);

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

        return view;

    }

    /**
     *Callback interface for when map is ready to be used
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnPolylineClickListener(this);

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

                marker = gMap.addMarker(new MarkerOptions()
                .position(latLng));
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
        if (cameraUpdates) {
            startLocationUpdates();
        }
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
        if (cameraUpdates) {
            lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            gMap.moveCamera(CameraUpdateFactory.newLatLng(lastKnownLatLng));
            mLastKnownLocation = location;

            //Toast.makeText(getActivity(), "Current location:\n" + lastKnownLatLng, Toast.LENGTH_LONG).show();
        }
        //**********************************************************************************************

        //Update speed and distance UI
        //**********************************************************************************************
        if (getActivity()==null){
            return;
        }

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

        directions.mode(TravelMode.BICYCLING);
        directions.alternatives(true);

        directions.origin(
                new com.google.maps.model.LatLng(

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

                addPolylinesToMap(result);
            }


            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){

        //******Posts to main thread********
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                //Ensures that the current polylines array list contains only those shown on the map
                if (mPolylines.size() > 0){
                    for (PolylineData polylineData: mPolylines){
                        polylineData.getPolyine().remove();
                    }
                    mPolylines.clear();
                    mPolylines = new ArrayList<>();
                }

                double duration = 99999999;

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg1: " + route.legs[0].toString());
                    
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = gMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.gpsRoute_lightgrey));
                    polyline.setClickable(true);
                    mPolylines.add(new PolylineData(polyline,route.legs[0]));

                    double tmp = route.legs[0].duration.inSeconds;

                    if(tmp < duration){

                        duration = tmp;
                        onPolylineClick(polyline);
                        focusCamera(polyline.getPoints());

                    }


                }
            }
        });
    }

    /**
     *Highlights the selected polyline and places it over other polyline routes
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        polyline.setColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryDark));


        for (PolylineData polylineData: mPolylines){
            Log.d(TAG, "onPolylineClick: " + mPolylines.toString());
            if(polyline.getId().equals(polylineData.getPolyine().getId())){
                polylineData.getPolyine().setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                polylineData.getPolyine().setZIndex(1);

                LatLng endlocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                marker.setPosition(endlocation);
                marker.setTitle(polylineData.getLeg().endAddress);
                marker.setSnippet("Duration: " + polylineData.getLeg().duration +
                        ",  Distance: " + polylineData.getLeg().distance);
                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyine().setColor(ContextCompat.getColor(getActivity(), R.color.gpsRoute_lightgrey));
                polylineData.getPolyine().setZIndex(0);
            }
        }
    }

    /**
     *Zooms the camera on a route
     */
    public void focusCamera(List<LatLng> LatLngRoute) {

        if (gMap == null || LatLngRoute == null || LatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : LatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 400;
        LatLngBounds latLngBounds = boundsBuilder.build();

        gMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }
}
