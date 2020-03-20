package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.bikebuddy.Utils.Bike;
import com.example.bikebuddy.Utils.BikeAdapter;

import java.util.ArrayList;

public class BikeActivity extends AppCompatActivity {

    //Toolbar
    //***********************************************************************************************
    ImageView backImageView;
    //***********************************************************************************************

    //Bikes
    //***********************************************************************************************
    ArrayList<Bike> bikes;
    //***********************************************************************************************

    //RecyclerView
    //***********************************************************************************************
    RecyclerView recyclerViewBikes;
    RecyclerView.Adapter bikeAdapter;
    RecyclerView.LayoutManager linearLayoutManager;
    //***********************************************************************************************


    //SharedPreferences
    //***********************************************************************************************
    SharedPreferenceHelper sharedPreferenceHelper;
    //***********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike);

        sharedPreferenceHelper = new SharedPreferenceHelper(BikeActivity.this);

        setupUI();
        setupToolbar();

        testData();
        loadBikes();
    }

    private void setupUI()
    {
        backImageView = findViewById(R.id.image_bike_back);
        recyclerViewBikes = findViewById(R.id.recycler_view_bikes);
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

    /*
    This is for testing purposes only, until database has been set up for bike table
     */
    private void testData()
    {
        bikes = new ArrayList<Bike>();
        bikes.add(new Bike(1,"Bike 1","Trek","123456",30,10000,100000));
        bikes.add(new Bike(2,"Bike 2","Brand 2","654321",31,10000,100000));
        bikes.add(new Bike(3,"Bike 3","Brand 3","171717",32,10000,100));
    }

    public void loadBikes()
    {
        bikeAdapter = new BikeAdapter(bikes,sharedPreferenceHelper);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewBikes.setAdapter(bikeAdapter);
        recyclerViewBikes.setLayoutManager(linearLayoutManager);
    }
}
