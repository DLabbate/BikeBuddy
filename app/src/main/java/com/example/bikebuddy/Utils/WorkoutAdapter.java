package com.example.bikebuddy.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.R;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

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
        holder.textViewDate.setText("TEST");
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewDate; //This is the TextView that displays the date for each workout
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_date);
        }
    }
}
