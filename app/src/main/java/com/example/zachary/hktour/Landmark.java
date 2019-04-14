package com.example.zachary.hktour;

import android.location.Address;
import android.provider.Telephony;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * <h1>Landmark</h1>
 * The Landmark class creates a landmark object holding various information about the landmark.
 * <p>
 * @author Zachary Yu
 * @since 3/20/19
 * @version 2.0.0
 * </p>
 */
public class Landmark {
    private LatLng latLng;
    private String name;
    private String description;
    private String address;
    private String snippet;

    private static final String TAG = "Landmark";

    /**
     * Constructor for Landmark Object
     * @param lat latitude
     * @param lon longitude
     * @param name name of the landmark
     * @param description description of the landmark
     * @param address address of the landmark
     */
    public Landmark(double lat, double lon, String name, String description, String address)
    {
        Log.d(TAG, "Landmark: creating landmark object");
        this.latLng = new LatLng(lat,lon);
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public Landmark(LatLng latLng){
        this.latLng = latLng;
    }
    /**
     * Returns the latLng object of the landmark
     * @return instance variable latLng
     */
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * Returns the name of the object
     * @return instance variable name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the landmark
     * @return instance variable description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns address of the landmark
     * @return instance variable address
     */
    public String getAddress() {
        return address;
    }
}
