package com.example.bikebuddy.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.FitnessFragment;
import com.example.bikebuddy.LoginActivity;
import com.example.bikebuddy.MainActivity;
import com.example.bikebuddy.R;
import com.example.bikebuddy.SharedPreferenceHelper;
import com.example.bikebuddy.Utils.Workout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordingService extends Service {

    public static final String TAG = "RecordingService";
    private static final int delay_seconds = 3;
    //Values to be added for Workout
    //******************************************************************************************************
    private Date date;
    private ArrayList<Long> time;
    private ArrayList <Double> listHR;
    private ArrayList <Double> listSpeed;
    private double totalDistance;
    private long totalDuration;
    private double caloriesRate;
    private double averageHR;
    private double averageSpeed;

    private Workout workout;
    //******************************************************************************************************


    //Values for Kalman filter
    //******************************************************************************************************
    private double[] kalReturn = new double[2];     //containts estimate and deviation from kalman filter
    double C = 1.0;                     //Kalman filter parameter
    double Q = 0.05;                     //Kalman filter parameter
    private double calRateEstimate = 0; //current best approximation of rate
    private double sigma = Q;           //Variable Kalman filter parameter
    int userAge;                        //to be taken from shared pref
    int userWeight;                     //to be taken from shared pref
    String userGender;                  //to be taken from shared pref

    //Lists used for testing
    //TODO: remove these lists after testing with sensor
    private List<Double> calorieList = new ArrayList<Double>();
    private List<Double> keytelList = new ArrayList<Double>();
    private List<Double> metList = new ArrayList<Double>();
    //******************************************************************************************************


    //DB
    //******************************************************************************************************
    DbHelper dbHelper;
    //******************************************************************************************************

    //Shared Preferences
    //******************************************************************************************************
    SharedPreferenceHelper sharedPreferenceHelper;
    //******************************************************************************************************


    /*
    This handler creates a new thread every 10 seconds.
    Each thread fills the workout values
     */
    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, delay_seconds * 1000);
                updateNotication();
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
        this.caloriesRate = 0;
        this.averageHR = 0;
        this.averageSpeed = 0;

        //Initialize DbHelper
        dbHelper = new DbHelper(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        getUserInfo();              //imports shared preference data
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");

        String stats = "Elapsed Time (s): " + (SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000
                + " \nHR: " + MainActivity.HR_RT
                + " \nSpeed: " + LocationService.SPEED_RT
                + " \nDistance: " + LocationService.WORKOUT_DISTANCE
                + " \nPosition: " + LocationService.lastKnownLatLng;
        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID_RECORDING)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle(getString(R.string.notification_title_recording))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(stats)) //Show stats in background
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
            kalReturn = calculateCalorieRate(MainActivity.HR_RT, LocationService.SPEED_RT, calRateEstimate, sigma, Q, C, userAge, userWeight, userGender);
            calRateEstimate = kalReturn[0];
            sigma = kalReturn[1];
        //*****************************************************************************************************************************

        //Now we want to fill all the workout data
        //*****************************************************************************************************************************
        time.add((SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000);         //Time in seconds
        listHR.add(MainActivity.HR_RT);                                                                 //Heart Rate list
        listSpeed.add(LocationService.SPEED_RT);                                                        //Speed list
        totalDistance = LocationService.WORKOUT_DISTANCE;                                               //Total Distance
        //*****************************************************************************************************************************
    }

    /*
    This method creates a new workout and then logs the values
    Check the logcat for debugging
     */
    private void createNewWorkout()
    {
        Log.d(TAG,"createNewWorkout");

        //Uncomment this block to retrieve hard caloric data in log at end of workout
        /*
        Log.d(TAG,"Keytel Data");
        for(double item:keytelList){
            Log.d(TAG,Double.toString(item));
        }
        Log.d(TAG,"MET Data");
        for(double item:metList){
            Log.d(TAG,Double.toString(item));
        }
        Log.d(TAG,"Kalman Data");
        for(double item:calorieList){
            Log.d(TAG,Double.toString(item));
        }
         */

        totalDuration = (SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000;   //Total Duration (seconds)
        workout = new Workout(time,listHR,listSpeed,totalDistance,totalDuration, calRateEstimate);
        workout.print(TAG);

        //Add the workout to the DB;
        dbHelper.insertWorkout(workout);
        dbHelper.UpdateBike( sharedPreferenceHelper.getSelectedBike() ,workout.getTotalDistance(),workout.getTotalDuration());
    }

    /*
    Takes user info from shared preferences and stores it for use in the caloric estimation.
     */
    public void getUserInfo(){
        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        if(sharedPreferenceHelper.getProfile()){
            userGender = sharedPreferenceHelper.getProfileGender();
            userAge = sharedPreferenceHelper.getProfileAge();
            userWeight = sharedPreferenceHelper.getProfileWeight();
        } else{
            Toast.makeText(this,"No Profile Found, Please create a profile " ,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        Log.d(TAG,"\ngetUserInfo\nAge: " + userAge + "\nWeight: " + userWeight + "\nGender: " + userGender);
    }
    private double[] calculateCalorieRate(double heartRate, double speed, double estimate, double sigma, double Q, double C, int age, int weight, String gender){
        /*
        Returns value of best guess for power output. Uses velocity data to approximate power
        based on MET standards and then HR to approximate output based on Keytel approximation.
        The MET standards typically provide approximations that are conservatively large. Keytel
        approximation will be used to bring the output to a more appropriate level (matching
        clinical trials).
         */
        Log.d(TAG,"\n" + "Received HR: " + heartRate + "\nReceived Speed: " + speed);
        Boolean male = gender.equals("Male");

        //Internal Parameters
        double deltaTime = delay_seconds;
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
        if(heartRate > 30) {
            if (male) {
                //Male
                newVal = ((-55.0969 + (0.6309 * heartRate) + (0.1988 * weight) + (0.2017 * age)) / 4.184) * (deltaTime)  / 60;  //kcal/min
            } else {
                //Female
                newVal = ((-20.4022 + (0.4472 * heartRate) - (0.1263 * weight) + (0.0740 * age)) / 4.184) * (deltaTime) / 60;  //kcal/min
            }
            if(newVal < 0) newVal = 0;
            K = (sigma * sigma * C) / (sigma * sigma * C * C + Q * Q);
            estimate = estimate + K * (newVal - C * estimate);
            sigma = (1 - K * C) * sigma;
        } else newVal = estimate;
        keytelList.add(newVal);


        //  update with metPower
        if(speed > 3) {
            newVal = MET * weight / 60;     //kcal/min
            K = (sigma * sigma * C) / (sigma * sigma * C * C + Q * Q);
            estimate = estimate + K * (newVal - C * estimate);
            sigma = (1 - K * C) * sigma;
            if(newVal < 0) newVal = 0;
        } else newVal = estimate;

        metList.add(newVal);
        //*****************************************************************************************************************************

        //Returning estimate and deviation, both used at next method call
        kalReturn = new double[]{estimate, sigma};
        calorieList.add(kalReturn[0]);
        Log.d(TAG,"Estimated Calorie Rate: " + kalReturn[0] + " cal/min");
        return kalReturn;
    }
    private void updateNotication()
    {
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        String hr = dec_0.format(MainActivity.HR_RT);
        String speed = dec_0.format(LocationService.SPEED_RT);
        String distance = dec_0.format(LocationService.WORKOUT_DISTANCE);

        String stats = "Elapsed Time (s): " + (SystemClock.elapsedRealtime() - FitnessFragment.chronometer.getBase())/1000
                + " \nHR (bpm): " + hr
                + " \nSpeed (km/h): " + speed
                + " \nDistance (m): " + distance
                + " \nPosition, " + LocationService.lastKnownLatLng;
        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID_RECORDING)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle(getString(R.string.notification_title_recording))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(stats)) //Show stats in background
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2,notification);
    }


}
