package com.example.bikebuddy.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.bikebuddy.Utils.Workout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
    private static final String CREATE_TABLE_WORKOUTS = "CREATE TABLE " + DbContract.WorkoutEntry.TABLE_NAME + "(" +
            DbContract.WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DbContract.WorkoutEntry.COLUMN_DATE + " STRING NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_DURATION + " REAL NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_DISTANCE + " REAL NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_TIME_LIST + " STRING NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_HR_LIST + " STRING NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_SPEED_LIST + " STRING NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_HR_AVG + " INTEGER NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_SPEED_AVG + " REAL NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_BIKE_USED + " INTEGER NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_CALORIES_RATE + " INTEGER NOT NULL," +
            DbContract.WorkoutEntry.COLUMN_CALORIES_TOT + " INTEGER NOT NULL" + ")";

    /*
    We haven't quite decided whether we are going to be making a bike table with SQLite, if we do it should go here.
     */
    //TODO: add bike table

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

    public long insertWorkout(Workout workout) {
        Log.d(TAG,"insertWorkout");
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        /*
        Set content values to be sent to DB. Takes from workout class passed to insert.
         */
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.WorkoutEntry.COLUMN_DATE,dateToString(workout.getDate()));
        contentValues.put(DbContract.WorkoutEntry.COLUMN_DURATION,workout.getTotalDuration());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_DISTANCE,workout.getTotalDistance());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_CALORIES_RATE,workout.getCaloriesRate());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_CALORIES_TOT,workout.getCaloriesBurned());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_HR_AVG,workout.getAverageHR());
        contentValues.put(DbContract.WorkoutEntry.COLUMN_SPEED_AVG,workout.getAverageSpeed());
        /*
        Serializing:
            - Uses GSON API
            - Converts lists from workout into JSON strings
            - Stores strings in DB field
         */
        Log.d(TAG,"Start Serialize");
        Gson gson = new Gson();
        String serializeTime = gson.toJson(workout.getTime());
        String serializeHR = gson.toJson(workout.getListHR());
        String serializeSpeed = gson.toJson(workout.getListSpeed());
        //Log.d(TAG,"JSONtime = " + serializeTime);
        //Log.d(TAG,"JSONhr = " + serializeHR);
        //Log.d(TAG,"JSONspeed = " + serializeSpeed);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_TIME_LIST,serializeTime);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_HR_LIST,serializeHR);
        contentValues.put(DbContract.WorkoutEntry.COLUMN_SPEED_LIST,serializeHR);

        //random bike value assigned to all workouts (tentative)
        //TODO: handle packaging bike objects
        contentValues.put(DbContract.WorkoutEntry.COLUMN_BIKE_USED,17);

        try{
            id = db.insertOrThrow(DbContract.WorkoutEntry.TABLE_NAME, null, contentValues);
        }
        catch (Exception error){
            Toast.makeText(context, "insert failed: " + error.getMessage(), Toast.LENGTH_SHORT);
        } finally{
            db.close();
        }
        return id;
    }

    //Accepts workout id and returns workout object with that ID from DB
    public Workout retrieveWorkout(long workoutID) throws ParseException {
        Log.d(TAG,"retrieve workout");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Workout workout = new Workout();

        //SQL command to return matching workout
        try {
            String selectQuery = "SELECT  * FROM " + DbContract.WorkoutEntry.TABLE_NAME +
                    " WHERE " + DbContract.WorkoutEntry._ID + " = " + workoutID;
            Log.d(TAG, "Select Query = " + selectQuery);
            cursor = db.rawQuery(selectQuery, null);

            //Checks if the list is empty
            if (cursor != null) {
                cursor.moveToFirst();
            }

        /*
        DESERIALIZING:
            - Strings stored in DB are taken and converted back to Lists using the Collections
              object TypeToken.
            - Converted lists are then stored in workout class as normal.
            - Uses GSON api.
        */
            Log.d(TAG, "Deserializing");
            Gson gson = new Gson();
            String JSONtime = cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_TIME_LIST));
            String JSONhr = cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_HR_LIST));
            String JSONspeed = cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_SPEED_LIST));

            Type listType_long = new TypeToken<Collection<Long>>() {
            }.getType();
            Type listType_double = new TypeToken<Collection<Double>>() {
            }.getType();
            List<Long> time = gson.fromJson(JSONtime, listType_long);
            List<Double> heartRate = gson.fromJson(JSONhr, listType_double);
            List<Double> speed = gson.fromJson(JSONspeed, listType_double);

            //Retrieving data from DB
            int id = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry._ID));
            Date date = stringToDate(cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DATE)));
            double distance = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DISTANCE));
            long duration = cursor.getLong(cursor.getColumnIndex((DbContract.WorkoutEntry.COLUMN_DURATION)));
            int calTotal = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_CALORIES_TOT));
            int calRate = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_CALORIES_RATE));
            double avgHR = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_HR_AVG));
            double avgSpeed = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_SPEED_AVG));
            int bikeUsed = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_BIKE_USED));

            //adding all workout parameters to workout object to be returned.
            workout.setID(id);
            workout.setDate(date);
            workout.setTime(time);
            workout.setListHR(heartRate);
            workout.setListSpeed(speed);
            workout.setTotalDuration(duration);
            workout.setTotalDistance(distance);
            workout.setAverageHR(avgHR);
            workout.setAverageSpeed(avgSpeed);
            workout.setCaloriesRate(calRate);
            workout.setCaloriesBurned(calTotal);

            return workout; //returns completed workout
        } catch (Exception error) {
            Log.d(TAG, "Attempting to retrieve Course data: " + error.getMessage());
        } finally {
        if (cursor != null) cursor.close();
        db.close();
    }
        return workout; //Returns NULL WORKOUT if the cursor doesn't find the workout, or exception thrown
    }

    /*
    Returns all workouts currently saved from the fitness fragment.
    Returns empty list if there are no workouts
     */
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
                    /*
                    DESERIALIZING:
                        - Strings stored in DB are taken and converted back to Lists using the Collections
                        object TypeToken.
                        - Converted lists are then stored in workout class as normal.
                        - Uses GSON api.
                    */
                    Log.d(TAG,"Deserializing");
                    Gson gson = new Gson();
                    String JSONtime = cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_TIME_LIST));
                    String JSONhr = cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_HR_LIST));
                    String JSONspeed = cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_SPEED_LIST));

                    Type listType_long = new TypeToken<Collection<Long>>(){}.getType();
                    Type listType_double = new TypeToken<Collection<Double>>(){}.getType();
                    List<Long> time = gson.fromJson(JSONtime,listType_long);
                    List<Double> heartRate = gson.fromJson(JSONhr,listType_double);
                    List<Double> speed = gson.fromJson(JSONspeed,listType_double);

                    //Retrieving data from DB
                    int id = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry._ID));
                    Date date = stringToDate(cursor.getString(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DATE)));
                    double distance = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_DISTANCE));
                    long duration = cursor.getLong(cursor.getColumnIndex((DbContract.WorkoutEntry.COLUMN_DURATION)));
                    int calTotal = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_CALORIES_TOT));
                    int calRate = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_CALORIES_RATE));
                    double avgHR = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_HR_AVG));
                    double avgSpeed = cursor.getDouble(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_SPEED_AVG));
                    int bikeUsed = cursor.getInt(cursor.getColumnIndex(DbContract.WorkoutEntry.COLUMN_BIKE_USED));

                    Log.d(TAG,"Workout Inserted: ID = " + id + " Date = " + date);
                    //adding all workout parameters to workout object to be returned.
                    workout.setID(id);
                    workout.setDate(date);
                    workout.setTime(time);
                    workout.setListHR(heartRate);
                    workout.setListSpeed(speed);
                    workout.setTotalDuration(duration);
                    workout.setTotalDistance(distance);
                    workout.setAverageHR(avgHR);
                    workout.setAverageSpeed(avgSpeed);
                    workout.setCaloriesRate(calRate);
                    workout.setCaloriesBurned(calTotal);
                    workoutList.add(workout);

                    //Print workout data for debugging
                    Log.d(TAG,"\n-------------------RETRIEVING ALL WORKOUT DATA FROM DB--------------------\n");

                } while (cursor.moveToNext()); //movetoNext returns true if next is nonNull
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

    /*
    These two methods are used to handle storing the workout date in the DB.
    SQLite cannot handle the date class, to the date is stored as a string and that string
    can be reconverted to a date.
     */
    private String dateToString(Date date){
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        String dateString = df.format(date);
        return dateString;
    }
    private Date stringToDate(String dateString) throws ParseException {
        String pattern = "dd/MM/yyyy HH:mm:ss";
        Date date = new SimpleDateFormat(pattern).parse(dateString);
        return date;
    }
}
