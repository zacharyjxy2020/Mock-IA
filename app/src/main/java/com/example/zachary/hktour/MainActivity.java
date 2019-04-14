package com.example.zachary.hktour;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;


import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    EditText startHourInput;
    EditText startMinutesInput;
    EditText endHourInput;
    EditText endMinutesInput;
    TextView calculate;

    Button submitButtonStart;
    Button submitButtonEnd;
    Button goToMap;
    Button calculateTime;
    Button logoutButton;
    Button setStartLocation;

    private int startHour;
    private int startMinutes;
    private int endHour;
    private int endMinutes;
    private String remainingTime;

    private Evaluate evaluate;

    Geocoder geocoder;
    List<Address> addresses;

    LatLng currLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: creating main activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        geocoder = new Geocoder(this);


        currLocation = null;
        startHour = 0;
        startMinutes = 0;
        endHour = 0;
        endMinutes = 0;
        calculate = (TextView) findViewById(R.id.calculation);
        remainingTime = "";
        startHourInput = (EditText) findViewById(R.id.startHour);
        startMinutesInput = (EditText) findViewById(R.id.startMinute);
        submitButtonStart = (Button) findViewById(R.id.submitButtonStart);

        submitButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: on click set values of startHour and startMinutes");
                startHour = Integer.valueOf(startHourInput.getText().toString());
                startMinutes = Integer.valueOf(startMinutesInput.getText().toString());
                Log.d(TAG, "onClick: checking for error in value of startMinutes:" + startMinutes);
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });

        endHourInput = (EditText) findViewById(R.id.endHour);
        endMinutesInput = (EditText) findViewById(R.id.endMinutes);
        submitButtonEnd = (Button) findViewById(R.id.submitButtonEnd);
        submitButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: onClick set value of start and end Hour");
                endHour = Integer.valueOf(endHourInput.getText().toString());
                endMinutes = Integer.valueOf(endMinutesInput.getText().toString());
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });


        goToMap = (Button) findViewById(R.id.goToMap);


        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currLocation == null){
                    Log.d(TAG, "onClick: user did not enter current location");
                    Toast.makeText(MainActivity.this, "Please Enter Current Address", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("1", startHour);
                    intent.putExtra("2", startMinutes);
                    intent.putExtra("3", endHour);
                    intent.putExtra("4", endMinutes);
                    intent.putExtra("Lat", currLocation.latitude);
                    intent.putExtra("Lon", currLocation.longitude);
                    startActivity(intent);
                }
            }
        });


        calculateTime = (Button) findViewById(R.id.calculateTime);


        calculateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRemainingTime();
                calculate.setText(remainingTime);
                Log.d(TAG, "onClick: set the text to remaining time.");
            }
        });

        //Setting the function of the log out button that will log the user out
        logoutButton = (Button) findViewById(R.id.logout_Button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Goodbye", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        setStartLocation = (Button) findViewById(R.id.setStartLocation);
        setStartLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter your start/end location");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            addresses = geocoder.getFromLocationName(input.getText().toString(), 1 );
                            Address address = addresses.get(0);
                            double lng = address.getLongitude();
                            double lat = address.getLatitude();
                            Toast.makeText(MainActivity.this, "Location Found", Toast.LENGTH_SHORT).show();
                            currLocation = new LatLng(lat,lng);

                        }catch (IOException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Can't Find Address Please Try Again", Toast.LENGTH_SHORT).show();
                        } catch(IndexOutOfBoundsException e){
                            Toast.makeText(MainActivity.this, "Can't Find Address Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private void setRemainingTime(){
        evaluate = new Evaluate(startHour, startMinutes, endMinutes, endHour);
        remainingTime = "Time remaining: " + evaluate.getRemainingTime();
        Log.d(TAG, "setRemainingTime: calculating remaining time" + evaluate.getRemainingTime());
    }
}
