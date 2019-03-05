package com.example.zachary.hktour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private int startHour;
    private int startMinutes;
    private int endHour;
    private int endMinutes;
    private String remainingTime;

    private Evaluate evaluate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: creating main activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("1",startHour);
                intent.putExtra("2", startMinutes);
                intent.putExtra("3",endHour);
                intent.putExtra("4",endMinutes);
                startActivity(intent);
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
    }


    public int getEndHour() {
        return endHour;
    }

    public int getEndMinutes() {
        return endMinutes;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    private void setRemainingTime(){
        evaluate = new Evaluate(startHour, startMinutes, endMinutes, endHour);
        remainingTime = "Time remaining: " + evaluate.getRemainingTime();
        Log.d(TAG, "setRemainingTime: calculating remaining time" + evaluate.getRemainingTime());
    }
}
