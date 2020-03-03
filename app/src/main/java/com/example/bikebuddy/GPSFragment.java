package com.example.bikebuddy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class GPSFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "GPSfragment";

    MapView gMapView;
    private GoogleMap gMap = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps,container,false);

        Log.d(TAG, ": ONcreateview");
        gMapView = (MapView) view.findViewById(R.id.mapView2);

        gMapView.onCreate(savedInstanceState);
        gMapView.onResume();


        gMapView.getMapAsync(this);

        //MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        //gMapView.OnResume();

        return view;

    }
    /*
    public void onMapReady(GoogleMap map){
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady Callback method");
        gMap = map;
        //gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49,-124), 20));
    }

     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }
}
