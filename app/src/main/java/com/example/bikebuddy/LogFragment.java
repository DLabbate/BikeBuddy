package com.example.bikebuddy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.google.android.gms.maps.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
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

    //MapView
    private MapView gMapView;

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
                refreshList();
            }
        });

        //This is the default date that no workout can be before (i.e. filtering by this date
        //  returns ALL workouts 28 June 1919 (the day the Treaty of Versailles was signed)
        lowerDate.setYear(19);
        lowerDate.setMonth(5);
        lowerDate.setDate(28);
        dbHelper = new DbHelper(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs,container,false);
        Log.d(TAG,"onCreateView");

        workoutRecyclerView = view.findViewById(R.id.recycler_view_workout);
        FAB_filterByDate = view.findViewById(R.id.FAB_filter_workouts);
        gMapView = view.findViewById(R.id.mapViewLogs);
        setupFAB();

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

    //this replaces the original code in "onCreate", this function can be called throughout fragment
    private void refreshList(){
        Log.d(TAG,"Refresh List");
        List<Workout> filteredWorkoutList;
        try {
            filteredWorkoutList = dbHelper.filterWorkoutByDate(lowerDate);
            Log.d(TAG,"filter List size: " + filteredWorkoutList.size());
            workoutAdapter = new WorkoutAdapter(this.getContext(),filteredWorkoutList);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            workoutRecyclerView.setAdapter(workoutAdapter);
            workoutRecyclerView.setLayoutManager(linearLayoutManager);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}


