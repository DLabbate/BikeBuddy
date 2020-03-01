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
    ArrayList<Entry> data_HR;

    public static final String TAG = "WorkoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Log.d(TAG,"onCreate");

        setupUI();
        data_HR = new ArrayList<Entry>();

        for (int i = 0; i < 5; i++) {
            data_HR.add(new Entry(i, i + 1));
        }

        //https://www.youtube.com/watch?v=yrbgN2UvKGQ
        LineDataSet lineDataSet1 = new LineDataSet(data_HR,"line data set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData lineData = new LineData(dataSets);
        chart_HR.setData(lineData);
        chart_HR.getXAxis().setDrawLabels(false);
        chart_HR.invalidate();
    }

    private void setupUI()
    {
        chart_HR = findViewById(R.id.line_chart_HR);
    }
}
