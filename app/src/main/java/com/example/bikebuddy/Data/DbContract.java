package com.example.bikebuddy.Data;

import android.provider.BaseColumns;
/*
    This class holds the format for table to be made with DbHelper
 */
public class DbContract {
    public static final class WorkoutEntry implements BaseColumns{
        //TABLE NAME
        public static final String TABLE_NAME = "workouts";
        //WORKOUTS TABLE COLUMNS
        public static final String COLUMN_DATE = "workoutDate";
        public static final String COLUMN_DURATION = "workoutDuration";
        public static final String COLUMN_DISTANCE = "workoutDistance";
        public static final String COLUMN_TIME_LIST = "workoutTimeList";
        public static final String COLUMN_HR_LIST = "workoutHRlist";
        public static final String COLUMN_SPEED_LIST = "workoutSpeedList";
        public static final String COLUMN_HR_AVG = "workoutAvgHR";
        public static final String COLUMN_SPEED_AVG = "workoutAvgSpeed";
        public static final String COLUMN_CALORIES_TOT = "workoutCaloriesTotal";
        public static final String COLUMN_CALORIES_RATE = "workoutCaloriesRate";

        //Not used yet
        public static final String COLUMN_BIKE_USED = "workoutBikeUsed";
    }

    public static final class BikeEntry implements BaseColumns{
        //TABLE NAME
        public static final String TABLE_NAME = "bikes";
        //BIKES TABLE COLUMNS
        public static final String COLUMN_BRAND = "bikeBrand";
        public static final String COLUMN_MODEL = "bikeModel";
        public static final String COLUMN_WHEELDIAMETER = "bikeWheelDiameter";
        public static final String COLUMN_CUMULATIVEDISTANCE = "bikeDistance";
    }

}
