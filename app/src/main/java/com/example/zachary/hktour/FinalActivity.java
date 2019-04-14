package com.example.zachary.hktour;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FinalActivity extends AppCompatActivity {

    private static final String TAG = "FinalActivity";
    private String itinerary;
    private static final DatabaseReference REF = FirebaseDatabase.getInstance().getReference();

    TextView finalItinerary;
    Button quitButton;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    Button saveButton;
    private String itineraryName;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "Making Itinerary...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate: creating itinerary");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        quitButton = (Button) findViewById(R.id.closeButton);
        Intent intent = getIntent();
        itinerary = intent.getStringExtra("Data");
        finalItinerary = (TextView) findViewById(R.id.finalPrint);
        finalItinerary.setText(itinerary);

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: checking if user wants to exit");
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE: {
                                break;
                            }
                            case DialogInterface.BUTTON_POSITIVE: {
                                Toast.makeText(FinalActivity.this, "Goodbye!", Toast.LENGTH_SHORT).show();
                                auth.signOut();
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

        saveButton = (Button) findViewById(R.id.save_Button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FinalActivity.this);
                builder.setTitle("Name This Itinerary");

                final EditText input = new EditText(FinalActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itineraryName = input.getText().toString();
                        FirebaseDatabase.getInstance().getReference().child(user.getUid()).child(itineraryName).setValue(itinerary).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: upload success");
                                    Toast.makeText(FinalActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "onComplete: upload failed");
                                    Toast.makeText(FinalActivity.this, "Upload Failed" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.show();
            }
        });

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FinalActivity.this);
                builder.setTitle("Enter the name of the itinerary you saved");

                final EditText input = new EditText(FinalActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ProgressDialog progressDialog = new ProgressDialog(FinalActivity.this);
                        progressDialog.setMessage("Searching");
                        progressDialog.show();
                    }
                });

            }
        });
    }
}