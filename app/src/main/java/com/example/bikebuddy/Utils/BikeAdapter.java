package com.example.bikebuddy.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikebuddy.R;

import java.util.ArrayList;
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

    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewcheckmark;
        public ImageView imageViewdelete;
        public TextView textViewDuration;
        public TextView textViewDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setupUI(itemView);
        }

        public void setupUI(View view)
        {
            imageViewcheckmark = view.findViewById(R.id.image_select_bike);
            imageViewdelete = view.findViewById(R.id.image_delete_bike);
            textViewDistance = view.findViewById(R.id.text_bike_distance);
            textViewDuration = view.findViewById(R.id.text_bike_duration);
        }
    }
}
