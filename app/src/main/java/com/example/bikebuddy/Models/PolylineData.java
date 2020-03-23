package com.example.bikebuddy.Models;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

/**
 *This model class holds the data for a polyline (direction on a map)
 */

public class PolylineData {

    private DirectionsLeg leg;
    private Polyline polyline;

    public PolylineData(Polyline polyline1, DirectionsLeg leg1){
        this.leg = leg1;
        this.polyline = polyline1;
    }

    public DirectionsLeg getLeg() {
        return leg;
    }

    public void setLeg(DirectionsLeg leg) {
        this.leg = leg;
    }

    public Polyline getPolyine() {
        return polyline;
    }

    public void setPolyine(Polyline polyine) {
        this.polyline = polyine;
    }



}
