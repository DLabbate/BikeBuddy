package com.example.bikebuddy;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Utils.Workout;
import com.example.bikebuddy.Utils.WorkoutAdapter;
import com.google.android.gms.maps.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LogFragment extends Fragment {

    private static final String TAG = "LogFragment";
    private DbHelper dbHelper;
    List<Workout> filteredWorkoutList;

    //Filter functionality
    FloatingActionButton FAB_filterByDate;
    static Date lowerDate = new Date();
    FilterWorkoutFragment d = new FilterWorkoutFragment();

    //Ordering Functionality
    Spinner orderSpinner;
    int currentFilter;

    //RecyclerView
    RecyclerView workoutRecyclerView;
    RecyclerView.Adapter workoutAdapter;
    RecyclerView.LayoutManager linearLayoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Dialog linked to dismiss listener. This method runs when the LogFragment detects the
        dialog "d" has been closed. Used to refresh list with new filtered list
         */
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.d(TAG,"onDismiss");
                //Filters the workout list based on the button clicked in the dialog fragment
                try {
                    filteredWorkoutList = dbHelper.filterWorkoutByDate(lowerDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                refreshList();
            }
        });

        //This is the default date that no workout can be before (i.e. filtering by this date
        //  returns ALL workouts 28 June 1919 (the day the Treaty of Versailles was signed)
        lowerDate.setYear(19);
        lowerDate.setMonth(5);
        lowerDate.setDate(28);
        dbHelper = new DbHelper(this.getActivity());
        try {
            filteredWorkoutList = dbHelper.filterWorkoutByDate(lowerDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs,container,false);
        Log.d(TAG,"onCreateView");

        workoutRecyclerView = view.findViewById(R.id.recycler_view_workout);
        FAB_filterByDate = view.findViewById(R.id.FAB_filter_workouts);
        orderSpinner = view.findViewById(R.id.spinner_sort_workout);

        //This override hides the FAB when scrolling down, and reappears when scrolling up
        workoutRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ( dy > 0 && FAB_filterByDate.getVisibility() == View.VISIBLE) FAB_filterByDate.hide();
                else if ( dy < 0 && FAB_filterByDate.getVisibility() != View.VISIBLE) FAB_filterByDate.show();
            }
        });

        setupFAB();
        setupSpinner();

        try {
            filteredWorkoutList = dbHelper.filterWorkoutByDate(lowerDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        refreshList();


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
                Bundle args = new Bundle();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((MainActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                args.putInt("buttonX",(int)(FAB_filterByDate.getWidth()));
                args.putInt("buttonY",(int)(FAB_filterByDate.getHeight()));
                d.setArguments(args);
                d.show(getFragmentManager(),"Filter Workouts Fragment");
            }
        });
    }

    private void setupSpinner(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.sorting_options));

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        orderSpinner.setAdapter(arrayAdapter);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //sort by date
                if(position == 1){
                    currentFilter = position;
                    Log.d(TAG,"Selected sort by date ascending");
                    orderByDate();
                    Toast.makeText(getContext(),"Sorted by Date",Toast.LENGTH_SHORT).show();
                    refreshList();
                }
                //sort by distance
                if(position == 2){
                    currentFilter = position;
                    Log.d(TAG,"Selected sort by distance ascending");
                    orderByDistance();
                    Toast.makeText(getContext(),"Sorted by Distance",Toast.LENGTH_SHORT).show();
                    refreshList();
                }
                //sort by duration
                if(position == 3){
                    currentFilter = position;
                    Log.d(TAG,"Selected sort by duration ascending");
                    orderByDuration();
                    Toast.makeText(getContext(),"Sorted by Duration",Toast.LENGTH_SHORT).show();
                    refreshList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //this replaces the original code in "onCreate", this function can be called throughout fragment
    private void refreshList() {
        Log.d(TAG, "Refresh List");
        Log.d(TAG, "filter List size: " + filteredWorkoutList.size());

        //Recall the last filter used to implement on the newly filtered list
        if      (currentFilter == 1) orderByDate();
        else if (currentFilter == 2) orderByDistance();
        else                         orderByDuration();

        //Populate the recycler view
        workoutAdapter = new WorkoutAdapter(this.getContext(), filteredWorkoutList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        workoutRecyclerView.setAdapter(workoutAdapter);
        workoutRecyclerView.setLayoutManager(linearLayoutManager);
    }

    //these functions are used to sort the current list of workouts
    private void orderByDate() {
        Collections.sort(filteredWorkoutList, new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return o2.getDate().compareTo(o1.getDate());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
    }
    private void orderByDistance(){
        Collections.sort(filteredWorkoutList, new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return (int) (o2.getTotalDistance() - o1.getTotalDistance());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
    }
    private void orderByDuration(){
        Collections.sort(filteredWorkoutList, new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                return (int) (o2.getTotalDuration() - o1.getTotalDuration());
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });
    }
}


