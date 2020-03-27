package com.example.bikebuddy;

import android.content.DialogInterface;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogFragment extends Fragment {

    private static final String TAG = "LogFragment";
    private DbHelper dbHelper;

    //Filter functionality
    FloatingActionButton FAB_filterByDate;
    static Date lowerDate = new Date();
    FilterWorkoutFragment d = new FilterWorkoutFragment();

    //RecyclerView
    RecyclerView workoutRecyclerView;
    RecyclerView.Adapter workoutAdapter;
    RecyclerView.LayoutManager linearLayoutManager;

    // These are test variables (mock data)
    //TODO remove test objects when recording is functional
    private List<Workout> testStrings;
    List <Double> doublelist= Collections.emptyList();
    List <Long> longList=Collections.emptyList();
    double doublevalue = 10;
    long longvalue=10;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG,"onDismiss");
                refreshList();
            }
        });
        lowerDate.setYear(19);
        lowerDate.setMonth(11);
        lowerDate.setDate(1);
        dbHelper = new DbHelper(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs,container,false);
        Log.d(TAG,"onCreateView");

        workoutRecyclerView = view.findViewById(R.id.recycler_view_workout);
        FAB_filterByDate = view.findViewById(R.id.FAB_filter_workouts);
        setupFAB();

        //testStrings = dbHelper.getWorkouts();
      //  testStrings = new LinkedList<Workout>();
      //  testStrings.add(new Workout(longList,doublelist,doublelist,doublevalue,longvalue,doublevalue,doublevalue,doublevalue));
      //  testStrings.add("Test2");
      //  testStrings.add("Test3");
        refreshList();
        /*
        Log.d(TAG,"testStrings size: " + testStrings.size());
        workoutAdapter = new WorkoutAdapter(getActivity(),testStrings);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        workoutRecyclerView.setAdapter(workoutAdapter);
        workoutRecyclerView.setLayoutManager(linearLayoutManager);
        */
        return view;
    }


    //Method that can be called from child fragment to pass date value.
    static void setLowerDate(Date date){
        Log.d(TAG,"setLowerDate: " + date);
        lowerDate = date;
    }


    private void setupFAB()
    {
        FAB_filterByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.show(getFragmentManager(),"Filter Workouts Fragment");
                /*
                FilterWorkoutFragment filterWorkoutFragment = new FilterWorkoutFragment();
                filterWorkoutFragment.show(getFragmentManager(),"Filter Workouts Fragment");
                 */
            }
        });
    }

    private void refreshList(){
        Log.d(TAG,"Refresh List");
        List<Workout> filteredWorkoutList;
        try {
            filteredWorkoutList = dbHelper.filterWorkoutByDate(lowerDate);
            workoutAdapter = new WorkoutAdapter(this.getContext(),filteredWorkoutList);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            workoutRecyclerView.setAdapter(workoutAdapter);
            workoutRecyclerView.setLayoutManager(linearLayoutManager);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
