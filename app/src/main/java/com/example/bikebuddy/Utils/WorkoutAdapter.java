package com.example.bikebuddy.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.R;
import com.example.bikebuddy.WorkoutActivity;
import com.google.gson.Gson;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    public static final String TAG = "WorkoutAdapter";
    private Context context;
    private List<Workout> workoutList;

    public WorkoutAdapter(Context context, List<Workout> workoutList)
    {
        this.context = context;
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_view_holder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final int click_position = position; //We make a final int so it can be accessed by the onClickListener (inner class)
        Log.d(TAG,"onBindViewHolder: " + workoutList.get(position).getDate());
        holder.textViewDate.setText(new SimpleDateFormat(" EEE, d MMM yyyy hh:mm:ss aaa").format(workoutList.get(position).getDate()));// we would like to display the Date on the log fragment

        /*
        We need to setup an onClickListener to open a detailed view of the workout
         */
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick, position #: " + click_position);
                Intent intent = new Intent(context, WorkoutActivity.class);

                /*
                 Converting the object into group of strings then sending them with the intent
                */
                // converting the date after changing its format
                intent.putExtra("Date",new SimpleDateFormat("hh:mma dd-MM-yyyy").format(workoutList.get(position).getDate()));

                // converting the distance from double to int to get rid of the decimals (we are measuring the distance in meter)
                intent.putExtra("Distance", Integer.toString((int)(workoutList.get(position).getTotalDistance())));

                //converting the time from long into hours, minutes and seconds
                long hours = TimeUnit.SECONDS.toHours(workoutList.get(position).getTotalDuration());
                long minute =TimeUnit.SECONDS.toMinutes(workoutList.get(position).getTotalDuration())-hours*60;
                long seconds = TimeUnit.SECONDS.toSeconds(workoutList.get(position).getTotalDuration())-TimeUnit.SECONDS.toMinutes(workoutList.get(position).getTotalDuration())*60;;
                intent.putExtra("Duration",hours + "h, " + minute + "m, "+ seconds + "s");

                intent.putExtra("Calories",Double.toString(workoutList.get(position).getCaloriesBurned()));
                intent.putExtra("AverageHR",Double.toString(workoutList.get(position).getAverageHR()));
                intent.putExtra("AverageSpeed",Double.toString(workoutList.get(position).getAverageSpeed()));
                //Serializing
                Gson gson = new Gson();
                intent.putExtra("Time", gson.toJson(workoutList.get(position).getTime()));
                intent.putExtra("HRList", gson.toJson(workoutList.get(position).getListHR()));
                intent.putExtra("SpeedList", gson.toJson(workoutList.get(position).getListSpeed()));
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewDate; //This is the TextView that displays the date for each workout
        public CardView cardView; //This is the card background for each view holder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_date);
            cardView = itemView.findViewById(R.id.card_workout);
        }
    }
}
