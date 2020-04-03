package com.example.bikebuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferenceHelper {
    private static final String TAG = "SharedPreferenceHelper";
    /*sharedPreferences helper class*/
    private SharedPreferences sharedPreferences;
    public SharedPreferenceHelper(Context context)
    {
        sharedPreferences =  context.getSharedPreferences
                ("ProfilePreference", Context.MODE_PRIVATE);

    }

    /*setters*/

    public String saveProfileName(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Profilename", name);
        Log.d(TAG,"Saving Profile Name: " + name);
        editor.commit();
        return name;
    }
    public void saveProfileAge(Integer age){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Profileage", age);
        Log.d(TAG,"Saving Profile Age: " + age);
        editor.commit();
    }
    public void saveProfileWeight(Integer weight){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Profileweight", weight);
        Log.d(TAG,"Saving Profile Weight: " + weight);
        editor.commit();
    }

    public void saveProfileGender(String gender)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Profilegender", gender);
        Log.d(TAG,"Saving Profile Gender: " + gender);
        editor.commit();
    }

    //Setters for summary view
    public void saveSummary(Integer distance, Integer duration, Integer HR_max, Integer burnedCal, Integer numWorkouts, Integer averageDistance){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("TotalDistance", distance);
        editor.putInt("TotalDuration", duration);
        editor.putInt("maxHR", HR_max);
        editor.putInt("burnedCalories", burnedCal);
        editor.putInt("number_of_Workouts", numWorkouts);
        editor.putInt("averageDistance",averageDistance);

        Log.d(TAG,"Saving Total Distance: " + distance);
        Log.d(TAG,"Saving Total Duration: " + duration);
        Log.d(TAG,"Saving Max HR: " + HR_max);
        Log.d(TAG,"Saving Calories Burned: " + burnedCal);
        Log.d(TAG,"Saving Number of Workouts: " + numWorkouts);
        Log.d(TAG,"Saving averageDistance of Workouts: " + averageDistance);
        editor.commit();
    }

    /*
    This method saves a boolean to indicate that profile info already exists
     */
    public void saveProfile()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //This sets starting values if the profile had not yet existed.
        if(!sharedPreferences.getBoolean("ProfileExists",false)){
            editor.putInt("TotalDistance", 0);
            editor.putInt("TotalDuration", 0);
            editor.putInt("maxHR", 0);
            editor.putInt("burnedCalories", 0);
            editor.putInt("number_of_Workouts", 0);
            editor.putInt("averageDistance",0);
        }
        editor.putBoolean("ProfileExists", true);
        editor.commit();
    }


    /*getters*/

    public String getProfileName()
    {
        String name = sharedPreferences.getString("Profilename", "");
        Log.d(TAG,"Getting Profile Name: " + name);
        return name;
    }
    public String getProfileGender(){
        String gender = sharedPreferences.getString("Profilegender","");
        Log.d(TAG,"Getting Profile Gender: " + gender);
        return gender;
    }
    public int getProfileAge()
    {
        int age = sharedPreferences.getInt("Profileage", -1);
        Log.d(TAG,"Getting Profile Age: " + age);
        return age;
    }
    public int getProfileWeight()
    {
        int weight = sharedPreferences.getInt("Profileweight", -1);
        Log.d(TAG,"Getting Profile Weight: " + weight);
        return weight;
    }

    //Getter for summary Activity
    public int[] getSummaryData(){
        int[] summaryData = new int[6];

        summaryData[0] = sharedPreferences.getInt("TotalDistance",-1);
        summaryData[1] = sharedPreferences.getInt("TotalDuration",-1);
        summaryData[2] = sharedPreferences.getInt("maxHR",-1);
        summaryData[3] = sharedPreferences.getInt("burnedCalories",-1);
        summaryData[4] = sharedPreferences.getInt("number_of_Workouts",-1);
        summaryData[5] = sharedPreferences.getInt("averageDistance",-1);

        Log.d(TAG,"Retrieving Total Distance: " + summaryData[0]);
        Log.d(TAG,"Retrieving Total Duration: " + summaryData[1]);
        Log.d(TAG,"Retrieving Max HR: " + summaryData[2]);
        Log.d(TAG,"Retrieving Calories Burned: " + summaryData[3]);
        Log.d(TAG,"Retrieving Number of Workouts: " + summaryData[4]);
        Log.d(TAG,"Retrieving AverageDistance of Workouts: " + summaryData[5]);

        return summaryData;
    }

    /*
    This method checks if a profile exists or not
     */
    public boolean getProfile()
    {
        boolean exists = sharedPreferences.getBoolean("ProfileExists",false);
        Log.d(TAG,"Checking if a profile exists already: " + exists);
        return exists;
    }



    //Bike Info
    //***********************************************************************************************


    /*
    This method returns the ID of the selected bike
    -1 is default (no bike selected)
     */
    public int getSelectedBike()
    {
        int ID = sharedPreferences.getInt("BikeID",-1);
        Log.d(TAG,"Getting Selected Bike ID: " + ID);
        return ID;
    }

    public void setSelectedBike(int ID)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d(TAG,"Saving Bike ID: " + ID);
        editor.putInt("BikeID", ID);
        editor.commit();
    }

    //***********************************************************************************************


}
