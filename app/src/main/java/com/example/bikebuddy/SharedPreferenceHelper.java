package com.example.bikebuddy;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
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
        editor.commit();
        return name;
    }
    public void saveProfileAge(Integer age){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Profileage", age);
        editor.commit();
    }
    public void saveProfileWeight(Integer weight){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Profileweight", weight);
        editor.commit();
    }


    /*getters*/

    public String getProfileName()
    {
        return sharedPreferences.getString("Profilename", "");
    }
    public int getProfileAge()
    {
        return sharedPreferences.getInt("Profileage", -1);
    }
    public int getProfileWeight()
    {
        return sharedPreferences.getInt("Profileweight", -1);
    }


}
