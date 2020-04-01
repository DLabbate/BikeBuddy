package com.example.bikebuddy.Utils;

import android.content.Context;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.SharedPreferenceHelper;

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





}
