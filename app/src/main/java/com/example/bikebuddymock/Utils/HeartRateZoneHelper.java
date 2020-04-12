package com.example.bikebuddymock.Utils;

import android.content.Context;
import android.util.Log;

import com.example.bikebuddymock.SharedPreferenceHelper;

public class HeartRateZoneHelper {

    //------------------------------------------------------FIELDS----------------------------------------------------------//

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


    //------------------------------------------------------CONSTRUCTOR----------------------------------------------------------//

    public HeartRateZoneHelper(Context context)
    {
        this.context = context;
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
        int age = sharedPreferenceHelper.getProfileAge();
        maxHeartRate = 208 - 0.7*age;

        Log.d(TAG,"HeartRateZoneHelper Constructor. " + "Age: " + age + " MHR: " + Double.toString(maxHeartRate));
    }


    //------------------------------------------------------METHODS----------------------------------------------------------//
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
        {
            Log.d(TAG,"Heart Rate Zone 5");
            return 5;
        }

        else if (HR >= (0.8*maxHeartRate))
        {
            Log.d(TAG,"Heart Rate Zone 4");
            return 4;
        }

        else if (HR >= (0.7*maxHeartRate))
        {
            Log.d(TAG,"Heart Rate Zone 3");
            return 3;
        }

        else if (HR >= (0.6*maxHeartRate))
        {
            Log.d(TAG,"Heart Rate Zone 2");
            return 2;
        }

        else
        {
            Log.d(TAG,"Heart Rate Zone 1");
            return 1;
        }
    }
}
