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

import java.text.SimpleDateFormat;
import java.util.List;

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
                Adds the workout ID to the intent when moving to the workoutActivity. This ID is
                used to get workout data from DB once in workoutActivity.
                 */
                intent.putExtra("__ID",workoutList.get(position).getID());

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
