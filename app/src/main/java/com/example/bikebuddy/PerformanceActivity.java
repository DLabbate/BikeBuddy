package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class PerformanceActivity extends AppCompatActivity {

    //Toolbar
    //***************************************************************************************************
    ImageView imageViewPerformance;
    //***************************************************************************************************

    //Tag
    //***************************************************************************************************
    public static final String TAG = "PerformanceActivity";
    //***************************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        setupUI();
    }

    /*
    Connect views to xml
     */
    private void setupUI()
    {
        imageViewPerformance = findViewById(R.id.image_performance_back);

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
}
