package com.example.bikebuddy.Utils;

import android.content.Context;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.SharedPreferenceHelper;

import java.util.List;

public class SummaryHelper {
    private static final String TAG = "Summary Helper";

    SharedPreferenceHelper sharedPreferenceHelper;
    DbHelper dbHelper;
    Context context;

    private int distance;
    private int duration;
    private int maxHR;
    private int calBurned;
    private int numWorkouts;
    private int averageDistance;

    private int[] retrievedData;

    public SummaryHelper(Context context){
        this.context = context;
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        dbHelper = new DbHelper(context);
        loadCurrentProfile();
        updateSummary();
    }

    private void loadCurrentProfile(){
        retrievedData = sharedPreferenceHelper.getSummaryData();
        distance = retrievedData[0];
        duration = retrievedData[1];
        maxHR = retrievedData[2];
        calBurned = retrievedData[3];
        numWorkouts = retrievedData[4];
        averageDistance = retrievedData[5];

        /*
        Log.d(TAG,"\nLoad Current Data: " +
                "\ndistance = " + distance +
                "\nduration = " + duration +
                "\nmaxHR = " + maxHR +
                "\ncalBurned = " + calBurned +
                "\nnumWorkouts = " + numWorkouts);
         */
    }

    public void updateSummary(){
        List<Workout> workoutList = dbHelper.getWorkouts();
        distance = 0;
        duration = 0;
        maxHR = 0;
        calBurned = 0;
        numWorkouts = workoutList.size();

        for(Workout item:workoutList){
            distance        += item.getTotalDistance();                 //find total distance
            duration        += item.getTotalDuration();                 //find total duration
            if( item.getMaxHR() > maxHR ) maxHR = item.getMaxHR();      //find maxHR
            calBurned       += item.getCaloriesBurned();                //find total calburned
        }
        if( workoutList.size() == 0 ) averageDistance = 0;
        else                          averageDistance = distance/workoutList.size();

        sharedPreferenceHelper.saveSummary(distance, duration, maxHR, calBurned, numWorkouts,averageDistance);
    }


    //Getters
    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public int getMaxHR() {
        return maxHR;
    }

    public int getCalBurned() {
        return calBurned;
    }

    public int getNumWorkouts() {
        return numWorkouts;
    }

    public int getAverageDistance() {
        return averageDistance;
    }

    public int[] getRetrievedData() {
        return retrievedData;
    }
}
