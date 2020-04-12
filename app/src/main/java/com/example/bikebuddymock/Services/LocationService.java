package com.example.bikebuddymock.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bikebuddymock.MainActivity;
import com.example.bikebuddymock.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class LocationService extends Service implements
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = "LocationService";

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

    public static LatLng lastKnownLatLng;
    public static double lastKnownLat;
    public static double lastKnownLng;
    //*****************************************************************************

    Context context;

    public LocationService() {
    }

    //First time we create our service
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        WORKOUT_DISTANCE = 0.0;

        mGoogleApiClient.connect();
        Log.d(TAG,"onCreate()");
    }

    //Every time we call start service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        if (mGoogleApiClient.isConnected()) {
            //createLocationRequest();
        }

        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID_LOCATION)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle(getString(R.string.notification_title_location))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();

        startForeground(1,notification);

        //.setStyle(new NotificationCompat.BigTextStyle()
        //                        .bigText("Speed:" + SPEED_RT + "\nHR: " + MainActivity.HR_RT + "\nDistance: " + WORKOUT_DISTANCE + " Clock: "))

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
    Stop the service when the app is removed from recent tasks
    https://stackoverflow.com/questions/53334235/how-to-properly-stop-a-foreground-service/53334788#53334788
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG,"onTaskRemoved called");
        super.onTaskRemoved(rootIntent);
        //do something you want
        //stop service
        this.stopSelf();
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
        Log.d(TAG,"onLocationChanged()");
        //Update speed and distance
        //**********************************************************************************************
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        lastKnownLat = location.getLatitude();
        lastKnownLng = location.getLongitude();

        updateValues(location);
        //incrementWorkoutDistance(location);
        Log.d(TAG,"Latitude: " + lastKnownLatLng.latitude + " Longitude" + lastKnownLatLng.longitude);
        Log.d(TAG,"Workout Distance: " + WORKOUT_DISTANCE);
        /*
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateTextViewSpeed();
                updateTextViewDistance();
            }
        });
         */
        //**********************************************************************************************
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    protected void createLocationRequest() {
        //Speed and Distance
        //****************************************************************************************************************
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //****************************************************************************************************************
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
            Log.d(TAG,"Speed: " + Float.toString(speed));


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
        TextView speedTextView = ((Activity) context).findViewById(R.id.text_speed_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (speedTextView != null)
        {
            speedTextView.setText(dec_0.format(SPEED_RT)); //Update the Heart Rate TextView (Real Time)
        }
    }

    private void updateTextViewDistance()
    {
        TextView distanceTextView = ((Activity) context).findViewById(R.id.text_distance_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (distanceTextView != null)
        {
            distanceTextView.setText(dec_0.format(WORKOUT_DISTANCE)); //Update the Heart Rate TextView (Real Time)
        }
    }

    public void createNotification()
    {
        Log.d(TAG,"createNotification()");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID_LOCATION)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle(getString(R.string.notification_title_location))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.notification_text_location)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(MainActivity.NOTIFICATION_ID_LOCATION,builder.build());
    }

}
