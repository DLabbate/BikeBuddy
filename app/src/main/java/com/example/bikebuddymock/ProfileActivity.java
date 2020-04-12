package com.example.bikebuddymock;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    protected EditText NameEditText;
    protected EditText AgeEditText;
    protected EditText WeightEditText;
    protected Button SaveButton;

    protected SharedPreferenceHelper sharedPreferenceHelper;

    public static final String TAG = "ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        /*
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        /*drop down menu for gender*/

        final Spinner GenderSpinner = (Spinner) findViewById(R.id.Gender_Spinner);


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

                String name_temp = NameEditText.getText().toString();
                String age_temp = AgeEditText.getText().toString();
                String weight_temp = WeightEditText.getText().toString();
                int age = 0;
                int weight = 0;
                String name = "";
                String gender = "";

                /*
                if(!"".equals(name_temp)){
                    name = name_temp;
                }
                else{
                    Toast.makeText(ProfileActivity.this, "No Name Entered",
                            Toast.LENGTH_SHORT).show();
                }

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
                 */

                if (name_temp.equals("") || name_temp == null
                        || age_temp.equals("") || age_temp == null
                        || age_temp.equals("0") || age_temp.equals("00")
                        || weight_temp.equals("") || weight_temp == null
                        || weight_temp.equals("0") ||weight_temp.equals("00") || weight_temp.equals("000"))
                {
                    Toast.makeText(ProfileActivity.this, "Please Fill all Fields",
                            Toast.LENGTH_SHORT).show();
                }

                else
                {
                    name = name_temp;
                    age = Integer.parseInt(age_temp);
                    weight = Integer.parseInt(weight_temp);
                    gender = GenderSpinner.getSelectedItem().toString();
                    Log.d(TAG,"Spinner: " + gender);

                    sharedPreferenceHelper.saveProfileName(name);
                    sharedPreferenceHelper.saveProfileAge(age);
                    sharedPreferenceHelper.saveProfileWeight((weight));
                    sharedPreferenceHelper.saveProfileGender(gender);
                    sharedPreferenceHelper.saveProfile();//Indicates that a profile has been saved
                    Toast.makeText(ProfileActivity.this, "Profile Saved",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                    startActivity(intent);
                }
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

    /*
    This method loads the profile info if it already exists
     */
    public void loadProfile()
    {
        if (sharedPreferenceHelper.getProfile() == true) //First we check if a profile exists or not
        {
            NameEditText.setText(sharedPreferenceHelper.getProfileName());
            AgeEditText.setText(Integer.toString(sharedPreferenceHelper.getProfileAge()));
            WeightEditText.setText(Integer.toString(sharedPreferenceHelper.getProfileWeight()));

            Spinner GenderSpinner = (Spinner) findViewById(R.id.Gender_Spinner);
            String gender = sharedPreferenceHelper.getProfileGender();

            if (gender.equals("Male"))
            {
                GenderSpinner.setSelection(0);
            }

            else
            {
                GenderSpinner.setSelection(1);
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProfile();
    }
}
