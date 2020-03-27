package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.bikebuddy.Utils.HeartRateZoneHelper;
import com.example.bikebuddy.Utils.PercentFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
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

    //----------------------------------------Workout Data----------------------------------------------//

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

    //----------------------------------------HR Zones------------------------------------------------//
    HeartRateZoneHelper heartRateZoneHelper;
    PieChart pieChartZones;
    Context context;

    public static final String TAG = "WorkoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Log.d(TAG,"onCreate");

        context = this;
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
        loadDateHRZones(heartRate);
    }

    /*
    This method is used for setting up the UI elements
    (associating the references with the appropriate views in the xml layout files)
     */
    private void setupUI()
    {
        chart_HR = findViewById(R.id.line_chart_HR);
        chart_Speed = findViewById(R.id.line_chart_speed);
        heartRateZoneHelper = new HeartRateZoneHelper(this);
        pieChartZones = findViewById(R.id.pie_chart_zones);
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

    /*
    The following method approximates the amount of time spent in each HR Zone
    NOTE THAT IT ASSUMES A SAMPLING RATE OF 3 SECONDS
     */
    private void loadDateHRZones(List<Double> HR)
    {
        int zoneTotals[] = new int[5];

        for (int i = 0; i<HR.size(); i++)
        {
            //Calculate the current zone
            int zone = heartRateZoneHelper.getZone(HR.get(i));
            switch (zone)
            {
                case 1:
                    zoneTotals[0] += 3;
                    break;
                case 2:
                    zoneTotals[1] += 3;
                    break;
                case 3:
                    zoneTotals[2] += 3;
                    break;
                case 4:
                    zoneTotals[3] += 3;
                    break;
                case 5:
                    zoneTotals[4] += 3;
                    break;
            }
        }
        pieChartZones.setUsePercentValues(true);

        pieChartZones.setDragDecelerationFrictionCoef(0.99f);
        pieChartZones.setDrawHoleEnabled(true);
        pieChartZones.setHoleColor(Color.WHITE);
        pieChartZones.setTransparentCircleRadius(55f);
        pieChartZones.setHoleRadius(50f);

        pieChartZones.getDescription().setEnabled(false);
        pieChartZones.setDrawEntryLabels(false);
        //pieChartZones.setEntryLabelColor(Color.WHITE);
        pieChartZones.setDrawCenterText(false);
        pieChartZones.setDrawMarkers(false);

        Legend l = pieChartZones.getLegend();
        l.setWordWrapEnabled(true);

        pieChartZones.setExtraOffsets(10f,10f,10f,10f);

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colours = new ArrayList<>();

        if (zoneTotals[0] != 0)
        {
            pieEntries.add(new PieEntry(zoneTotals[0], getString(R.string.HR_zone1)));
            colours.add(R.color.color_zone1);
        }

        if (zoneTotals[1] != 0)
        {
            pieEntries.add(new PieEntry(zoneTotals[1], getString(R.string.HR_zone2)));
            colours.add(R.color.color_zone2);
        }

        if (zoneTotals[2] != 0)
        {
            pieEntries.add(new PieEntry(zoneTotals[2], getString(R.string.HR_zone3)));
            colours.add(R.color.color_zone3);
        }

        if (zoneTotals[3] != 0)
        {
            pieEntries.add(new PieEntry(zoneTotals[3], getString(R.string.HR_zone4)));
            colours.add(R.color.color_zone4);
        }

        if (zoneTotals[4] != 0)
        {
            pieEntries.add(new PieEntry(zoneTotals[4],getString(R.string.HR_zone5)));
            colours.add(R.color.color_zone5);
        }

        PieDataSet pieDataSet= new PieDataSet(pieEntries,"");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);

        /*
        pieDataSet.setColors(new int[]{
                R.color.color_zone1,
                R.color.color_zone2,
                R.color.color_zone3,
                R.color.color_zone4,
                R.color.color_zone5});

         */

        int colourArray[] = new int[colours.size()];
        for (int i = 0; i < colourArray.length; i++)
        {
            colourArray[i] = colours.get(i);
        }
        pieDataSet.setColors(colourArray,context);
        //pieDataSet.setColors(colours,context);
        //pieDataSet.setColors(new int[] { R.color.color_zone1, R.color.color_zone2, R.color.color_zone3, R.color.color_zone4, R.color.color_zone5 }, context);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueFormatter(new PercentFormatter(pieChartZones));

        //Make %Values outside
        //See https://stackoverflow.com/questions/51493521/manage-text-in-some-situation-piechart-of-mpandroidchart
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueLinePart1OffsetPercentage(10.f);
        pieDataSet.setValueLinePart1Length(0.55f);
        pieDataSet.setValueLinePart2Length(.1f);
        pieData.setValueTextColor(Color.BLACK);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        pieChartZones.setData(pieData);

    }
}
