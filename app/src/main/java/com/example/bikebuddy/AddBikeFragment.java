package com.example.bikebuddy;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.Utils.Bike;

public class AddBikeFragment extends DialogFragment {
    private static final String TAG = "AddBikeFragment";
    private Context context;

    //Fragment Objects
    EditText editText_bikeName;
    EditText editText_bikeBrand;
    EditText editText_bikeModel;
    EditText editText_wheelDiameter;
    Button button_addBike;
    Button button_cancel;

    //constructor
    public AddBikeFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        this.context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_add_bike,container,false);
        setupUI(view);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupUI(View view){
        editText_bikeName = view.findViewById(R.id.edit_Text_Bike_Name);
        editText_bikeBrand = view.findViewById(R.id.edit_Text_Bike_Brand);
        editText_bikeModel = view.findViewById(R.id.edit_Text_Bike_Model);
        editText_wheelDiameter = view.findViewById(R.id.edit_Text_Wheel_Diameter);

        button_addBike = view.findViewById(R.id.button_addBike);
        button_cancel = view.findViewById(R.id.button_cancel);

        editText_bikeName.requestFocus();

        button_addBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DbHelper dbHelper = new DbHelper(getActivity());
                Bike bike = new Bike();
                //check if inputs are valid
                if(checkInputs()) {
                    //Retrieve Data to be stored
                    String bikeName = editText_bikeName.getText().toString();
                    String bikeBrand = editText_bikeBrand.getText().toString();
                    String bikeModel = editText_bikeModel.getText().toString();
                    double bikeWheelDiameter = Double.parseDouble(editText_wheelDiameter.getText().toString());

                    bike.setName(bikeName);
                    bike.setBrand(bikeBrand);
                    bike.setModel(bikeModel);
                    bike.setWheelDiameter(bikeWheelDiameter);

                    try {
                        dbHelper.insertBike(bike);
                        ((BikeActivity)getActivity()).loadBikes();
                    } catch(Exception error){
                        Toast.makeText(context, "insert failed: " + error.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Add Bike Cancelled");
                Toast toast = Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT);
                toast.show();

                getDialog().dismiss();
            }
        });
    }

    private boolean checkInputs(){
        if(editText_bikeName.getText().toString().equals("")){
            Toast toast = Toast.makeText(getContext(),"Bike must have a name",Toast.LENGTH_SHORT);
            toast.show();
            editText_bikeName.requestFocus();
            return false;
        }
        if(editText_bikeBrand.getText().toString().equals("")){
            Toast toast = Toast.makeText(getContext(),"Bike must have a brand",Toast.LENGTH_SHORT);
            toast.show();
            editText_bikeBrand.requestFocus();
            return false;
        }
        if(editText_bikeModel.getText().toString().equals("")){
            Toast toast = Toast.makeText(getContext(),"Bike must have a model",Toast.LENGTH_SHORT);
            toast.show();
            editText_bikeModel.requestFocus();
            return false;
        }
        if(editText_wheelDiameter.getText().toString().equals("")){
            Toast toast = Toast.makeText(getContext(),"Bike must have a wheel diameter",Toast.LENGTH_SHORT);
            toast.show();
            editText_wheelDiameter.requestFocus();
            return false;
        }
        return true;
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = 1000;
        params.height = 1100;
        window.setAttributes(params);
    }

     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();
        assert window != null;

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }
}


