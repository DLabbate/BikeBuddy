package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

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

    public static final String TAG = "WorkoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Log.d(TAG,"onCreate");

        setupUI();
        loadDataHR();
        loadDataSpeed();
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
    This method will be used for populating the data of the graphs
     */
    private void loadDataHR()
    {
        data_HR = new ArrayList<Entry>();

        //These values are only for testing purposes.
        for (int i = 0; i < 5; i++) {
            data_HR.add(new Entry(i, i*i));
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

    private void loadDataSpeed()
    {
        data_speed = new ArrayList<Entry>();

        //These values are only for testing purposes.
        for (int i = 0; i < 5; i++) {
            data_speed.add(new Entry(i, i + 1));
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
