package com.example.zachary.hktour;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.Key;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    public static final int REQUEST_CODE = 1001;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int DEFAULT_ZOOM = 15;
    private GoogleMap mMap;
    private ArrayList<Landmark> Alllandmarks;
    private ArrayList<Landmark> landmarks;

    private boolean permissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener;
    private Button finishButton;
    private Evaluate evaluate;

    private int startHour;
    private int startMinutes;
    private int endHour;
    private int endMinutes;

    //widget

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkPermissions();

        Intent intent = getIntent();
        startHour = intent.getIntExtra("1" , 0);
        startMinutes = intent.getIntExtra("2",0);
        endHour = intent.getIntExtra("3",0);
        endMinutes = intent.getIntExtra("4",0);

        evaluate = new Evaluate(startHour, startMinutes, endMinutes, endHour);
        evaluate.getRemainingTime();


        landmarks = new ArrayList<>();
        Alllandmarks = new ArrayList<>();
        onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(TAG, "onInfoWindowClick: searching for landmark with same snippet");
                for(Landmark landmark : Alllandmarks){
                    if(landmark.getName().equals(marker.getTitle())){
                        if(landmarks.contains(landmark)){
                            landmarks.remove(landmark);
                            Toast.makeText(MapsActivity.this,"Successfully Removed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d(TAG, "onInfoWindowClick: Found landmark, adding to list.");
                            landmarks.add(landmark);
                            Toast.makeText(MapsActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }
        };

        finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getApplicationContext(), FinalActivity.class);
                DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_NEGATIVE:{
                                break;
                            }
                            case DialogInterface.BUTTON_POSITIVE:{
                                intent.putExtra("Data", evaluate.makeItinerary());
                                startActivity(intent);
                            }
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Are you finished choosing your landmarks?").setPositiveButton("Yes", dialogListener)
                        .setNegativeButton("No", dialogListener).show();
            }
        });
    }




    public void initMap() {
        Log.d(TAG, "initMap: initialising map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        Toast.makeText(MapsActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();

        if (permissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
            createLandmarks();
            getDeviceLocation();
        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{

        }catch (SecurityException e){
            if(permissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(DEFAULT_ZOOM, new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                        }else{
                            Log.d(TAG, "onComplete: can't find location");
                        }
                    }
                });
            }
        }
    }

    public void createLandmarks() {
        Log.d(TAG, "createLandmarks: creating landmarks");
        Landmark timesSquare = new Landmark(22.2781, 114.1822, "Times Square",
                "Popular Shopping Mall in Causeway Bay", "Causeway Bay, Matheson St, No. 1");
        Landmark hysanPlace = new Landmark(22.2796, 114.1839, "Hysan Place",
                "Shopping mall and food court in Causeway Bay", "500 Hennessy Rd, Causeway Bay");
        Landmark peak = new Landmark(22.2759, 114.1455, "The Peak",
                "Famous residential area and tourist hotspot", "Victoria Peak");
        Landmark oceanPark = new Landmark(22.2467, 114.1757, "Ocean Park",
                "Theme park in Hong Kong with a variety of rides", "Aberdeen");
        Log.d(TAG, "createLandmarks: making markers for each landmark");
        makeMarker(timesSquare);
        makeMarker(oceanPark);
        makeMarker(hysanPlace);
        makeMarker(peak);

        Alllandmarks.add(timesSquare);
        Alllandmarks.add(oceanPark);
        Alllandmarks.add(hysanPlace);
        Alllandmarks.add(peak);
    }

    private void makeMarker(final Landmark landmark) {
        Log.d(TAG, "makeMarker: making a marker on position latlng");
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
        try{
            String snippet = "Address: " + landmark.getAddress() + "." + "\n" +
                    "Description: " + landmark.getDescription() + "." + "\n" +
                    "Tap this window to add the landmark to your itinerary and tap again to remove it.";
            MarkerOptions options = new MarkerOptions()
                    .position(landmark.getLatLng()).title(landmark.getName()).snippet(snippet);
            mMap.addMarker(options);
        }catch (NullPointerException e){
            Log.e(TAG, "makeMarker: found null pointer error" + e.getMessage() );
        }

    }

    public void checkPermissions() {
        String[] permissions = {COARSE_LOCATION, FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermissions: permissions granted");
                permissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionGranted = false;
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    initMap();
                }
            }
        }
    }
    
    private void moveCamera(float zoom, LatLng latLng){
        Log.d(TAG, "moveCamera: moving camera...");

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    public void setEvaluate(Evaluate evaluate) {
        this.evaluate = evaluate;
    }
}
