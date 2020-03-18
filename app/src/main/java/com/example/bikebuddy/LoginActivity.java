package com.example.bikebuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bikebuddy.Permissions.Permissions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    boolean permissionsGranted = false; //boolean that checks if permissions are granted or not (Location...)
    final int permissionsRequestCode = 1;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private FusedLocationProviderClient mFusedLocationClient;

    Button loginButton;
    protected SharedPreferenceHelper sharedpreferencehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUI();

        sharedpreferencehelper = new SharedPreferenceHelper(LoginActivity.this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }



    /*
    This method connects the UI elements and onClickListeners
     */
    private void setupUI() {
        loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                To DO:
                Check if input email and password is correct (PRIOR to permissions). If correct, proceed to verify permissions.
                Otherwise, inform user they have entered incorrect information
                 */


                    if(sharedpreferencehelper.getProfileName() == null ||
                            sharedpreferencehelper.getProfileAge() == -1 ||
                            sharedpreferencehelper.getProfileWeight() == -1) {
                        Intent intentp = new Intent(LoginActivity.this,
                                ProfileActivity.class);
                        startActivity(intentp);
                    }





                //Check permissions (Location,etc.)
                checkPermissions();

                if (permissionsGranted == true && isServicesAvailable() && isMapsEnabled()) {
                    Log.d(TAG,"Moving to MainActivity");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /*
    This method verifies if ALL permissions are granted.
    If permissions have not yet been granted, then it invokes requestPermissions()
     */
    private void checkPermissions() {
        Log.d(TAG, "checkPermissions() method");
        for (int i = 0; i < Permissions.permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, Permissions.permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false;
                Log.d(TAG, "Missing Permission: " + Permissions.permissions[i]);
                requestPermissions();
                return;
            }
        }
        permissionsGranted = true; //If all permissions are granted, we set this boolean to true
    }

    /*
    Requests permissions from the user
     */
    private void requestPermissions() {
        Log.d(TAG, "Requesting Permissions");
        ActivityCompat.requestPermissions(this, Permissions.permissions, permissionsRequestCode);
    }

    /*
    Determines if the user accepted or denied the permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case permissionsRequestCode: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(),"Please enable permissions to proceed",Toast.LENGTH_SHORT).show();
                            permissionsGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG,"Moving to MainActivity"); //If all permissions have been granted, we can move to main activity
                    permissionsGranted = true;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }
    }


    //Determines if user has access to google maps services
    public boolean isServicesAvailable(){
        Log.d(TAG, "isServicesAvailable() method ");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesAvailable() method: google play services is working");
            return true;
        }

        //checks if error is resolvable
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Log.d(TAG, "isServicesAvailable() method: error can be resolved");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();

        }
        else{
            Toast.makeText(LoginActivity.this,"Can't Access Maps", Toast.LENGTH_LONG).show();
        }
        return false;
    }



    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Checks if user device has GPS enabled
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityResult() method");

        switch(requestCode){
            case PERMISSIONS_REQUEST_ENABLE_GPS:{
                //if ()
            }
        }
    }

     */


}


