package com.example.bikebuddy.Utils;

import android.util.Log;

public class Bike {

    private int ID;
    private String brand;
    private String model;
    private double wheelDiameter;
    private double cumulativeDistance;

    //CONSTRUCTORS
    public Bike() {
    }



    public Bike(int ID, String brand, String model, double wheelDiameter, double cumulativeDistance) {
        this.ID = ID;
        this.brand = brand;
        this.model = model;
        this.wheelDiameter = wheelDiameter;
        this.cumulativeDistance = cumulativeDistance;
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
