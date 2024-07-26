package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeEdit extends BaseActivity {

    private static final int MAP_ACTIVITY_REQUEST_CODE = 1;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText rentEditText;
    private EditText roomsEditText;
    private Button locationButton;

    private FirebaseDatabase database;
    private DatabaseReference homesRef;
    private String homeId;
    private Home home;

    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_edit);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        homesRef = database.getReference("homes");

        // Initialize UI components
        nameEditText = findViewById(R.id.name);
        descriptionEditText = findViewById(R.id.disc);
        rentEditText = findViewById(R.id.rent);
        roomsEditText = findViewById(R.id.rooms);
        locationButton = findViewById(R.id.locbut);

        locationButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeEdit.this, MapActivity.class);
            startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
        });

        // Get home ID from intent
        homeId = getIntent().getStringExtra("homeId");
        if (homeId == null) {
            Toast.makeText(this, "No home ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadHomeDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedLatitude = data.getDoubleExtra("latitude", 0);
                selectedLongitude = data.getDoubleExtra("longitude", 0);
            }
        }
    }

    private void loadHomeDetails() {
        homesRef.child(homeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                home = dataSnapshot.getValue(Home.class);
                if (home != null) {
                    // Pre-fill the EditTexts with home details
                    nameEditText.setText(home.getName());
                    descriptionEditText.setText(home.getDisk());
                    rentEditText.setText(String.valueOf(home.getRent()));
                    roomsEditText.setText(String.valueOf(home.getRooms()));
                    selectedLatitude = home.getLatitude();
                    selectedLongitude = home.getLongitude();
                } else {
                    Toast.makeText(HomeEdit.this, "Failed to load home details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void submitPost(View view) {
        // Get user input
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String rentString = rentEditText.getText().toString().trim();
        String roomsString = roomsEditText.getText().toString().trim();

        // Validate input
        if (name.isEmpty() || description.isEmpty() || rentString.isEmpty() || roomsString.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int rent;
        int rooms;
        try {
            rent = Integer.parseInt(rentString);
            rooms = Integer.parseInt(roomsString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rent or rooms number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLatitude == 0 && selectedLongitude == 0) {
            Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update home object with new details
        home.setName(name);
        home.setDisk(description);
        home.setRent(rent);
        home.setRooms(rooms);
        home.setLatitude(selectedLatitude);
        home.setLongitude(selectedLongitude);

        // Save the updated home to the database
        homesRef.child(homeId).setValue(home).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Home updated successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Failed to update home: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

