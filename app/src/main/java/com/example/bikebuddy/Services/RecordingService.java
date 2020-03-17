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


    //Values for Kalman filter
    //******************************************************************************************************
    private double[] kalReturn = new double[2];     //containts estimate and deviation from kalman filter
    double C = 1.5;                     //Kalman filter parameter
    double Q = 0.5;                     //Kalman filter parameter
    private int initialRate = 1;        //1=first time getting estimate, 0=first estimate complete
    private double calRateEstimate = 0; //current best approximation of rate
    private double sigma = Q;           //Variable Kalman filter parameter
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
        
        //Stop the periodic update
        handler.removeCallbacks(periodicUpdate);
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

        //Here we calculate the caloric output
        //*****************************************************************************************************************************
            kalReturn = calculateCalorieRate(MainActivity.HR_RT, LocationService.SPEED_RT, calRateEstimate, sigma, Q, C);
            calRateEstimate = kalReturn[0];
            sigma = kalReturn[1];
            //TODO: insert calRateEstimate into workout on completion of recording
        //*****************************************************************************************************************************

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

    private double[] calculateCalorieRate(double heartRate, double speed, double estimate, double sigma, double Q, double C){
        /*
        Returns value of best guess for power output. Uses velocity data to approximate power
        based on MET standards and then HR to approximate output based on Keytel approximation.
        The MET standards typically provide approximations that are conservatively large. Keytel
        approximation will be used to bring the output to a more appropriate level (matching
        clinical trials).
         */

        //*****************************************************************************************************************************
        //TODO: THIS DATA NEEDS TO BE TAKEN FROM PROFILE
        //      hardcoded for now
        int age = 27;           //years
        int weight = 95;        //kg
        Boolean gender = true;  //true = male, false = female
        //*****************************************************************************************************************************

        //Internal Parameters
        double deltaTime = 10;
        double K;
        double MET;
        double newVal;
        double[] kalReturn;

        //Calculating MET level
        if(speed < 11) MET = 4.8;
        else if(speed <16) MET = 5.9;
        else if(speed <21) MET = 7.1;
        else if(speed <26) MET = 8.4;
        else MET = 9.8;

        //*****************************************************************************************************************************
        //Performing the filter approximation.
        //  assume keytelPower only
        newVal = ((-55.0969 + (0.6309*heartRate) + (0.1988*weight) + (0.2017*age))/4.184)*(deltaTime)*(1/60);  //kcal/min
        K = (sigma * C)/(sigma*sigma * C*C + Q*Q);
        estimate = estimate + K*(newVal-C*estimate);
        sigma = (1-K*C)*sigma;

        //  update with metPower
        newVal = MET*weight/60;     //kcal/min
        K = (sigma * C)/(sigma*sigma * C*C + Q*Q);
        estimate = estimate + K*(newVal-C*estimate);
        sigma = (1-K*C)*sigma;
        //*****************************************************************************************************************************

        //Returning estimate and deviation, both used at next method call
        kalReturn = new double[]{estimate, sigma};
        return kalReturn;

    }

}
