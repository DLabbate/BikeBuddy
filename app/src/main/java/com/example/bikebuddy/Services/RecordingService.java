package com.example.bikebuddy.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.FitnessFragment;
import com.example.bikebuddy.MainActivity;
import com.example.bikebuddy.R;
import com.example.bikebuddy.Services.LocationService;
import com.example.bikebuddy.Utils.Workout;

import java.util.ArrayList;
import java.util.Date;

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

    private Workout workout;
    //******************************************************************************************************


    //DB
    //******************************************************************************************************
    DbHelper dbHelper;
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
        Log.d(TAG,"RecordingService Constructor");
        this.date = new Date();
        this.time = new ArrayList<>();
        this.listHR = new ArrayList<>();
        this.listSpeed = new ArrayList<>();
        this.totalDistance = 0;
        this.totalDuration = 0;
        this.caloriesBurned = 0;
        this.averageHR = 0;
        this.averageSpeed = 0;

        //Initialize DbHelper
        dbHelper = new DbHelper(this);
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
        createNewWorkout();
    }

    /*
    This method keeps an ongoing list of values WHILE A WORKOUT IS BEING RECORDED
     */
    private void fillWorkoutValues()
    {
        Log.d(TAG,"Adding data values (" +
                  " Date: " + new Date()
                + " Elapsed Time (s): " + (SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000
                + " HR: " + MainActivity.HR_RT
                + " Speed: " + LocationService.SPEED_RT
                + " Distance " + LocationService.WORKOUT_DISTANCE
                + " )");

        //Now we want to fill all the workout data
        //*****************************************************************************************************************************
        time.add((SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000);         //Time in seconds
        listHR.add(MainActivity.HR_RT);                                                                 //Heart Rate list
        listSpeed.add(LocationService.SPEED_RT);                                                        //Speed list
        totalDistance = LocationService.WORKOUT_DISTANCE;                                               //Total Distance
        totalDuration = (SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000;   //Total Duration (seconds)
        //*****************************************************************************************************************************
    }

    /*
    This method creates a new workout and then logs the values
    Check the logcat for debugging
     */
    private void createNewWorkout()
    {
        Log.d(TAG,"createNewWorkout");
        workout = new Workout(time,listHR,listSpeed,totalDistance,totalDuration);
        workout.print(TAG);

        //Add the workout to the DB;
        dbHelper.insertWorkout(workout);
    }


}
