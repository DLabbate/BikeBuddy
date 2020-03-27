package com.example.bikebuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Services.LocationService;
import com.example.bikebuddy.Services.RecordingService;
import com.example.bikebuddy.Utils.Workout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class FitnessFragment extends Fragment {

    //Public static variables to be accessed in this fragment AND recording service
    //***************************************************************************************
    public static Chronometer chronometer;
    public static boolean running;
    public static long WorkoutDuration;
    //***************************************************************************************

    private Button RecordWorkout;
    private Button generateMock;        //THIS IS JUST TO READ MOCK DATA
    TextView speedTextView;
    TextView distanceTextView;
    TextView distanceTitleTextView;

    FrameLayout distanceFrameLayout;

    //Added by brady to test DB
    //TODO: remove once recording manager is setup
    private Workout workout;
    private DbHelper dbHelper;

    public static final String TAG = "FitnessFragment";

    private ImageView imageViewBluetoothStatus; //This is the ImageView that displays whether a device is connected or not
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness,container,false);
        RecordWorkout=view.findViewById(R.id.button_record_workout);
        generateMock = view.findViewById(R.id.button_mockData);     //THIS IS JUST TO READ MOCK DATA
        chronometer= view.findViewById(R.id._chronometer);
        speedTextView = view.findViewById(R.id.text_speed_rt);
        distanceTextView = view.findViewById(R.id.text_distance_rt);
        distanceTitleTextView = view.findViewById(R.id.text_distance);
        distanceFrameLayout = view.findViewById(R.id.frame_distance);


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
                    startRecording();

                }
                else{ // when running
                    Log.d(TAG,"workout stopped");
                    chronometer.stop();
                    RecordWorkout.setText("record workout");
                    running=false;
                    chronometer.setVisibility(View.INVISIBLE);
                    distanceTextView.setVisibility(View.INVISIBLE);
                    distanceTitleTextView.setVisibility(View.INVISIBLE);
                    distanceFrameLayout.setVisibility(View.INVISIBLE);
                    WorkoutDuration = chronometer.getBase();
                    resetWorkoutDistance(); //Reset the workout distance
                    Toast.makeText(getActivity(),"Workout Recorded",Toast.LENGTH_SHORT).show();
                    stopRecordingService();
                }

            }
        });

        //**************************************************************************************
        //THIS IS JUST FOR POPULATING MOCK DATA
        generateMock.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                for(int j = 0; j < 14; j++) {       //this 14 is hardcoded  to make 20 workouts
                    //data initialization for workout
                    List<Long> listTime = new ArrayList<>();
                    List<Double> listHR = new ArrayList<>();
                    List<Double> listSpeed = new ArrayList<>();
                    double distance = 0;
                    Date date;
                    LocalDateTime LDT;

                    //Parsing the JSON
                    try {
                        JSONObject obj = new JSONObject(loadJSONfromAsset());
                        String bikeData = "bikeData" + (j+1);
                        JSONObject data = obj.getJSONObject(bikeData);
                        int id = data.getInt("id");
                        JSONArray heartRate = data.getJSONArray("heart_rate");
                        JSONArray speed = data.getJSONArray("speed");
                        JSONArray time = data.getJSONArray("timestamp");
                        for (int i = 0; i < time.length(); i++) {
                            listTime.add(time.getLong(i));
                            listHR.add(heartRate.getDouble(i));
                            listSpeed.add(speed.getDouble(i));
                            if(i>0) {
                                distance = distance + speed.getDouble(i) * (time.getDouble(i) - (double) time.getDouble(i - 1));
                            }
                        }
                        //Run Kalman filter on data
                        double userWeight = 0;
                        int userAge = 0;
                        getUserInfo(userWeight, userAge);
                        Double calRate = mockFilter(listTime, listHR, listSpeed, userWeight, userAge);

                        //generating random date and time
                        Instant lowerLimit = Instant.now().minus(Duration.ofDays(30));
                        Instant dateInstance = dateAfter(lowerLimit, Instant.now());
                        //date = Date.from(dateInstance);

                        //Creating workout
                        Workout workout = new Workout();
                        workout.setTime(listTime);
                        workout.setListSpeed(listSpeed);
                        workout.setListHR(listHR);
                        workout.setAverageSpeed(workout.calculateAverageSpeed());
                        workout.setAverageHR(workout.calculateAverageHR());
                        workout.setCaloriesRate(calRate);
                        workout.setCaloriesBurned(workout.calculateCaloriesBurned(calRate));
                        workout.setTotalDistance(distance);
                        workout.setTotalDuration(listTime.get(listTime.size()-1)-listTime.get(0));
                        workout.setDate(Calendar.getInstance().getTime());
                        workout.print(TAG);
                        /* THIS SECTION IS USED TO VERIFY PARSING OF JSON DATA IN LOG
                        Log.d(TAG, "JSON RETRIEVED data: " + bikeData);
                        Log.d(TAG, "JSON CONVERTED LISTS:" + "time = " + listTime);
                        Log.d(TAG, "JSON CONVERTED LISTS:" + "heart rate = " + listHR);
                        Log.d(TAG, "JSON CONVERTED LISTS:" + "speed = " + listSpeed);
                         */
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                generateMock.setVisibility(View.INVISIBLE);
            }
        });
        //**************************************************************************************
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
            distanceTitleTextView.setVisibility(View.VISIBLE);
            distanceTextView.setVisibility(View.VISIBLE);
            distanceFrameLayout.setVisibility(View.VISIBLE);
        chronometer.start();
        }
    }

    private void resetWorkoutDistance()
    {
        LocationService.WORKOUT_DISTANCE = 0;
        TextView distanceTextView = getActivity().findViewById(R.id.text_distance_rt);
        DecimalFormat dec_0 = new DecimalFormat("#0"); //0 decimal places https://stackoverflow.com/questions/14845937/java-how-to-set-precision-for-double-value
        if (distanceTextView != null)
        {
            distanceTextView.setText(dec_0.format(LocationService.WORKOUT_DISTANCE)); //Update the Heart Rate TextView (Real Time)
        }
    }

    /*
    This methods displays an alert dialog prior to beginning a workout.
    It informs the user to secure their phone and make sure the sensor connection is stable.
    The user has the ability to proceed or cancel.
     */
    private void startRecording()
    {
        //First we create a dialog to be displayed to the user
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.text_record_workout));
        builder.setCancelable(true);
        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //If user selects continue, then we begin a workout
                //******************************************************************************
                resetWorkoutDistance(); //Reset the workout distance before we display it
                chronometer.setVisibility(View.VISIBLE);
                distanceTextView.setVisibility(View.VISIBLE);
                distanceTitleTextView.setVisibility(View.VISIBLE);
                distanceFrameLayout.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                RecordWorkout.setText("Stop Recording");
                running=true;
                createRecordingService();
                //******************************************************************************
            }
        });
        //If the user selects close, then we disregard
        //**************************************************************************************
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //**************************************************************************************
    }

    /*
    This method creates a recording service.
     */
    public void createRecordingService()
    {
        Log.d(TAG,"createRecordingService()");
        getActivity().startService(new Intent(getActivity(), RecordingService.class));
    }

    /*
    This method STOPS the recording service.
     */
    public void stopRecordingService()
    {
        Log.d(TAG,"createRecordingService()");
        getActivity().stopService(new Intent(getActivity(), RecordingService.class));
    }

    //**************************************************************************************
    //THIS IS JUST FOR POPULATING MOCK DATA
    public String loadJSONfromAsset(){
        String json = null;
        try{
            InputStream inputStream = getActivity().getAssets().open("MockData");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
    public Double mockFilter(List<Long> time, List<Double> speed, List<Double> heartRate, double weight, int age){
        //Kalman filter parameters and values
        int N = time.size();
        double C = 1.0;
        double Q = 0.05;
        double sigma = Q;
        List<Double> pGuess = new ArrayList<>();
        double newVal;
        double K;
        long delta_time;
        double MET;
        //Retrieve user profile

        //initial guess
        newVal = ((-55.0969 + (0.6309*heartRate.get(0)) + (0.1988*weight) + (0.2017*age))/4.184)*(time.get(1)-time.get(0))*(1/60);  // kcal/min
        pGuess.add(newVal);

        newVal = 5*weight/60;        // kcal/min
        K = (sigma*sigma * C)/(sigma*sigma * C*C + Q*Q);
        pGuess.set(0, pGuess.get(0) + K*(newVal-C*pGuess.get(0)));
        sigma = (1-K*C)*sigma;

        for(int i = 1; i < N; i++) {
            delta_time = time.get(i) - time.get(i - 1);
            if(speed.get(i) < 11)          MET = 4.8;
            else if(speed.get(i) < 16)     MET = 5.9;
            else if(speed.get(i) < 21)     MET = 7.1;
            else if(speed.get(i) < 26)     MET = 8.4;
            else                           MET = 9.8;

            //update guess with HR data
            newVal = ((-55.0969 + (0.6309 * heartRate.get(i)) + (0.1988 * weight) + (0.2017 * age)) / 4.184) * delta_time * (1 / 60);  //kcal / min
            K = (sigma*sigma * C)/(sigma*sigma * C*C + Q*Q);
            pGuess.add(pGuess.get(i - 1) + K * (newVal - C * pGuess.get(i - 1)));
            sigma = (1 - K * C) * sigma;


            //update guess with Speed data
            newVal = MET * weight / 60;        // kcal / min
            K = (sigma*sigma * C)/(sigma*sigma * C*C + Q*Q);
            pGuess.set(i,pGuess.get(i - 1) + K * (newVal - C * pGuess.get(i - 1)));
            sigma = (1 - K * C) * sigma;
        }
        return pGuess.get(N-1);
    }
   //Takes user info from shared preferences and stores it for use in the caloric estimation.
    public void getUserInfo(double userWeight, int userAge){
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(getContext());
        if(sharedPreferenceHelper.getProfile()){
            userAge = sharedPreferenceHelper.getProfileAge();
            userWeight = sharedPreferenceHelper.getProfileWeight();
        } else{
            Toast.makeText(getContext(),"No Profile Found, Please create a profile " ,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
        Log.d(TAG,"\ngetUserInfo\nAge: " + userAge + "\nWeight: " + userWeight);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Instant dateAfter(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long random = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds);

        return Instant.ofEpochSecond(random);
    }
    //**************************************************************************************
}
