package com.example.bikebuddy.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.R;

import java.util.List;

public class BikeAdapter extends RecyclerView.Adapter<BikeAdapter.ViewHolder> {

    List<Bike> bikeList;

    public BikeAdapter(List<Bike> bikeList)
    {
        this.bikeList = bikeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bike_view_holder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bike currentBike = bikeList.get(position);
        holder.textViewBikeName.setText(currentBike.getName());
        holder.textViewBikeModel.setText(currentBike.getModel());
        holder.textViewBikeBrand.setText(currentBike.getBrand());
        holder.textViewBikeWheelDiameter.setText(Double.toString(currentBike.getWheelDiameter()));
        holder.textViewDistance.setText(Double.toString(currentBike.getCumulativeDistance()));
        holder.textViewDuration.setText(Long.toString(currentBike.getTotalDuration()));
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
        }
    }
}
