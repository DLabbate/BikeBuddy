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
import android.widget.Toast;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    protected EditText NameEditText;
    protected EditText AgeEditText;
    protected EditText WeightEditText;
    protected Button SaveButton;

    protected SharedPreferenceHelper sharedPreferenceHelper;


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



        sharedPreferenceHelper = new SharedPreferenceHelper(ProfileActivity.this);

        NameEditText = findViewById(R.id.NameEditText);
        AgeEditText = findViewById(R.id.AgeEditText);
        WeightEditText = findViewById(R.id.WeightEditText);
        SaveButton = findViewById(R.id.SaveButton1);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*saving the edit text values with sharedpreferencehelper. Use the if statement to
                convert into Int for the shared preference*/


                String age_temp = AgeEditText.getText().toString();
                String weight_temp = WeightEditText.getText().toString();
                int age = 0;
                if(!"".equals(age_temp)){
                    age = Integer.parseInt(age_temp);
                }
                else{
                    Toast.makeText(ProfileActivity.this, "No Age Entered",
                            Toast.LENGTH_SHORT).show();
                }
                int weight = 0;
                if(!"".equals(weight_temp)){
                    weight = Integer.parseInt(weight_temp);
                }
                else{
                    Toast.makeText(ProfileActivity.this, "No Weight Entered",
                            Toast.LENGTH_SHORT).show();
                }

                sharedPreferenceHelper.saveProfileName(NameEditText.getText().toString());
                sharedPreferenceHelper.saveProfileAge(age);
                sharedPreferenceHelper.saveProfileWeight((weight));


                Toast.makeText(ProfileActivity.this, "Profile Saved",
                        Toast.LENGTH_SHORT).show();


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
        /*Saving the gender from the spinner*/
        Log.v("item", (String) parent.getItemAtPosition(position));

    }

}
