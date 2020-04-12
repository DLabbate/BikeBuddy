package com.example.bikebuddymock.Utils;

import android.util.Log;

import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class Workout {

    private int ID;
    private Date date;
    private List <Long> time;
    private List <Double> listHR;
    private List <Double> listSpeed;
    private List <Double> listLatCoords;
    private List <Double> listLngCoords;
    private double totalDistance;
    private long totalDuration;
    private double caloriesBurned;
    private double caloriesRate;
    private double averageHR;
    private double averageSpeed;
    private int maxHR;

    // Empty Constructor
    public Workout(){}

    // Constructor With date set explicitly
    public Workout(Date date, List<Long> time, List<Double> listHR, List<Double> listSpeed, double totalDistance, long totalDuration, double caloriesBurned, double averageHR, double averageSpeed) {
        this.date = date;
        this.time = time;
        this.listHR = listHR;
        this.listSpeed = listSpeed;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.caloriesBurned = caloriesBurned;
        this.averageHR = averageHR;
        this.maxHR = calculateMaxHR();
        this.averageSpeed = averageSpeed;
    }

    // Constructor without date
    public Workout(List<Long> time, List<Double> listHR, List<Double> listSpeed, double totalDistance, long totalDuration, double caloriesBurned, double averageHR, double averageSpeed) {
        this.date=  Calendar.getInstance().getTime();
        this.time = time;
        this.listHR = listHR;
        this.listSpeed = listSpeed;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.caloriesBurned = caloriesBurned;
        this.averageHR = averageHR;
        this.maxHR = calculateMaxHR();
        this.averageSpeed = averageSpeed;
    }


    // Constructor without average values
    // Date is not included either
    public Workout(List<Long> time, List<Double> listHR, List<Double> listSpeed, double totalDistance, long totalDuration, double caloriesRate) {
        this.date=  Calendar.getInstance().getTime();
        this.time = time;
        this.listHR = listHR;
        this.listSpeed = listSpeed;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.caloriesBurned = calculateCaloriesBurned(caloriesRate);
        this.caloriesRate = caloriesRate;
        this.averageHR = calculateAverageHR();
        this.maxHR = calculateMaxHR();
        this.averageSpeed = calculateAverageSpeed();
        this.maxHR = calculateMaxHR();
    }

    // Constructor without average values
    // Date is not included either
    //Includes LatLng lists
    public Workout(List<Long> time, List<Double> listHR, List<Double> listSpeed, List<Double> listLatCoords, List<Double> listLngCoords, double totalDistance, long totalDuration, double caloriesRate) {
        this.date=  Calendar.getInstance().getTime();
        this.time = time;
        this.listHR = listHR;
        this.listSpeed = listSpeed;
        this.listLatCoords = listLatCoords;
        this.listLngCoords = listLngCoords;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.caloriesBurned = calculateCaloriesBurned(caloriesRate);
        this.caloriesRate = caloriesRate;
        this.averageHR = calculateAverageHR();
        this.averageSpeed = calculateAverageSpeed();
    }




    //Setters and Getters
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public List<Long> getTime() {
        return time;
    }
    public void setTime(List<Long> time) {
        this.time = time;
    }
    public List<Double> getListHR() {
        return listHR;
    }
    public void setListHR(List<Double> listHR) {
        this.listHR = listHR;
    }
    public List<Double> getListSpeed() {
        return listSpeed;
    }
    public void setListSpeed(List<Double> listSpeed) {
        this.listSpeed = listSpeed;
    }

    public List<Double> getListLatCoords() {
        return listLatCoords;
    }

    public void setListLatCoords(List<Double> listLatCoords) {
        this.listLatCoords = listLatCoords;
    }

    public List<Double> getListLngCoords() {
        return listLngCoords;
    }

    public void setListLngCoords(List<Double> listLngCoords) {
        this.listLngCoords = listLngCoords;
    }

    public double getTotalDistance() {
        return totalDistance;
    }



    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
    public long getTotalDuration() {
        return totalDuration;
    }
    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }
    public double getCaloriesBurned() {
        return caloriesBurned;
    }
    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
    public double getAverageHR() {
        return averageHR;
    }
    public void setAverageHR(double averageHR) {
        this.averageHR = averageHR;
    }
    public double getAverageSpeed() {
        return averageSpeed;
    }
    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public int getMaxHR() {
        return maxHR;
    }
    public void setMaxHR(int maxHR) {
        this.maxHR = maxHR;
    }
    public double getCaloriesRate() {
        return caloriesRate;
    }
    public void setCaloriesRate(double caloriesRate) {
        this.caloriesRate = caloriesRate;
    }


    //Averaging Calculations
    public double calculateAverageHR(){
        if (this.listHR.size()!=0) {// to make sure that we don't divide by zero
            double sum = 0;
            for (int i = 0; i < this.listHR.size(); i++) {
                sum += this.listHR.get(i);
            }

            return (sum / this.listHR.size());
        }
        return -1; // when the method returns -1, that means the list is empty
    }
    public double calculateAverageSpeed(){
        if (this.listSpeed.size()!=0) { // to make sure that we don't divide by zero
            double sum = 0;
            for (int i = 0; i < this.listSpeed.size(); i++) {
                sum += this.listSpeed.get(i);
            }

            return (sum / this.listSpeed.size());
        }
        return -1; // when the method returns -1, that means the list is empty
    }
    public double calculateCaloriesBurned(double calRate) {
        caloriesBurned = calRate * time.get(time.size()-1) / 60;   //returns total burned
        return caloriesBurned;
    }
    public int calculateMaxHR() {
        return Collections.max(listHR).intValue();
    }
    /*
    Prints all the data of a workout in a log
    Used for debugging
     */
    public void print(String TAG)
    {
        String workoutData = " \nWORKOUT DATA: \n";
        workoutData += "********************************************************************\n";

        workoutData += " Date: " + date;

        /*
        workoutData += " \n Times (seconds): ";
        for (int i = 0; i < time.size(); i++)
            workoutData += time.get(i).toString() + " ";
        workoutData += "\n";

        workoutData += " HR List: ";
        for (int i = 0; i < listHR.size(); i++)
            workoutData += listHR.get(i).toString() + " ";
        workoutData += "\n";

        workoutData += " Speed List: ";
        for (int i = 0; i < listSpeed.size(); i++)
            workoutData += listSpeed.get(i).toString() + " ";
        workoutData += "\n";
         */

        workoutData += " Total Distance: " + totalDistance + "\n";
        workoutData += " Total Duration: " + totalDuration + "\n";
        workoutData += " Calories Rate(cal/min): " + caloriesRate + "\n";
        workoutData += " Calories Burned: " + caloriesBurned + "\n";
        workoutData += " Average HR: " + averageHR + "\n";
        workoutData += " Maximum HR: " + maxHR + "\n";
        workoutData += " Average Speed: " + averageSpeed + "\n";

        workoutData += "********************************************************************\n";

        Log.d(TAG,workoutData);
    }
}
