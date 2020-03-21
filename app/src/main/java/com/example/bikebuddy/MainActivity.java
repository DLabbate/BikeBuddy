package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bikebuddy.Bluetooth.DelimiterReader;
import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Services.LocationService;
import com.example.bikebuddy.Utils.MainPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class MainActivity extends AppCompatActivity {

    //Constants
    //************************************************************************************************
    public static final String TAG = "MainActivity"; //TAG used for debugging
    public static final String SENSOR_NAME = "HXM034738"; //Name of the Zephyr HxM BT sensor
    //************************************************************************************************

    /*
    To Do
    Let the user put in the name of their sensor
    */

    //Views
    //************************************************************************************************
    ViewPager viewPager;
    TabLayout tabLayout;
    MainPagerAdapter mainPagerAdapter;
    ImageView imageViewBluetooth; //This is the image that displays if the device is connected or not
    //************************************************************************************************

    //Bluetooth
    //************************************************************************************************
    private Bluetooth bluetooth;
    public static boolean isDeviceConnected = false;
    //************************************************************************************************

    //Real Time Values
    //************************************************************************************************
    public static double HR_RT; //Heart Rate
    //************************************************************************************************

    //Shared Preferences
    //***********************************************************************************************
    SharedPreferenceHelper sharedpreferencehelper;
    //***********************************************************************************************

    //Notifications
    //************************************************************************************************
    public static final String CHANNEL_ID_LOCATION = "workout_notifications";
    public static final int NOTIFICATION_ID_LOCATION = 1;

    public static final String CHANNEL_ID_RECORDING = "recording_notifications";
    public static final int NOTIFICATION_ID_RECORDING = 2;
    //************************************************************************************************


    //Database
    //************************************************************************************************
    DbHelper dbHelper;
    //************************************************************************************************

    //Toolbar
    //***********************************************************************************************
    Toolbar toolbarMain;
    ImageView profileImageView;
    //***********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();

        bluetooth = new Bluetooth(this);

        bluetooth.setReader(DelimiterReader.class); //Set the custom delimiter for the Zephyr HxM sensor, which uses ETX to end the message
        bluetooth.setDeviceCallback(deviceCallback);

        sharedpreferencehelper = new SharedPreferenceHelper(this);

        createNotificationChannelLocation();
        createNotificationChannelRecording();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        bluetooth.onStart();
        if(bluetooth.isEnabled()){
            if(!bluetooth.isConnected())
            {
                //Try connecting to the sensor if it is not already connected
                // doStuffWhenBluetoothOn() ...
                bluetooth.connectToName(SENSOR_NAME); //This is the name of the Zephyr HxM BT sensor being used
            }
        } else {
            bluetooth.enable();
            bluetooth.connectToName(SENSOR_NAME);}

        checkProfile();
        //Start Location Services
        createLocationService();
    }



    @Override



    protected void onStop() {
        super.onStop();
        //bluetooth.onStop();
        //SaveTimerState(false);
        Log.d(TAG,"onStop()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
    }

    private void setupUI()
    {
        Log.d(TAG,"setupUI");
        viewPager = findViewById(R.id.view_pager_main);
        tabLayout = findViewById(R.id.tab_layout_main);
        dbHelper = new DbHelper(this);

        /*
        Next we should set up the ViewPager and TabLayout
        The ViewPager contains 3 Fragments: LogFragment, GPSFragment, and FitnessFragment
        The TabLayout is used to move between these 3 fragments (found at the bottom of the application UI)
         */
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPagerAdapter.addFragment(new LogFragment());
        mainPagerAdapter.addFragment(new GPSFragment());
        mainPagerAdapter.addFragment(new FitnessFragment());
        viewPager.setAdapter(mainPagerAdapter);
        tabLayout.setupWithViewPager(viewPager); //Associates the TabLayout with the ViewPager
        tabLayout.getTabAt(0).setText("Logs");
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_list);
        tabLayout.getTabAt(1).setText("GPS");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_navigation);
        tabLayout.getTabAt(2).setText("Fitness");
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_heart);

        toolbarMain = findViewById(R.id.toolbar_main);
        profileImageView = findViewById(R.id.image_profile_toolbar);
        setToolbarOnClickListener();
    }

    private DeviceCallback deviceCallback = new DeviceCallback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            isDeviceConnected = true;
            /*
            Update the Bluetooth Status (ImageView)
            This needs to run in main thread!
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView imageViewBluetoothStatus = findViewById(R.id.image_bluetooth_status);
                    if (imageViewBluetoothStatus != null)
                    {
                        imageViewBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_on);
                    }
                }
            });
            Log.d(TAG,"Device connected: " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
            isDeviceConnected = false;
             /*
            Update the Bluetooth Status (ImageView)
            This needs to run in main thread!
             */
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     ImageView imageViewBluetoothStatus = findViewById(R.id.image_bluetooth_status);
                     if (imageViewBluetoothStatus != null)
                     {
                         imageViewBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_off);
                     }
                 }
             });
            Log.d(TAG,"Device disconnected");
        }

        @Override
        public void onMessage(byte[] message) {
            /*
            This method is used to read the message sent by the Zephyr HxM BT sensor
            Byte 12 --> Heart Rate (unsigned byte)
            See the following link for more details:
            https://www.zephyranywhere.com/media/download/hxm1-api-p-bluetooth-hxm-api-guide-20100722-v01.pdf
             */
            final long HR = message[12];
            if (HR >= 30)
            {
                HR_RT = HR; //Update HR Value
            }
            Log.d(TAG,"onMessage, Message Size: " + message.length);
            Log.d(TAG,"onMessage, HR: " + HR);

            /*
            In order to update the UI, we have to run it in the main thread!
            See the following link for more detail
            https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //DecimalFormat dec_2 = new DecimalFormat("#0.00"); //2 decimal places
                    DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
                    TextView heartRateTextView = (findViewById(R.id.text_heart_rate_rt));
                    if (heartRateTextView != null) //Check if it is null
                    {
                        /*
                        When a heart beat is not detected, the sensor sends a value of zero
                         */
                        if (!(HR_RT <= 0))
                        {
                            heartRateTextView.setText(dec_0.format(HR_RT)); //Update the Heart Rate TextView (Real Time)
                        }
                    }
                }
            });

        }

        @Override
        public void onError(int errorCode) {
            Log.d(TAG,"onError");
        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {
            Log.d(TAG,"onConnectError");
        }
    };

    /*
    This function is to be called when the Zephyr Sensor has been disconnected, or the user
    unintentionally disabled Bluetooth.
     */
    public void connectToSensor()
    {
        if(bluetooth.isEnabled()){
            bluetooth.connectToName(SENSOR_NAME); //This is the name of the Zephyr HxM BT sensor being used

        } else {
            bluetooth.enable();
            bluetooth.connectToName(SENSOR_NAME);
        }
    }

    // this function saves the timer into sharedprefrences
    public void SaveTimerTime(long time){
        SharedPreferences timer = getSharedPreferences("stopwatch", MODE_PRIVATE);
        SharedPreferences.Editor editor = timer.edit();
        editor.putLong("timerRunning", time);
        editor.apply();
        Log.d(TAG,"SaveTimerTime: " + time);
    }

    // this function saves the status of the timer (running or not) into sharedprefrences
    public void SaveTimerState( boolean running){
        SharedPreferences timer = getSharedPreferences("stopwatch", MODE_PRIVATE);
        SharedPreferences.Editor editor = timer.edit();
        editor.putBoolean("state", running);
        editor.apply();
        Log.d(TAG,"SaveTimerState: " + running);
    }

    // this function retrieves the timer from sharedprefrences
    public long BaseOfTimer(){
        SharedPreferences timer = getSharedPreferences("stopwatch", MODE_PRIVATE);
        long baseOfTimer = timer.getLong("timerRunning",SystemClock.elapsedRealtime());
        Log.d(TAG,"Base of Timer: " + baseOfTimer);
        return baseOfTimer;
    }

    // this function retrieves the status of the timer from sharedprefrences
    public boolean StateOfTimer(){
        SharedPreferences timer = getSharedPreferences("stopwatch", MODE_PRIVATE);
        boolean stateOfTimer = timer.getBoolean("state",false);
        Log.d(TAG,"State of Timer: " + stateOfTimer);
        return stateOfTimer;
    }

    private void checkProfile()
    {
        if(sharedpreferencehelper.getProfileName() == null ||
                sharedpreferencehelper.getProfileAge() == -1 ||
                sharedpreferencehelper.getProfileWeight() == -1)
        {
            Intent intentp = new Intent(MainActivity.this,
                    ProfileActivity.class);
            startActivity(intentp);
        }
    }
    private void connectToZephyr()
    {
        bluetooth.connectToName(SENSOR_NAME);
    }

    /*
    This method creates a foreground location service
    so that workouts can be recorded while in background
     */
    public void createLocationService()
    {
        Log.d(TAG,"createLocationService()");
        startService(new Intent(this, LocationService.class));
    }

    private void createNotificationChannelLocation() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_name_location);
            String description = (getString(R.string.notification_description_location));
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_LOCATION, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannelRecording() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_name_recording);
            String description = (getString(R.string.notification_description_recording));
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_RECORDING, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setToolbarOnClickListener()
    {
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
    }



}
