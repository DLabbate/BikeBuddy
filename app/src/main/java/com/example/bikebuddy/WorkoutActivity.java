package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    /*
    This activity is to display detailed information of a single workout
    - HR Graph
    - Speed Graph
    - Calories burned
    (...)
     */

    LineChart chart_HR;
    LineChart chart_Speed;
    ArrayList<Entry> data_HR;
    ArrayList<Entry> data_speed;
    TextView DateText;
    TextView DurationText;
    TextView DistanceText;
    TextView AverageHRText;
    TextView AverageSpeedText;
    TextView CaloriesText;


    public static final String TAG = "WorkoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Log.d(TAG,"onCreate");

        setupUI();


        DateText = findViewById(R.id.text_date_value);
        DurationText = findViewById(R.id.text_duration_value);
        DistanceText = findViewById(R.id.text_distance_value);
        AverageHRText = findViewById(R.id.text_heart_rate_value);
        AverageSpeedText = findViewById(R.id.text_speed_value);
        CaloriesText = findViewById(R.id.text_calories_value);

        DateText.setText(getIntent().getStringExtra("Date"));
        DurationText.setText(getIntent().getStringExtra("Duration"));
        DistanceText.setText(getIntent().getStringExtra("Distance") + " m");
        CaloriesText.setText(getIntent().getStringExtra("Calories"));
        AverageSpeedText.setText(getIntent().getStringExtra("AverageSpeed"));
        AverageHRText.setText(getIntent().getStringExtra("AverageHR"));

        // Deserializing the lists sent from log fragment
        Log.d(TAG, "Deserializing from Log Fragment");
        Gson gson = new Gson();
        String JSONtime = getIntent().getStringExtra("Time");
        String JSONhr =  getIntent().getStringExtra("HRList");
        String JSONspeed = getIntent().getStringExtra("SpeedList");

        Type listType_long = new TypeToken<Collection<Long>>() {
        }.getType();
        Type listType_double = new TypeToken<Collection<Double>>() {
        }.getType();
        List<Long> time = gson.fromJson(JSONtime, listType_long);
        List<Double> heartRate = gson.fromJson(JSONhr, listType_double);
        List<Double> speed = gson.fromJson(JSONspeed, listType_double);

        loadDataHR(heartRate);
        loadDataSpeed(speed);
    }

    /*
    This method is used for setting up the UI elements
    (associating the references with the appropriate views in the xml layout files)
     */
    private void setupUI()
    {
        chart_HR = findViewById(R.id.line_chart_HR);
        chart_Speed = findViewById(R.id.line_chart_speed);
    }

    /*
    This method will be used for populating the data of the HR graph
     */
    private void loadDataHR(List<Double> HR)
    {
        data_HR = new ArrayList<Entry>();

        //These values are only for testing purposes.
        for (int i = 0; i < HR.size() ; i++) {
            data_HR.add(new Entry(i, (HR.get(i).floatValue())));
        }

        //https://www.youtube.com/watch?v=yrbgN2UvKGQ
        LineDataSet lineDataSet1 = new LineDataSet(data_HR,"HR Data Set");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData lineData = new LineData(dataSets);
        chart_HR.setData(lineData);
        chart_HR.getXAxis().setDrawLabels(false); //X-axis not visible for now
        chart_HR.getDescription().setEnabled(false); //Description not visible for now
        chart_HR.invalidate();
    }

    /*
    This method will be used for populating the data of the speed graph
     */
    private void loadDataSpeed(List<Double> Speed)
    {
        data_speed = new ArrayList<Entry>();

        //These values are only for testing purposes.
        for (int i = 0; i < Speed.size(); i++) {
            data_speed.add(new Entry(i, Speed.get(i).floatValue()));
        }

        //https://www.youtube.com/watch?v=yrbgN2UvKGQ
        LineDataSet lineDataSet1 = new LineDataSet(data_speed,"Speed Data Set");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData lineData = new LineData(dataSets);
        chart_Speed.setData(lineData);
        chart_Speed.getXAxis().setDrawLabels(false); //X-axis not visible for now
        chart_Speed.getDescription().setEnabled(false); //Description not visible for now
        chart_Speed.invalidate();
    }
}
