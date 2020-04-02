package com.example.bikebuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Utils.Workout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class PerformanceActivity extends AppCompatActivity {

    //Toolbar
    //***************************************************************************************************
    ImageView imageViewPerformance;
    //***************************************************************************************************

    //Tag
    //***************************************************************************************************
    public static final String TAG = "PerformanceActivity";
    //***************************************************************************************************

    //Chart
    //***************************************************************************************************
    private LineChart lineChartPerformance;
    //***************************************************************************************************

    //Spinners
    //***************************************************************************************************
    private Spinner spinnerParameter;
    private Spinner spinnerNumberWorkouts;

    private int currentParameter;
    private int currentNumberWorkouts;
    //***************************************************************************************************

    //Database
    //***************************************************************************************************
    DbHelper dbHelper;
    //***************************************************************************************************

    //Workouts
    //***************************************************************************************************
    List<Workout> filteredWorkoutList;
    //***************************************************************************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        setupUI();
        setupDB();

        //loadDataTest();
        setupSpinnerParameter();
        setupSpinnerNumberWorkouts();
    }

    /*
        Connect views to xml
         */
    private void setupUI()
    {
        imageViewPerformance = findViewById(R.id.image_performance_back);
        lineChartPerformance = findViewById(R.id.lineChartPerformance);
        initializeSpinners();

        //Setup navigation to main activity (BACK BUTTON)
        if (imageViewPerformance != null)
        {
            imageViewPerformance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PerformanceActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupDB()
    {
        dbHelper = new DbHelper(this);
        filteredWorkoutList = dbHelper.getWorkouts();
        orderByDate();
    }

    private void initializeSpinners()
    {
        spinnerParameter = findViewById(R.id.spinnerParameter);
        spinnerNumberWorkouts = findViewById(R.id.spinnerNumberWorkouts);

        currentParameter = spinnerParameter.getSelectedItemPosition();
        currentNumberWorkouts = spinnerNumberWorkouts.getSelectedItemPosition();
        Log.d(TAG, "Initial Spinner Positions (parameter, number of workouts) --> " + "(" + currentParameter +"," + currentNumberWorkouts + ")");
    }

    private void loadDataTest()
    {
        ArrayList<Entry> data_HR = new ArrayList<Entry>();

        //These values are only for testing purposes.
        Random r = new Random();
        for (int i = 0; i < 50 ; i++) {
            int low = 10;
            int high = 100;
            int result = r.nextInt(high-low) + low;
            data_HR.add(new Entry(i, result));
        }

        //https://www.youtube.com/watch?v=yrbgN2UvKGQ
        LineDataSet lineDataSet1 = new LineDataSet(data_HR,"HR Data Set");
        //lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData lineData = new LineData(dataSets);
        lineChartPerformance.setData(lineData);
        lineChartPerformance.getXAxis().setDrawLabels(false); //X-axis not visible for now
        lineChartPerformance.getDescription().setEnabled(false); //Description not visible for now

        //Remove circles
        lineDataSet1.setDrawCircles(true);

        //Add gradient fill
        //See https://stackoverflow.com/questions/32907529/mpandroidchart-fill-color-gradient
        lineData.setDrawValues(false);


        /*
        lineDataSet1.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.gradient_chart);
            lineDataSet1.setFillDrawable(drawable);
        }
        else {
            lineDataSet1.setFillColor(Color.BLACK);
        }

         */

        lineChartPerformance.invalidate();
    }


    private void setupSpinnerParameter(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PerformanceActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.performanceParameters));

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerParameter.setAdapter(arrayAdapter);

        spinnerParameter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    currentParameter = position;
                    Log.d(TAG,"Position: " +position);
                    Log.d(TAG,"Selected HR");
                    Toast.makeText(PerformanceActivity.this,"HR",Toast.LENGTH_SHORT).show();
                    loadChartData();
                }

                if(position == 1){
                    currentParameter = position;
                    Log.d(TAG,"Position: " +position);
                    Log.d(TAG,"Selected Calories");
                    Toast.makeText(PerformanceActivity.this,"Calories",Toast.LENGTH_SHORT).show();
                    loadChartData();
                }

                if(position == 2){
                    currentParameter = position;
                    Log.d(TAG,"Position: " +position);
                    Log.d(TAG,"Selected Distance");
                    Toast.makeText(PerformanceActivity.this,"Distance",Toast.LENGTH_SHORT).show();
                    loadChartData();
                }

                if(position == 3){
                    currentParameter = position;
                    Log.d(TAG,"Position: " +position);
                    Log.d(TAG,"Selected Duration");
                    Toast.makeText(PerformanceActivity.this,"Duration",Toast.LENGTH_SHORT).show();
                    loadChartData();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupSpinnerNumberWorkouts(){
       // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PerformanceActivity.this,
       // android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.performanceNumberWorkouts));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.performanceNumberWorkouts,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinnerNumberWorkouts.setAdapter(arrayAdapter);
        spinnerNumberWorkouts.setAdapter(adapter);

        spinnerNumberWorkouts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    currentNumberWorkouts = position;
                    Log.d(TAG,"Position: " +position);
                    Log.d(TAG,"Selected Last 10 Workouts");
                    Toast.makeText(PerformanceActivity.this,"Last 10 Workouts",Toast.LENGTH_SHORT).show();
                    loadChartData();
                }

                if(position == 1){
                    currentNumberWorkouts = position;
                    Log.d(TAG,"Position: " +position);
                    Log.d(TAG,"Selected Last 50 Workouts");
                    Toast.makeText(PerformanceActivity.this,"Last 50 Workouts",Toast.LENGTH_SHORT).show();
                    loadChartData();
                }

                if(position == 2){
                    currentNumberWorkouts = position;
                    Log.d(TAG,"Position: " +position);
                    Log.d(TAG,"Selected All Workouts");
                    Toast.makeText(PerformanceActivity.this,"All Workouts",Toast.LENGTH_SHORT).show();
                    loadChartData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    //these functions are used to sort the current list of workouts
    private void orderByDate() {
        Collections.sort(filteredWorkoutList, new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return o2.getDate().compareTo(o1.getDate());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
    }

    public void loadChartData()
    {
        ArrayList<Entry> data = new ArrayList<Entry>();

        int totalWorkouts;
        if (currentNumberWorkouts == 0)
        {
            totalWorkouts = Math.min(10,filteredWorkoutList.size()); //If there are less than 10 workouts, we take the entire list
        }
        else if(currentNumberWorkouts == 1)
        {
            totalWorkouts = Math.min(50,filteredWorkoutList.size()); //If there are less than 50 workouts, we take the entire list
        }

        else if (currentNumberWorkouts == 2)
        {
            totalWorkouts = filteredWorkoutList.size();
        }

        else
        {
            totalWorkouts = 0;
        }

        try
        {
            for (int i = 0; i < totalWorkouts; i++)
            {
                Workout currentWorkout = filteredWorkoutList.get(totalWorkouts - i - 1);

                /*
                NOTE the entry x-axis is "totalWorkouts - i" so that index 0 is the oldest in the list
                 */
                switch (currentParameter)
                {
                    case 0:
                        Log.d(TAG,"Adding a chart value: " + (float) currentWorkout.getAverageHR());
                        data.add(new Entry(i,((float) currentWorkout.getAverageHR())));
                        break;
                    case 1:
                        Log.d(TAG,"Adding a chart value: " + (float) currentWorkout.getAverageHR());
                        data.add(new Entry(i,((float) currentWorkout.getCaloriesBurned())));
                        break;
                    case 2:
                        Log.d(TAG,"Adding a chart value: " + (float) currentWorkout.getAverageHR());
                        data.add(new Entry(i,((float) currentWorkout.getTotalDistance())));
                        break;
                    case 3:
                        Log.d(TAG,"Adding a chart value: " + (float) currentWorkout.getAverageHR());
                        double hours = (double) (currentWorkout.getTotalDuration())/((double) 3600);
                        //long minute = TimeUnit.SECONDS.toMinutes(currentWorkout.getTotalDuration())-hours*60;
                        //long seconds = TimeUnit.SECONDS.toSeconds(currentWorkout.getTotalDuration())-TimeUnit.SECONDS.toMinutes(currentWorkout.getTotalDuration())*60;;
                        data.add(new Entry(i,(float) (hours)));
                        break;
                    default:
                        break;
                }
            }
        }

        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }

        //https://www.youtube.com/watch?v=yrbgN2UvKGQ
        LineDataSet lineDataSet1 = new LineDataSet(data,"Performance Data");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData lineData = new LineData(dataSets);
        lineChartPerformance.setData(lineData);
        //lineChartPerformance.getXAxis().setDrawLabels(false); //X-axis not visible for now
        lineChartPerformance.getDescription().setEnabled(false); //Description not visible for now

        lineDataSet1.setDrawCircles(true);

        lineDataSet1.setLineWidth(3f);
        lineDataSet1.setCircleRadius(7f);
        lineChartPerformance.animateX(2000);
        //Add gradient fill
        //See https://stackoverflow.com/questions/32907529/mpandroidchart-fill-color-gradient
        lineData.setDrawValues(false);

        lineChartPerformance.invalidate();
    }


}
