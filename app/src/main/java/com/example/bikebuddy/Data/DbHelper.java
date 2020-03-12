package com.example.bikebuddy.Data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.bikebuddy.Utils.Workout;

import java.sql.SQLException;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "__dbHelper";

    private Resources mResources;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BikeBuddyDB";
    Context context;
    SQLiteDatabase db;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        mResources = context.getResources();

        db = this.getWritableDatabase();
    }

    //Creating Table Statements
    /*
        This is the string to create the workout table. It uses the DB contract (which holds the
        format for tables to be created)
     */
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
            These columns need to have their values set. Set to 0 to keep the DB valid until
            input is sorted.
         */
        contentValues.put(DbContract.WorkoutEntry.COLUMN_DATE,0);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_CALORIES_RATE,0);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_CALORIES_TOT,0);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_BIKE_USED,0);

        try{
            id = db.insertOrThrow(DbContract.WorkoutEntry.TABLE_NAME, null, contentValues);

            /*
        }

        catch (SQLException error){
            Toast.makeText(context, "course insert failed: " + error.getMessage(), Toast.LENGTH_SHORT);
            */
        } finally{
            db.close();
        }

        return id;
    }
}
