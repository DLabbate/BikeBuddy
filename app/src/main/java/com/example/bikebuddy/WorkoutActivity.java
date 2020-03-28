package com.example.bikebuddy;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Utils.Workout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkoutActivity extends AppCompatActivity {

    /*
    This activity is to display detailed information of a single workout
    - HR Graph
    - Speed Graph
    - Calories burned
    (...)
     */

    //Id passed from intent
    int __ID;
    Workout workout;
    DbHelper dbHelper;

    //Fields to be populated
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

        dbHelper = new DbHelper(this);
        setupUI();

        __ID = getIntent().getIntExtra("__ID",-1);

        try {
            workout = dbHelper.retrieveWorkout(__ID);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateText.setText(dateToString(workout.getDate()));

        //converting the time from long into hours, minutes and seconds
        long hours = TimeUnit.SECONDS.toHours(workout.getTotalDuration());
        long minute =TimeUnit.SECONDS.toMinutes(workout.getTotalDuration())-hours*60;
        long seconds = TimeUnit.SECONDS.toSeconds(workout.getTotalDuration())-TimeUnit.SECONDS.toMinutes(workout.getTotalDuration())*60;;
        String displayDuration = hours + "h, " + minute + "m, "+ seconds + "s";
        DurationText.setText(displayDuration);

        // converting the distance from double to int to get rid of the decimals (we are measuring the distance in meter)
        String displayDistance = Integer.toString((int)workout.getTotalDistance()) + " m";
        DistanceText.setText(displayDuration);

        // Importing remaining data from workout object
        CaloriesText.setText(Integer.toString((int)workout.getCaloriesBurned()));
        AverageSpeedText.setText(Double.toString(workout.getAverageSpeed()));
        AverageHRText.setText(Double.toString(workout.getAverageHR()));
        List<Long> time = workout.getTime();
        List<Double> heartRate = workout.getListHR();
        List<Double> speed = workout.getListSpeed();

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

        DateText = findViewById(R.id.text_date_value);
        DurationText = findViewById(R.id.text_duration_value);
        DistanceText = findViewById(R.id.text_distance_value);
        AverageHRText = findViewById(R.id.text_heart_rate_value);
        AverageSpeedText = findViewById(R.id.text_speed_value);
        CaloriesText = findViewById(R.id.text_calories_value);
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
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData lineData = new LineData(dataSets);
        chart_HR.setData(lineData);
        chart_HR.getXAxis().setDrawLabels(false); //X-axis not visible for now
        chart_HR.getDescription().setEnabled(false); //Description not visible for now

        //Remove circles
        lineDataSet1.setDrawCircles(false);

        //Add gradient fill
        //See https://stackoverflow.com/questions/32907529/mpandroidchart-fill-color-gradient
        lineData.setDrawValues(false);

        lineDataSet1.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.gradient_chart);
            lineDataSet1.setFillDrawable(drawable);
        }
        else {
            lineDataSet1.setFillColor(Color.BLACK);
        }
        
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
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        LineData lineData = new LineData(dataSets);
        chart_Speed.setData(lineData);
        chart_Speed.getXAxis().setDrawLabels(false); //X-axis not visible for now
        chart_Speed.getDescription().setEnabled(false); //Description not visible for now

        //Remove circles
        lineDataSet1.setDrawCircles(false);

        //Add gradient fill
        //See https://stackoverflow.com/questions/32907529/mpandroidchart-fill-color-gradient
        lineData.setDrawValues(false);

        lineDataSet1.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.gradient_chart);
            lineDataSet1.setFillDrawable(drawable);
        }
        else {
            lineDataSet1.setFillColor(Color.BLACK);
        }

        chart_Speed.invalidate();
    }

    //Converts imported date to a string for display in textView
    private String dateToString(Date date){
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        String dateString = df.format(date);
        return dateString;
    }
}
