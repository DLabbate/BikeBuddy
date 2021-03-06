package com.example.bikebuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Models.PolylineData;
import com.example.bikebuddy.Utils.CustomMarkerView;
import com.example.bikebuddy.Utils.HeartRateZoneHelper;
import com.example.bikebuddy.Utils.PercentFormatter;
import com.example.bikebuddy.Utils.Workout;
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
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback {

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


    //----------------------------------------TAG----------------------------------------------------//
    public static final String TAG = "WorkoutActivity";


    //----------------------------------------Toolbar------------------------------------------------//
    ImageView imageViewBack;

    //----------------------------------------MapView------------------------------------------------//
    MapView gMapView;
    GoogleMap gMap = null;
    List<Double> latcoords;
    List<Double> lngcoords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Log.d(TAG,"onCreate");

        context = this;
        dbHelper = new DbHelper(context);

        setupUI();
        setupMapView(savedInstanceState);

        __ID = getIntent().getIntExtra("__ID",-1);

        try {
            workout = dbHelper.retrieveWorkout(__ID);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateText.setText(dateToString(workout.getDate()));
        //for (int i=1;i<workout.getTime().size();i++)
        //Log.d(TAG," last time is : " + durationToTime(workout.getTime().get(workout.getTime().size()-i)));
        //Log.d(TAG," last time is : " + durationToTime(workout.getTime().get(workout.getTime().size()-1)));

        //converting the time from long into hours, minutes and seconds
        long hours = TimeUnit.SECONDS.toHours(workout.getTotalDuration());
        long minute =TimeUnit.SECONDS.toMinutes(workout.getTotalDuration())-hours*60;
        long seconds = TimeUnit.SECONDS.toSeconds(workout.getTotalDuration())-TimeUnit.SECONDS.toMinutes(workout.getTotalDuration())*60;;
        String displayDuration = hours + "h, " + minute + "m, "+ seconds + "s";
        DurationText.setText(displayDuration);

        // distance
        if ( workout.getTotalDistance() > 10000 ){
            DistanceText.setText( String.format("%.2f",(float)workout.getTotalDistance()/1000)  + " km");
        }
        else{
            DistanceText.setText(Integer.toString((int)workout.getTotalDistance()) +" m");
        }



        // Importing remaining data from workout object
        CaloriesText.setText(Integer.toString((int)workout.getCaloriesBurned()));
        AverageSpeedText.setText(Double.toString(workout.getAverageSpeed()));
        AverageHRText.setText(Double.toString(workout.getAverageHR()));
        List<Long> time = workout.getTime();
        List<Double> heartRate = workout.getListHR();
        List<Double> speed = workout.getListSpeed();
        latcoords = workout.getListLatCoords();
        lngcoords = workout.getListLngCoords();

        System.out.println("WORKOUT ACTIVITY ON CREATE  " + latcoords.get(0));

        loadDataHR(heartRate,workout.getTime());
        loadDataSpeed(speed,workout.getTime());
        loadDateHRZones(heartRate);

        //Setup toolbar back button for navigation to main activity
        setupBackButton();
        CustomMarkerView mv = new CustomMarkerView(context, R.layout.marker);

        // set the marker to the chart
        chart_Speed.setMarker(mv);
        chart_HR.setMarker(mv);
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

        heartRateZoneHelper = new HeartRateZoneHelper(this);
        pieChartZones = findViewById(R.id.pie_chart_zones);


    }
    /*
    This method is used for setting up the UI elements regarding the Google Map
    (associating the references with the appropriate views in the xml layout files)
     */
    private void setupMapView(Bundle savedInstanceState){
        gMapView = findViewById(R.id.mapViewLogs);

        gMapView.onCreate(savedInstanceState);
        gMapView.onResume();

        gMapView.getMapAsync(this);
    }

    /*
    This method will be used for populating the data of the HR graph
     */
    private void loadDataHR(List<Double> HR,List<Long> time)
    {
        data_HR = new ArrayList<Entry>();

        //These values are only for testing purposes.
        for (int i = 0; i < HR.size() ; i++) {
            data_HR.add(new Entry((time.get(i).floatValue()), (HR.get(i).floatValue())));
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
    private void loadDataSpeed(List<Double> Speed,List<Long> time)
    {
        data_speed = new ArrayList<Entry>();

        //These values are only for testing purposes.
        for (int i = 0; i < Speed.size(); i++) {
            data_speed.add(new Entry(time.get(i).floatValue(), Speed.get(i).floatValue()));
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
    private String dateToString(Date date) {
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        String dateString = df.format(date);
        return dateString;
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

    private void setupBackButton()
    {
        imageViewBack = findViewById(R.id.image_workout_back);
        final Context context = this;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        addPolylineToMap(latcoords,lngcoords);

        Integer polylineSize = latcoords.size() - 1;
        LatLng destination = new LatLng(latcoords.get(polylineSize),lngcoords.get(polylineSize));

        gMap.addMarker(new MarkerOptions()
                .position(destination));

        gMap.getUiSettings().setAllGesturesEnabled(false);

    }

    private void addPolylineToMap(List<Double> latcoords, List<Double> lngcoords){


        List<LatLng> path = new ArrayList<>();

        for(int i=0; i<latcoords.size(); i++){

            path.add(new LatLng(
                    latcoords.get(i),
                    lngcoords.get(i)));

            System.out.println("ADD POLYLINE TO MAP" + path.get(i));

        }


        Polyline polyline = gMap.addPolyline(new PolylineOptions().addAll(path));

        polyline.setColor(ContextCompat.getColor(context, R.color.colorAccent));

        focusCamera(polyline.getPoints());

    }
    /**
     *Zooms the camera on a route
     */
    public void focusCamera(List<LatLng> LatLngRoute) {

        if (gMap == null || LatLngRoute == null || LatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : LatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 400;
        LatLngBounds latLngBounds = boundsBuilder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        gMap.animateCamera(

                CameraUpdateFactory.newLatLngBounds(latLngBounds,width, height, routePadding),
                600,
                null
        );
    }
    private String durationToTime(double seconds){

        int P1 = (int) seconds % 60;
        int P2 = (int) seconds / 60;
        int P3 = (int) P2 % 60;
        P2 = P2 / 60;

        String time = P2 + ":" + P3 + ":" + P1;
        return time;
    }
}