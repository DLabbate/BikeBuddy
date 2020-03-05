package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bikebuddy.Bluetooth.DelimiterReader;
import com.example.bikebuddy.Utils.MainPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;
import me.aflak.bluetooth.interfaces.DiscoveryCallback;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity"; //TAG used for debugging
    public static final String SENSOR_NAME = "HXM034738"; //Name of the Zephyr HxM BT sensor
    /*
    To Do
    Let the user put in the name of their sensor
    */

    ViewPager viewPager;
    TabLayout tabLayout;
    MainPagerAdapter mainPagerAdapter;
    ImageView imageViewBluetooth; //This is the image that displays if the device is connected or not

    //Bluetooth
    private Bluetooth bluetooth;
    public static boolean isDeviceConnected = false;

    //Real Time Values
    public static double HR_RT; //Heart Rate
    public static double SPEED_RT; //Speed (km/h)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();

        bluetooth = new Bluetooth(this);

        bluetooth.setReader(DelimiterReader.class); //Set the custom delimiter for the Zephyr HxM sensor, which uses ETX to end the message
        bluetooth.setDeviceCallback(deviceCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bluetooth.onStart();
        if(bluetooth.isEnabled()){
            // doStuffWhenBluetoothOn() ...
            bluetooth.connectToName(SENSOR_NAME); //This is the name of the Zephyr HxM BT sensor being used

        } else {
            bluetooth.enable();
            bluetooth.connectToName(SENSOR_NAME);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }

    private void setupUI()
    {
        viewPager = findViewById(R.id.view_pager_main);
        tabLayout = findViewById(R.id.tab_layout_main);

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
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_bike);
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
            Byte 52-53 --> Instantaneous speed (with the least significant byte sent first)
            See the following link for more details:
            https://www.zephyranywhere.com/media/download/hxm1-api-p-bluetooth-hxm-api-guide-20100722-v01.pdf
             */
            long x = 256;
            double speed_ms = (double) ((0x000000FF & message[52]) | (0x000000FF & message[53]) << 8)/x; //Speed in m/s
            final double speed_kmh = speed_ms*3.6; //Speed in km/h
            final long HR = message[12];
            HR_RT = HR; //Update HR Value
            SPEED_RT = speed_kmh; //Update Speed Value
            Log.d(TAG,"onMessage, Message Size: " + message.length);
            Log.d(TAG,"onMessage, HR: " + HR);
            Log.d(TAG,"onMessage, Speed: " + speed_kmh);

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
                    TextView speedTextView = findViewById(R.id.text_speed_rt);
                    if (heartRateTextView != null && heartRateTextView != null) //Check if it is null
                    {
                        /*
                        When a heart beat is not detected, the sensor sends a value of zero
                         */
                        if (!(HR_RT <= 0))
                        {
                            heartRateTextView.setText(dec_0.format(HR_RT)); //Update the Heart Rate TextView (Real Time)
                        }
                        speedTextView.setText(dec_0.format(SPEED_RT)); //Update the Heart Rate TextView (Real Time)
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

}
