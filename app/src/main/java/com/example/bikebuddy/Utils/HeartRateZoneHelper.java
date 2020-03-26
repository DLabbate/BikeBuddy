package com.example.bikebuddy.Utils;

import android.content.Context;

import com.example.bikebuddy.SharedPreferenceHelper;

public class HeartRateZoneHelper {

    //Shared Preferences
    //*********************************************************************************************
    protected SharedPreferenceHelper sharedPreferenceHelper;
    protected Context context;
    //*********************************************************************************************

    //Profile Data
    //*********************************************************************************************

    /*
    The maximum heart rate, or MHR, is used to compute the intensity of a workout
    In other words, which heart rate zone someone is exercising in
    MHR = 208 - 0.7*Age

    Zone 1 (Rest)                   --> 50-60% of MHR
    Zone 2 (Light Intensity)        -->
    Zone 3 (Moderate Intensity)     -->
    Zone 4 (High Intensity)         -->
    Zone 5

     */
    protected double MaxHeartRate;
    //*********************************************************************************************

    public HeartRateZoneHelper(Context context)
    {
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }


}
