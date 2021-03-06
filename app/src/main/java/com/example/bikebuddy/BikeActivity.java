package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Utils.Bike;
import com.example.bikebuddy.Utils.BikeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BikeActivity extends AppCompatActivity {

    //Toolbar
    //***********************************************************************************************
    ImageView backImageView;
    //***********************************************************************************************

    //Bikes
    //***********************************************************************************************
    List<Bike> bikes;
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

    //Floating Action Button
    //***********************************************************************************************
    FloatingActionButton FABaddBike;
    //***********************************************************************************************

    //Database
    //***********************************************************************************************
    DbHelper dbHelper;
    //***********************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike);

        sharedPreferenceHelper = new SharedPreferenceHelper(BikeActivity.this);

        setupUI();
        setupToolbar();

        //testData();
        setupDB();
        loadBikes();

        setupFAB();
    }

    private void setupUI()
    {
        backImageView = findViewById(R.id.image_bike_back);
        recyclerViewBikes = findViewById(R.id.recycler_view_bikes);
        FABaddBike = findViewById(R.id.FAB_add_bike);
    }

    private void setupDB()
    {
        dbHelper = new DbHelper(this);
        bikes = dbHelper.getBikes();
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

    private void setupFAB()
    {
        FABaddBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddBikeFragment addBikeFragment = new AddBikeFragment();
                addBikeFragment.show(getSupportFragmentManager(),"Add Bike Fragment");
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
        bikes = dbHelper.getBikes();
        bikeAdapter = new BikeAdapter(bikes,sharedPreferenceHelper,this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewBikes.setAdapter(bikeAdapter);
        recyclerViewBikes.setLayoutManager(linearLayoutManager);
    }
}
