package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        setupUI();

        loadDataTest();
    }

    /*
    Connect views to xml
     */
    private void setupUI()
    {
        imageViewPerformance = findViewById(R.id.image_performance_back);
        lineChartPerformance = findViewById(R.id.lineChartPerformance);

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

}
