package com.example.zachary.hktour;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FinalActivity extends AppCompatActivity {

    private static final String TAG = "FinalActivity";
    private String itinerary;
    TextView finalItinerary;
    Button quitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: creating itinerary");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        quitButton = (Button) findViewById(R.id.closeButton);
        Intent intent =  getIntent();
        itinerary = intent.getStringExtra("Data");
        finalItinerary = (TextView) findViewById(R.id.finalPrint);
        finalItinerary.setText(itinerary);

        Toast.makeText(this,"Making Itinerary...", Toast.LENGTH_SHORT).show();
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: checking if user wants to exit");
                        switch (which){
                            case DialogInterface.BUTTON_NEGATIVE:{
                                break;
                            }
                            case DialogInterface.BUTTON_POSITIVE:{
                                Toast.makeText(FinalActivity.this,"Goodbye!",Toast.LENGTH_SHORT).show();
                                finish();
                                System.exit(0);
                            }
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(FinalActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogListener)
                        .setNegativeButton("No", dialogListener).show();
            }
        });
    }
}
