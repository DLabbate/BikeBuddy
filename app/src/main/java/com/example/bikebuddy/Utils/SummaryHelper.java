package com.example.bikebuddy.Utils;

import android.content.Context;
import android.util.Log;

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

    public void updateInsertWorkout(Workout workout){
        Log.d(TAG,"UpdateInsertWorkout");
        Log.d(TAG,"Old Distance = " + distance);
        distance += workout.getTotalDistance();
        Log.d(TAG,"New distance = " + distance);
        duration += workout.getTotalDuration();


        if(workout.getMaxHR() > maxHR) maxHR = workout.getMaxHR();
        averageDistance = calculateAverageDistance();
        calBurned += workout.getCaloriesBurned();
        numWorkouts = dbHelper.getWorkouts().size();

        sharedPreferenceHelper.saveSummary(distance, duration, maxHR, calBurned, numWorkouts, averageDistance);
    }

    public void updateDeleteWorkout(Workout workout){
        Log.d(TAG,"UpdateDeleteWorkout");
        distance -= workout.getTotalDistance();
        duration -= workout.getTotalDuration();

        List<Workout> workoutList = dbHelper.getWorkouts();
        maxHR = 0;
        for(Workout item:workoutList){
            if( item.getMaxHR() > maxHR ) maxHR = item.getMaxHR();
        }

        averageDistance = calculateAverageDistance();
        calBurned -= workout.getCaloriesBurned();
        numWorkouts = workoutList.size();

        sharedPreferenceHelper.saveSummary(distance, duration, maxHR, calBurned, numWorkouts,averageDistance);
    }

    private int calculateAverageDistance(){
        List<Workout> workoutList = dbHelper.getWorkouts();
        int average = 0;
        for(Workout item:workoutList){
            average += item.getTotalDistance();
        }
        return average/workoutList.size();
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
