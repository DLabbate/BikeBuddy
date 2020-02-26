package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.bikebuddy.Bluetooth.DelimiterReader;
import com.example.bikebuddy.Utils.MainPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity"; //TAG used for debugging

    ViewPager viewPager;
    TabLayout tabLayout;
    MainPagerAdapter mainPagerAdapter;

    //Bluetooth
    private Bluetooth bluetooth;

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
            bluetooth.connectToName("HXM034738"); //This is the name of the Zephyr HxM BT sensor being used
            /*
            To Do
            Let the user put in the name of their sensor
             */
        } else {
            bluetooth.enable();
            bluetooth.connectToName("HXM034738");
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
            Log.d(TAG,"Device connected: " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device, String message) {
            Log.d(TAG,"Device disconnected");
        }

        @Override
        public void onMessage(byte[] message) {
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
                    DecimalFormat dec_2 = new DecimalFormat("#0.00"); //2 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
                    DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places
                    TextView heartRateTextView = (findViewById(R.id.text_heart_rate_rt));
                    TextView speedTextView = findViewById(R.id.text_speed_rt);
                    if (heartRateTextView != null && heartRateTextView != null) //Check if it is null
                    {
                        heartRateTextView.setText(dec_0.format(HR_RT)); //Update the Heart Rate TextView (Real Time)
                        speedTextView.setText(dec_2.format(SPEED_RT)); //Update the Heart Rate TextView (Real Time)
                    }
                }
            });

        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {

        }
    };
}
