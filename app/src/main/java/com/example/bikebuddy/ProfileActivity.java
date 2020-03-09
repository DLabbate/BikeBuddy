package com.example.bikebuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ProfileActivity extends AppCompatActivity {

    protected EditText NameEditText;
    protected EditText AgeEditText;
    protected EditText WeightEditText;
    protected Button SaveButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        /*
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        /*drop down menu for gender*/

        Spinner GenderSpinner = (Spinner) findViewById(R.id.Gender_Spinner);


        /*Create an ArrayAdapter using the string array and a default spinner
         */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array,android.R.layout.simple_spinner_item);

        /* Specify the layout to use when the list of choice appears*/

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /* Apply the adapter to the spinner*/


        GenderSpinner.setAdapter(adapter);

        NameEditText = findViewById(R.id.NameEditText);
        AgeEditText = findViewById(R.id.AgeEditText);
        WeightEditText = findViewById(R.id.WeightEditText);
        SaveButton = findViewById(R.id.SaveButton1);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = NameEditText.getText().toString();
                String age = AgeEditText.getText().toString();
                String weight = WeightEditText.getText().toString();


            }
        });
/*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

 */
    }


        public void onItemSelected(AdapterView<?> parent, View view, int position,long id){
            Log.v("item", (String) parent.getItemAtPosition(position));
        }

        public void onNothingSelected(AdapterView<?> parent){
                //TODO Auto-generated method stub
            }

}
