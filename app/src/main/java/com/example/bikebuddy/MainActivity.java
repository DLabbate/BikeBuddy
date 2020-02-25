package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;

import com.example.bikebuddy.Bluetooth.DelimiterReader;
import com.example.bikebuddy.Utils.MainPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity"; //TAG used for debugging

    ViewPager viewPager;
    TabLayout tabLayout;
    MainPagerAdapter mainPagerAdapter;

    //Bluetooth
    private Bluetooth bluetooth;

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
        bluetooth.connectToName("HXM034738");
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

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPagerAdapter.addFragment(new LogFragment());
        mainPagerAdapter.addFragment(new GPSFragment());
        mainPagerAdapter.addFragment(new FitnessFragment());
        viewPager.setAdapter(mainPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
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
            long speed = ((0x000000FF & message[52]) | (0x000000FF & message[53]) << 8);
            Log.d(TAG,"onMessage, Message Size: " + message.length);
            Log.d(TAG,"onMessage, Message HR: " + message[12]);
            Log.d(TAG,"onMessage, Message Speed: " + speed);
        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onConnectError(BluetoothDevice device, String message) {

        }
    };
}
