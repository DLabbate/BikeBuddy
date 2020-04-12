package com.example.bikebuddymock;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RecordWorkoutDialog extends DialogFragment {

    //TextViews
    TextView textViewBTStatus;
    TextView textViewLocationStatus;

    //Buttons
    Button buttonContinue;
    Button buttonCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_workout,container,false);

        //Continue Button
        //*************************************************************************************************************************************
        buttonContinue = view.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FitnessFragment) getParentFragment()).continueWorkoutAction();
                getDialog().dismiss();
            }
        });
        //*************************************************************************************************************************************

        //Cancel Button
        //*************************************************************************************************************************************
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        //*************************************************************************************************************************************

        //Bluetooth status
        //*************************************************************************************************************************************
        textViewBTStatus = view.findViewById(R.id.textDialogBTStatus);
        //*************************************************************************************************************************************

        //Location status
        //*************************************************************************************************************************************
        textViewLocationStatus = view.findViewById(R.id.textDialogLocationStatus);
        //*************************************************************************************************************************************

        updateSensorStatus();

        return view;
    }

    private void updateSensorStatus()
    {
        if (MainActivity.isDeviceConnected)
        {
            textViewBTStatus.setText("Connected");
        }
        else
        {
            textViewBTStatus.setText("Not Connected");
        }

        if (isLocationEnabled(getContext()))
        {
            textViewLocationStatus.setText("Enabled");
        }

        else
        {
            textViewLocationStatus.setText("Not Enabled");
        }
    }

    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }


}
