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

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    public static final String TAG = "WorkoutAdapter";
    private Context context;
    private List<String> workoutList;

    public WorkoutAdapter(Context context, List<String> workoutList)
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int click_position = position; //We make a final int so it can be accessed by the onClickListener (inner class)
        Log.d(TAG,"onBindViewHolder: " + workoutList.get(position));
        holder.textViewDate.setText(workoutList.get(position));

        /*
        We need to setup an onClickListener to open a detailed view of the workout
         */
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick, position #: " + click_position);
                Intent intent = new Intent(context, WorkoutActivity.class);
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
