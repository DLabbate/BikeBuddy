package com.example.bikebuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;


public class FitnessFragment extends Fragment {
    Chronometer chronometer;
    Button RecordWorkout;
    public static boolean running;
    long WorkoutDuration;

    TextView speedTextView;
    TextView distanceTextView;
    TextView distanceTitleTextView;

    public static final String TAG = "FitnessFragment";

    ImageView imageViewBluetoothStatus; //This is the ImageView that displays whether a device is connected or not
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness,container,false);
        RecordWorkout=view.findViewById(R.id.button_record_workout);
        chronometer= view.findViewById(R.id._chronometer);
        speedTextView = view.findViewById(R.id.text_speed_rt);
        distanceTextView = view.findViewById(R.id.text_distance_rt);
        distanceTitleTextView = view.findViewById(R.id.text_distance);


        imageViewBluetoothStatus = view.findViewById(R.id.image_bluetooth_status);
        if (MainActivity.isDeviceConnected == true)
        {
            imageViewBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_on);
        }
        else
        {
            imageViewBluetoothStatus.setImageResource(R.drawable.ic_bluetooth_off);
        }

        imageViewBluetoothStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isDeviceConnected != true)
                {
                    Log.d(TAG,"imageViewBluetoothStatus onClickListener");
                    ((MainActivity)getActivity()).connectToSensor();
                    Toast.makeText(getActivity(),"Trying to connect to " + MainActivity.SENSOR_NAME,Toast.LENGTH_SHORT).show();
                }
            }
        });
        // recording workout
        RecordWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running){ // when it is not running
                    showAlertDialog();

                }
                else{ // when running
                    chronometer.stop();
                    RecordWorkout.setText("record workout");
                    running=false;
                    chronometer.setVisibility(View.INVISIBLE);
                    distanceTextView.setVisibility(View.INVISIBLE);
                    distanceTitleTextView.setVisibility(View.INVISIBLE);
                    WorkoutDuration=chronometer.getBase();
                    resetWorkoutDistance(); //Reset the workout distance
                    Toast.makeText(getActivity(),"Workout Recorded",Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        // Saving the information of the timer when leaving the fragment
        Log.d(TAG,"onStop()");
        ((MainActivity)getActivity()).SaveTimerState(running);
        ((MainActivity)getActivity()).SaveTimerTime(chronometer.getBase());
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Simply when opening the fitness fragment,
         the app checks if the timer has been activated before in order to continue counting.
        */

        running = ((MainActivity) getActivity()).StateOfTimer();
        chronometer.setBase(((MainActivity) getActivity()).BaseOfTimer());
        if (running) {
            chronometer.setVisibility(View.VISIBLE);
            RecordWorkout.setText("Stop Recording");
        chronometer.start();
        }
    }

    private void resetWorkoutDistance()
    {
        GPSFragment.WORKOUT_DISTANCE = 0;
        TextView distanceTextView = getActivity().findViewById(R.id.text_distance_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (distanceTextView != null)
        {
            distanceTextView.setText(dec_0.format(GPSFragment.WORKOUT_DISTANCE)); //Update the Heart Rate TextView (Real Time)
        }
    }

    private void showAlertDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.text_record_workout));
        builder.setCancelable(true);
        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                resetWorkoutDistance(); //Reset the workout distance before we display it
                chronometer.setVisibility(View.VISIBLE);
                distanceTextView.setVisibility(View.VISIBLE);
                distanceTitleTextView.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                RecordWorkout.setText("Stop Recording");
                running=true;
            }
        });
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
