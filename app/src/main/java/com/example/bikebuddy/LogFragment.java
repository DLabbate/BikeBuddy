package com.example.bikebuddy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Utils.Workout;
import com.example.bikebuddy.Utils.WorkoutAdapter;

import java.util.Collections;
import java.util.List;

public class LogFragment extends Fragment {

    private static final String TAG = "LogFragment";
    private DbHelper dbHelper;
    //RecyclerView
    RecyclerView workoutRecyclerView;
    RecyclerView.Adapter workoutAdapter;
    RecyclerView.LayoutManager linearLayoutManager;


    private List<Workout> Workouts;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs,container,false);

        workoutRecyclerView = view.findViewById(R.id.recycler_view_workout);
        LoadWorkout();

        return view;
    }
    public void LoadWorkout(){

        dbHelper = new DbHelper(this.getActivity());
        Workouts = dbHelper.getWorkouts();

        Log.d(TAG,"Workoutlist size: " + Workouts.size());
        workoutAdapter = new WorkoutAdapter(getActivity(),Workouts);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        workoutRecyclerView.setAdapter(workoutAdapter);
        workoutRecyclerView.setLayoutManager(linearLayoutManager);
    }
}


