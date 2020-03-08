package com.example.bikebuddy;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class GPSFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, LocationListener {

    public static final String TAG = "GPSfragment";

    MapView gMapView;
    private GoogleMap gMap = null;
    FusedLocationProviderClient fusedLocationClient;
    private Location mLastKnownLocation;

    private final float DEFAULT_ZOOM = 15;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps,container,false);
        Log.d(TAG, ": ONcreateview");

        //mGeoDataClient = Places.getGeoDataClient(this,null);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        gMapView = (MapView) view.findViewById(R.id.mapView2);



        gMapView.onCreate(savedInstanceState);
        gMapView.onResume();

        gMapView.getMapAsync(this);



        return view;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49,-124), 20));


        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);

        getDeviceLocation();

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(),location.getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation() method");
        try {
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                //Sets map's position to location of device
                                mLastKnownLocation = task.getResult();
                                if (mLastKnownLocation != null) {
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                }
                                else{
                                    Log.d(TAG, "Current location is null");
                                    Log.e(TAG, "Exception: ", task.getException());
                                    //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom());

                                }

                            }
                        }
                    });
        } catch (SecurityException e){
            Log.e("Exception: ", e.getMessage());
            }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(),"MyLocationButtonClicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
