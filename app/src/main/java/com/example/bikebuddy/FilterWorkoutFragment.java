package com.example.bikebuddy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bikebuddy.Data.DbHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        Window window = dialog.getWindow();
        Bundle args = getArguments();

        //Setting gravity
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.END);

        //Setting position based on FAB
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();
        assert args != null;
        int width = window.getDecorView().getWidth();
        int height = window.getDecorView().getHeight();
        params.x = args.getInt("buttonX") - width;
        params.y = args.getInt("buttonY")*2 - height;
        window.setAttributes(params);

        dialog.setContentView(R.layout.fragment_filter_workout);
        dialog.show();
        return dialog;

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
