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

    /*
    This method saves a boolean to indicate that profile info already exists
     */
    public void saveProfile()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
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
