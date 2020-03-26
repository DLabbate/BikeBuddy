package com.example.bikebuddy.Utils;

import android.content.Context;

import com.example.bikebuddy.SharedPreferenceHelper;

public class HeartRateZoneHelper {

    //Log
    //*********************************************************************************************
    public static final String TAG = "HeartRateZoneHelper";
    //*********************************************************************************************

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

    Zone 1 (Rest)                --> 50-60% of MHR
    Zone 2 (Light Intensity)     --> 60-70% of MHR
    Zone 3 (Moderate Intensity)  --> 70-80% of MHR
    Zone 4 (High Intensity)      --> 80-90% of MHR
    Zone 5 (Maximal Intensity)   --> 90-100% of MHR

    For more info, see https://www.hexoskin.com/pages/key-metrics
     */
    protected double maxHeartRate;
    //*********************************************************************************************


    public HeartRateZoneHelper(Context context)
    {
        this.context = context;
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        int age = sharedPreferenceHelper.getProfileAge();
        maxHeartRate = 208 - 0.7*age;
    }

    public double getMaxHeartRate() {
        return maxHeartRate;
    }


    /*
    This method determines the Zone the user is operating at (1,2,3,4 or 5)
    Input: double HR (heart rate)
     */
    public int getZone(double HR)
    {
        if (HR >= (0.9*maxHeartRate))
            return 5;
        else if (HR >= (0.8*maxHeartRate))
            return 4;
        else if (HR >= (0.7*maxHeartRate))
            return 3;
        else if (HR >= (0.6*maxHeartRate))
            return 2;
        else
            return 1;
    }
}
