package com.example.bikebuddy.Utils;

import android.content.Context;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.SharedPreferenceHelper;

import java.util.Collections;

public class SummaryHelper {

    SharedPreferenceHelper sharedPreferenceHelper;
    DbHelper dbHelper;
    Context context;

    int distance;
    int duration;
    int maxHR;
    int minHR;
    int calBurned;
    int numWorkouts;

    public SummaryHelper(Context context){
        this.context = context;
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        dbHelper = new DbHelper(context);
        loadCurrentProfile();
    }

    private void loadCurrentProfile(){
        int[] retrievedData = sharedPreferenceHelper.getSummaryData();
        distance = retrievedData[0];
        duration = retrievedData[1];
        maxHR = retrievedData[2];
        minHR = retrievedData[3];
        calBurned = retrievedData[4];
        numWorkouts = retrievedData[5];
    }

    private void updateInsertWorkout(Workout workout){
        distance += workout.getTotalDistance();
        duration += workout.getTotalDuration();


        if(workout.getMaxHR() > maxHR) maxHR = workout.getMaxHR();

        calBurned += workout.getCaloriesBurned();
        numWorkouts = dbHelper.getWorkouts().size();

        sharedPreferenceHelper.saveSummary(distance, duration, maxHR, calBurned, numWorkouts);
    }

    private void updateDeleteWorkout(Workout workout){
        distance -= workout.getTotalDistance();
        duration -= workout.getTotalDuration();

        //TODO: find way to get max HR with workout deletion
        if(Collections.max(workout.getListHR()) > maxHR) maxHR = Collections.max(workout.getListHR()).intValue();

        calBurned -= workout.getCaloriesBurned();
        numWorkouts = dbHelper.getWorkouts().size();

        sharedPreferenceHelper.saveSummary(distance, duration, maxHR, calBurned, numWorkouts);
    }
}
