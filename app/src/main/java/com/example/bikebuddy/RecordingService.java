package com.example.bikebuddy;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.bikebuddy.Utils.Workout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordingService extends Service {

    public static final String TAG = "RecordingService";


    //Values to be added for Workout
    //******************************************************************************************************
    private Date date;
    private ArrayList<Long> time;
    private ArrayList <Double> listHR;
    private ArrayList <Double> listSpeed;
    private double totalDistance;
    private long totalDuration;
    private double caloriesBurned;
    private double averageHR;
    private double averageSpeed;
    //******************************************************************************************************



    /*
    This handler creates a new thread every 10 seconds.
    Each thread fills the workout values
     */
    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 10000);
                fillWorkoutValues();
        }
    };


    public RecordingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");

        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID_RECORDING)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle(getString(R.string.notification_title_recording))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();

        //fillWorkoutValues();
        handler.post(periodicUpdate);

        startForeground(2,notification);


        return START_NOT_STICKY;


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    private void fillWorkoutValues()
    {
        Log.d(TAG,"Adding data values:" +
                  " Date: " + new Date()
                + " Elapsed Time (s): " + (SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000
                + " HR: " + MainActivity.HR_RT
                + " Speed: " + LocationService.SPEED_RT
                + " Distance " + LocationService.WORKOUT_DISTANCE);

        //Now we want to fill all the workout data
        //*****************************************************************************************************************************
        date = new Date();                                                                              //Current date
        time.add((SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000);         //Time in seconds
        listHR.add(MainActivity.HR_RT);                                                                 //Heart Rate list
        listSpeed.add(LocationService.SPEED_RT);                                                        //Speed list
        totalDistance = LocationService.WORKOUT_DISTANCE;                                               //Total Distance
        totalDuration = (SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000;   //Total Duration (seconds)
        caloriesBurned = 0;                                                                             //*TO DO*
        averageHR = 0;
        averageHR = 0;
        //*****************************************************************************************************************************
    }


}
