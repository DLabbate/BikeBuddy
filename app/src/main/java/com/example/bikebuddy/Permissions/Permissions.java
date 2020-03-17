package com.example.bikebuddy.Permissions;


import android.Manifest;

public class Permissions {
    public static final String[] permissions =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    //Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
            };
}
