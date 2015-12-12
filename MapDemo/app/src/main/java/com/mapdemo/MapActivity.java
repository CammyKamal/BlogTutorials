package com.mapdemo;

import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends FragmentActivity {
    private GoogleMap googleMap;
    private SupportMapFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupDynamicMapFragment();
    }

    private void setupDynamicMapFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initializeMap() {
        LatLng location = null;
        if (googleMap == null) {
            googleMap = fragment.getMap();
            if (googleMap != null) {
                Geocoder geocoder = new Geocoder(this);
                    location = new LatLng(30.7500,76.7800);
                if (location != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            location).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }
}
