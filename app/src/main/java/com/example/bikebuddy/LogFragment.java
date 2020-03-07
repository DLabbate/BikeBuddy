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

import com.example.bikebuddy.Utils.WorkoutAdapter;

import java.util.LinkedList;
import java.util.List;

public class LogFragment extends Fragment {

    private static final String TAG = "LogFragment";
    //RecyclerView
    RecyclerView workoutRecyclerView;
    RecyclerView.Adapter workoutAdapter;
    RecyclerView.LayoutManager linearLayoutManager;
    List<String> testStrings;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs,container,false);

        workoutRecyclerView = view.findViewById(R.id.recycler_view_workout);

        testStrings = new LinkedList<String>();
        testStrings.add("Test1");
        testStrings.add("Test2");
        testStrings.add("Test3");
        Log.d(TAG,"testStrings size: " + testStrings.size());
        workoutAdapter = new WorkoutAdapter(getActivity(),testStrings);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        workoutRecyclerView.setAdapter(workoutAdapter);
        workoutRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }
}
