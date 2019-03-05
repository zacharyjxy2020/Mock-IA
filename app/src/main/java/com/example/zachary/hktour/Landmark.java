package com.example.zachary.hktour;

import android.location.Address;
import android.provider.Telephony;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Landmark {
    private LatLng latLng;
    private String name;
    private String description;
    private String address;
    private String snippet;

    private static final String TAG = "Landmark";
    public Landmark(double lat, double lon, String name, String description, String address)
    {
        Log.d(TAG, "Landmark: creating landmark object");
        this.latLng = new LatLng(lat,lon);
        this.name = name;
        this.description = description;
        this.address = address;
    }
    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }


}
