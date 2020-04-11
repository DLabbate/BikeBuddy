package com.example.bikebuddy.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.BikeActivity;
import com.example.bikebuddy.Data.DbHelper;
import com.example.bikebuddy.R;
import com.example.bikebuddy.SharedPreferenceHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.ViewHolder> {

    protected List<Bike> bikeList;
    protected SharedPreferenceHelper sharedPreferenceHelper;
    protected int selectedBikeID;
    protected Context context;
    DbHelper dbHelper;

    public BikeAdapter(List<Bike> bikeList, SharedPreferenceHelper sharedPreferenceHelper,Context context)
    {
        this.bikeList = bikeList;
        this.sharedPreferenceHelper = sharedPreferenceHelper;
        this.selectedBikeID = sharedPreferenceHelper.getSelectedBike();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_view_holder,parent,false);
        dbHelper=new DbHelper(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Bike currentBike = bikeList.get(position);
        holder.textViewBikeName.setText(currentBike.getName());
        holder.textViewBikeModel.setText(currentBike.getModel());
        holder.textViewBikeBrand.setText(currentBike.getBrand());
        holder.textViewBikeWheelDiameter.setText(Double.toString(currentBike.getWheelDiameter()));

        if ( currentBike.getCumulativeDistance()> 10000 ){
            holder.textViewDistance.setText(String.format("%.2f",(float)currentBike.getCumulativeDistance()/1000) + " km"  );
        }
        else{
            holder.textViewDistance.setText( Integer.toString((int)currentBike.getCumulativeDistance()) + " m" );
        }




        //holder.textViewDuration.setText(Long.toString(currentBike.getTotalDuration()));

        /*
        We want to convert from seconds to days:hours:minutes:seconds
        See https://stackoverflow.com/questions/11357945/java-convert-seconds-into-day-hour-minute-and-seconds-using-timeunit
         */
        long seconds = currentBike.getTotalDuration();
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

        holder.textViewDuration.setText(Integer.toString(day)+ "d, " + hours + "h, " + minute + "m, " + second + "s");

        updateSelectedBike(currentBike,holder); //Update the checkmark (UI)

        //Setup onclick listener to select a bike
        //**************************************************************************************************************************
        final int pos = position; //Make a final variable so it can be accessed from inner class
        holder.cardViewBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBikeID = currentBike.getID(); //Update the selected bike ID (field in BikeAdapter)
                sharedPreferenceHelper.setSelectedBike(currentBike.getID()); //Update this ID in sharedpreferences as well
                notifyDataSetChanged();
            }
        });
        //**************************************************************************************************************************

        //Setup onclick listener for deleting a bike
        //**************************************************************************************************************************
        holder.imageViewdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.text_delete_bike));
                builder.setCancelable(true);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //If user selects Yes, then delete the workout
                        //******************************************************************************

                        dbHelper.deleteBike(currentBike.getID());
                        // refresh the page (update the log fragment)
                        ((BikeActivity)context).loadBikes();

                        Toast.makeText(context,"Bike Deleted",Toast.LENGTH_SHORT).show();
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
        //**************************************************************************************************************************
    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewcheckmark;
        public ImageView imageViewdelete;

        public TextView textViewBikeName;
        public TextView textViewBikeModel;
        public TextView textViewBikeBrand;
        public TextView textViewBikeWheelDiameter;

        public TextView textViewDuration;
        public TextView textViewDistance;

        public CardView cardViewBike;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setupUI(itemView);
        }

        public void setupUI(View view)
        {
            //Bike Info
            textViewBikeName = view.findViewById(R.id.text_bike_name_value);
            textViewBikeModel = view.findViewById(R.id.text_bike_model_value);
            textViewBikeBrand = view.findViewById(R.id.text_bike_brand_value);
            textViewBikeWheelDiameter = view.findViewById(R.id.text_bike_wheel_diameter_value);

            //Bike stats
            textViewDistance = view.findViewById(R.id.text_bike_distance_value);
            textViewDuration = view.findViewById(R.id.text_bike_value);

            //Icons (select/delete)
            imageViewcheckmark = view.findViewById(R.id.image_select_bike);
            imageViewdelete = view.findViewById(R.id.image_delete_bike);

            //Card View
            cardViewBike = view.findViewById(R.id.card_bike);
        }
    }

    private void updateSelectedBike(final Bike currentBike, final ViewHolder holder)
    {
        //Make sure the checkmark is ONLY VISIBLE FOR THE SELECTED BIKE
        if (currentBike.getID() == selectedBikeID)
        {
            holder.imageViewcheckmark.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imageViewcheckmark.setVisibility(View.INVISIBLE);
        }
    }


}
