package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.example.bikebuddy.Utils.HeartRateZoneHelper;
import com.example.bikebuddy.Utils.MainPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

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

    public static final String CHANNEL_ID_DISCONNECT = "disconnected_notifications";
    public static final int NOTIFICATION_ID_BT_DISCONNECT = 3;
    //************************************************************************************************


    //Database
    //************************************************************************************************
    DbHelper dbHelper;
    //************************************************************************************************

    //Toolbar
    //***********************************************************************************************
    Toolbar toolbarMain;
    ImageView profileImageView;
    ImageView bikeImageView;
    ImageView performanceImageView;
    //***********************************************************************************************


    //Heart Rate Sampling
    //***********************************************************************************************
    public long currentHRValue;
    public long lastHRValue;
    //***********************************************************************************************

    //Heart Rate Zones
    //***********************************************************************************************
    public HeartRateZoneHelper heartRateZoneHelper;
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

        //Create notification channels
        createNotificationChannelLocation();
        createNotificationChannelRecording();
        createNotificationChannelDisconnect();

        currentHRValue = 0;
        lastHRValue = 0;

        setupHRZoneHelper();
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
        bikeImageView = findViewById(R.id.image_bike_toolbar);
        performanceImageView = findViewById(R.id.image_stats_toolbar);
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
                    TextView textBluetoothStatus = findViewById(R.id.text_bluetooth_status);
                    if (imageViewBluetoothStatus != null && textBluetoothStatus != null)
                    {
                        imageViewBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_on);
                        textBluetoothStatus.setText("Connected");
                    }
                }
            });
            Log.d(TAG,"Device connected: " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
            isDeviceConnected = false;
            createBTSensorDisconnectNotification();
             /*
            Update the Bluetooth Status (ImageView)
            This needs to run in main thread!
             */
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     ImageView imageViewBluetoothStatus = findViewById(R.id.image_bluetooth_status);
                     TextView textBluetoothStatus = findViewById(R.id.text_bluetooth_status);
                     if (imageViewBluetoothStatus != null && textBluetoothStatus != null)
                     {
                         imageViewBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_off);
                         textBluetoothStatus.setText("Disconnected");
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
            lastHRValue = currentHRValue; //Update last value
            final long HR = 0x000000FF & message[12];
            currentHRValue = HR; //Update current value

            //Check if the heart rate value makes sense (>45)
            //Also check if the value is SIMILAR TO THE LAST VALUE THAT CAME IN (if difference between the 2 points is less than 30)
            if (HR > 45 && (Math.abs(lastHRValue - currentHRValue ) < 30))
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
                    TextView zoneTextView = findViewById(R.id.text_zone); //Level of intensity of workout
                    if (heartRateTextView != null && zoneTextView != null) //Check if it is null
                    {
                        /*
                        When a heart beat is not detected, the sensor sends a value of zero
                         */
                        if (!(HR_RT <= 0))
                        {
                            heartRateTextView.setText(dec_0.format(HR_RT)); //Update the Heart Rate TextView (Real Time)
                            updateZoneText(zoneTextView);
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
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{ 0 });
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannelDisconnect() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_name_disconnect);
            String description = (getString(R.string.notification_description_disconnect));
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_DISCONNECT, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /*
    This function will create a notification to inform the user that
    the HR sensor has been disconnected
     */
    private void createBTSensorDisconnectNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_DISCONNECT)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle("HR Sensor Disconnected!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.text_sensor_disconnect)))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID_BT_DISCONNECT, builder.build());
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

        bikeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BikeActivity.class);
                startActivity(intent);
            }
        });

        performanceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PerformanceActivity.class);
                startActivity(intent);
            }
        });
    }


    /*
    Disable the back button
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        return;
    }

    //Creates a new instance of HeartRateZoneHelper
    private void setupHRZoneHelper()
    {
        heartRateZoneHelper = new HeartRateZoneHelper(this);
    }


    /*
    The following method updates a text view to display the appropriate intensity
    Zone 1 --> "Rest"
    Zone 2 --> "Light Intensity"
    Zone 3 --> "Moderate Intensity"
    Zone 4 --> "High Intensity"
    Zone 5 --> "Maximal Intensity
     */
    private void updateZoneText(TextView HRZone)
    {
        int zone = heartRateZoneHelper.getZone(HR_RT);

        if (zone == 5)
        {
            HRZone.setText(getString(R.string.HR_zone5));
            HRZone.setBackgroundResource(R.drawable.shape_zone_5);
        }

        else if (zone == 4)
        {
            HRZone.setText(getString(R.string.HR_zone4));
            HRZone.setBackgroundResource(R.drawable.shape_zone_4);
        }

        else if (zone == 3)
        {
            HRZone.setText(getString(R.string.HR_zone3));
            HRZone.setBackgroundResource(R.drawable.shape_zone_3);
        }

        else if (zone == 2)
        {
            HRZone.setText(getString(R.string.HR_zone2));
            HRZone.setBackgroundResource(R.drawable.shape_zone_2);
        }

        else
        {
            HRZone.setText(getString(R.string.HR_zone1));
            HRZone.setBackgroundResource(R.drawable.shape_zone_1);
        }
    }
}
