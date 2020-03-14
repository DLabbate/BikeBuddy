package com.example.bikebuddy;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Date;
import java.util.List;

public class RecordingService extends Service {

    private Date date;
    private List<Long> time;
    private List <Double> listHR;
    private List <Double> listSpeed;
    private double totalDistance;
    private long totalDuration;
    private double caloriesBurned;
    private double averageHR;
    private double averageSpeed;

    public static final String TAG = "RecordingService";

    public RecordingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");

        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID_RECORDING)
                .setSmallIcon(R.drawable.ic_bike)
                .setContentTitle(getString(R.string.notification_title_recording))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        startForeground(2,notification);


        return START_NOT_STICKY;


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    private void fillWorkoutValues()
    {
        //if ()
    }
}
