package com.example.bikebuddy.Utils;

import android.util.Log;

public class Bike {

    private int ID;
    private String name;
    private String brand;
    private String model;
    private double wheelDiameter;
    private double cumulativeDistance;
    private long totalDuration;

    //CONSTRUCTORS
    public Bike() {

        //Initializes integer terms to avoid error in storage
        wheelDiameter = 0;
        cumulativeDistance = 0;
        totalDuration = 0;
    }



    public Bike(int ID,String name, String brand, String model, double wheelDiameter, double cumulativeDistance, long totalDuration) {
        this.ID = ID;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.wheelDiameter = wheelDiameter;
        this.cumulativeDistance = cumulativeDistance;
        this.totalDuration = totalDuration;
    }

    //This constructor also takes the total duration
    public Bike(int ID, String brand, String model, double wheelDiameter, double cumulativeDistance, long totalDuration) {
        this.ID = ID;
        this.brand = brand;
        this.model = model;
        this.wheelDiameter = wheelDiameter;
        this.cumulativeDistance = cumulativeDistance;
        this.totalDuration = totalDuration;
    }

    //SETTERS AND GETTERS
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public double getWheelDiameter() {
        return wheelDiameter;
    }
    public void setWheelDiameter(double wheelDiameter) {
        this.wheelDiameter = wheelDiameter;
    }
    public double getCumulativeDistance() {
        return cumulativeDistance;
    }
    public void setCumulativeDistance(double cumulativeDistance) {
        this.cumulativeDistance = cumulativeDistance;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getTotalDuration() {
        return totalDuration;
    }
    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void print(String TAG)
    {
        String bikeData = " \nBIKE DATA: \n";
        bikeData += "********************************************************************\n";

        bikeData += " ID: " + ID;
        bikeData += " Brand: " + brand + "\n";
        bikeData += " Model: " + model + "\n";
        bikeData += " Wheel Diameter: " + wheelDiameter + "\n";
        bikeData += " Cumulative Distance: " + cumulativeDistance + "\n";

        bikeData += "********************************************************************\n";

        Log.d(TAG,bikeData);
    }
}
