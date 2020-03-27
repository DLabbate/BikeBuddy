package com.example.bikebuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bikebuddy.Data.DbHelper;

import java.util.Calendar;
import java.util.Date;

public class FilterWorkoutFragment extends DialogFragment {

    private static final String TAG = "FilterWorkoutFragment";
    private Context context;
    private DialogInterface.OnDismissListener onDismissListener;

    //Fragment Objects
    Button filter1week;
    Button filter2week;
    Button filter1Month;
    Button showAll;

    //Database
    DbHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        this.context = getActivity();
        dbHelper = new DbHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_filter_workout,container,false);
        setupUI(view);

        return view;
    }


    private void setupUI(View view){
        filter1week = view.findViewById(R.id.button_filter1week);
        filter2week = view.findViewById(R.id.button_filter2weeks);
        filter1Month = view.findViewById(R.id.button_filter1month);
        showAll = view.findViewById(R.id.button_showAll);

        //Filter buttons
        filter1week.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Calendar.getInstance().getTime());
            calendar.add(Calendar.DATE,-7);
            Date lowerDate = calendar.getTime();
            Log.d(TAG,"LowerDate = " + lowerDate);
            LogFragment.setLowerDate(lowerDate);
            getDialog().dismiss();
        });
        filter2week.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Calendar.getInstance().getTime());
            calendar.add(Calendar.DATE,-14);
            Date lowerDate = calendar.getTime();
            Log.d(TAG,"LowerDate = " + lowerDate);
            LogFragment.setLowerDate(lowerDate);
            getDialog().dismiss();
        });
        filter1Month.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Calendar.getInstance().getTime());
            calendar.add(Calendar.DATE,-30);
            Date lowerDate = calendar.getTime();
            Log.d(TAG,"LowerDate = " + lowerDate);
            LogFragment.setLowerDate(lowerDate);
            getDialog().dismiss();
        });
        showAll.setOnClickListener(v -> {
            Date lowerDate = new Date();
            lowerDate.setYear(19);
            lowerDate.setMonth(11);
            lowerDate.setDate(1);
            LogFragment.setLowerDate(lowerDate);
            getDialog().dismiss();
        });
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
}
