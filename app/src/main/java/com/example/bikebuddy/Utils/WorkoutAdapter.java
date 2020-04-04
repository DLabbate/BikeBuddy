package com.example.bikebuddy.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.R;
import com.example.bikebuddy.WorkoutActivity;

import java.text.SimpleDateFormat;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    public static final String TAG = "WorkoutAdapter";
    private Context context;
    private List<Workout> workoutList;
    DbHelper dbHelper;
    public WorkoutAdapter(Context context, List<Workout> workoutList)
    {
        this.context = context;
        this.workoutList = workoutList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_view_holder,parent,false);
        dbHelper=new DbHelper(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final int click_position = position; //We make a final int so it can be accessed by the onClickListener (inner class)
        Log.d(TAG,"onBindViewHolder: " + workoutList.get(position).getDate());
        holder.textViewDate.setText(new SimpleDateFormat(" EEE, d MMM yyyy hh:mm:ss aaa").format(workoutList.get(position).getDate()));// we would like to display the Date on the log fragment
        holder.textViewDistance.setText("Distance: " + Integer.toString((int)workoutList.get(position).getTotalDistance()) + " m");
        holder.textViewDuration.setText("Duration: " + durationToTime(workoutList.get(position).getTotalDuration()));
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

        //Setup onclick listener for deleting a Workout
        //**************************************************************************************************************************
        holder.imageViewdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.text_delete_workout));
                builder.setCancelable(true);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //If user selects Yes, then delete the workout
                        //******************************************************************************
                        dbHelper.deleteWorkout(workoutList.get(position).getID());

                        // refresh the page (update the log fragment)
                        Intent intent = new Intent(context,context.getClass());
                        context.startActivity(intent);

                        Toast.makeText(context,"Workout Deleted",Toast.LENGTH_SHORT).show();
                        //******************************************************************************
                    }
                });
                //If the user selects close, then we disregard
                //**************************************************************************************
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                //**************************************************************************************
            }
        });
    }



    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewDate;       //This is the TextView that displays the date for each workout
        public TextView textViewDistance;   //This is the TextView that displays the distance for each workout
        public TextView textViewDuration;   //this is the TextView that displays the distance for each workout
        public CardView cardView;           //This is the card background for each view holder
        public ImageView imageViewdelete;   // This is the delete icon in each view holder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_date);
            textViewDistance = itemView.findViewById(R.id.text_distance);
            textViewDuration = itemView.findViewById(R.id.text_duration);
            cardView = itemView.findViewById(R.id.card_workout);
            imageViewdelete = itemView.findViewById(R.id.image_delete_Workout);
        }
    }

    private String durationToTime(double seconds){

        int P1 = (int) seconds % 60;
        int P2 = (int) seconds / 60;
        int P3 = (int) P2 % 60;
        P2 = P2 / 60;

        String time = P2 + ":" + P3 + ":" + P1;
        return time;
    }
}
