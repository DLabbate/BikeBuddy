package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class BikeActivity extends AppCompatActivity {

    //Toolbar
    //***********************************************************************************************
    ImageView backImageView;
    //***********************************************************************************************


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike);

        setupUI();
        setupToolbar();
    }

    private void setupUI()
    {
        backImageView = findViewById(R.id.image_bike_back);
    }

    private void setupToolbar()
    {
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BikeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
