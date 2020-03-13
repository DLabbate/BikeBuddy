package com.example.bikebuddy.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bikebuddy.Utils.Workout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "__dbHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BikeBuddyDB";
    public Context context;
    SQLiteDatabase db;

    public DbHelper(@Nullable Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //Creating Table Statements
    /*
        This is the string to create the workout table. It uses the DB contract (which holds the
        format for tables to be created)
     */
    //TODO: change data types to match workout class once real data is to be used
    private static final String CREATE_TABLE_WORKOUTS = "CREATE TABLE " + DbContract.WorkoutEntry.TABLE_NAME + "(" +
            DbContract.WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DbContract.WorkoutEntry.COLUMN_DATE + " INTEGER NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_DURATION + " REAL NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_DISTANCE + "  REAL NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_HR_AVG + " INTEGER NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_SPEED_AVG + " REAL NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_BIKE_USED + " INTEGER NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_CALORIES_RATE + " INTEGER NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_CALORIES_TOT + " INTEGER NOT NULL" + ")";

    /*
        We haven't quite decided whether we are going to be making a bike table with SQLite, if we do it should go here.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORKOUTS);
        Log.d(TAG, "workoutDB created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.WorkoutEntry.TABLE_NAME);
        onCreate(db);
    }

    public long insertWorkout(Workout workout){
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        /*
            Set content values to be sent to DB. Takes from workout class passed to insert.
         */
        ContentValues contentValues = new ContentValues();
        //contentValues.put(DbContract.WorkoutEntry.COLUMN_DATE,workout.getDate());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_DURATION,workout.getTotalDuration());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_DISTANCE,workout.getTotalDistance());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_HR_AVG,workout.getAverageHR());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_SPEED_AVG,workout.getAverageSpeed());

        /*
            These columns need to have their values set and handled by some sort of packager.
            Currently set to 0 to keep the DB testable until input is sorted.
         */
        //TODO: handle packaging of lists, date objects, and bike objects
        contentValues.put(DbContract.WorkoutEntry.COLUMN_DATE,5);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_CALORIES_RATE,20);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_CALORIES_TOT,60);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_BIKE_USED,17);

        try{
            id = db.insertOrThrow(DbContract.WorkoutEntry.TABLE_NAME, null, contentValues);

            /* TODO: fix exception handler
        }
        catch (SQLException error){
            Toast.makeText(context, "course insert failed: " + error.getMessage(), Toast.LENGTH_SHORT);
            */
        } finally{
            db.close();
        }

        return id;
    }

    public Workout retrieveWorkout(long workoutID){
        Log.d(TAG,"retrieve workout");
        SQLiteDatabase db = this.getReadableDatabase();

        //SQL command to return matching workout
        String selectQuery = "SELECT  * FROM " + DbContract.WorkoutEntry.TABLE_NAME +
                " WHERE " + DbContract.WorkoutEntry._ID + " = " + workoutID;

        Cursor cursor = db.rawQuery(selectQuery, null);

        //Checks if the list is empty
        //TODO: add behaviours for empty workout table
        if (cursor!=null){
            cursor.moveToFirst();
        }

        //Populates a workout object from DB and returns it.
        Workout workout = new Workout();
        workout.setAverageHR(cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_HR_AVG)));
        workout.setAverageSpeed(cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_SPEED_AVG)));
        workout.setCaloriesBurned(cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_CALORIES_TOT)));
        workout.setTotalDistance(cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DISTANCE)));
        workout.setTotalDuration(cursor.getLong(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DURATION)));

        return workout;
    }

    public List<Workout> getWorkouts(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Log.d(TAG,"getWorkouts");

        try {
            cursor = db.query(DbContract.WorkoutEntry.TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();   //initializes cursor at table start
                ArrayList<Workout> workoutList = new ArrayList<>();
                do {
                    //Stores all columns in the table as a new entry to the arraylist
                    Workout workout = new Workout();
                    //retrieve data from db
                    //TODO Clean up once real data starts to be used
                    int id = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry._ID));
                    long duration = cursor.getLong(cursor.getColumnIndex((DbContract.WorkoutEntry.COLUMN_DURATION)));
                    double distance = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DISTANCE));
                    double avgHR = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_HR_AVG));
                    double avgSpeed = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_SPEED_AVG));
                    int date = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DATE));
                    int calRate = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_CALORIES_RATE));
                    int calTotal = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_CALORIES_TOT));
                    int bikeUsed = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_BIKE_USED));
                    //Set workout parameters before adding to list
                    workout.setID(id);
                    workout.setTotalDuration(duration);
                    workout.setTotalDistance(distance);
                    workout.setAverageHR(avgHR);
                    workout.setAverageSpeed(avgSpeed);
                    workout.setDate(Calendar.getInstance().getTime());  //TODO: change to workout date once DB storage is fixed
                    workout.setCaloriesRate(calRate);
                    workout.setCaloriesBurned(calTotal);
                    workoutList.add(workout);
                } while (cursor.moveToNext()); //movetoNext returns true if next is nonNull

                Log.d(TAG,"Courses Retrieved");
                return workoutList;
            }
        } catch (Exception error) {
            Log.d(TAG, "Attempting to retrieve Course data: " + error.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return Collections.emptyList(); //This return is only if the cursor doesn't find the table start, or exception thrown
    }
}
