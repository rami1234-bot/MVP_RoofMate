package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddHome extends BaseActivity {

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText rentEditText;
    private EditText roomsEditText;

    private FirebaseDatabase database;
    private DatabaseReference homesRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        homesRef = database.getReference("homes");

        // Initialize UI components
        nameEditText = findViewById(R.id.name);
        descriptionEditText = findViewById(R.id.disc);
        rentEditText = findViewById(R.id.rent);
        roomsEditText = findViewById(R.id.rooms);
    }

    public void submitPost(View view) {
        // Get user input
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String rentString = rentEditText.getText().toString().trim();
        String roomsString = roomsEditText.getText().toString().trim();
        if (name.isEmpty() || description.isEmpty() || rentString.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int rent;
        int rooms;
        try {
            rent = Integer.parseInt(rentString);
            rooms = Integer.parseInt(roomsString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rent amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Home object
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not signed in, handle accordingly
            return;
        }
        String ownerId = currentUser.getUid();
        List<Image> imageList = new ArrayList<>();  // Replace with actual image list if any
        Home home = new Home(rent, name, ownerId, description, rooms);

        // Save to Firebase under "homes"
        String homeId = homesRef.push().getKey(); // Generate a unique ID for the home
        if (homeId == null) {
            // Error generating unique ID, handle accordingly
            return;
        }
        home.setId(homeId); // Set the ID for the home object
        homesRef.child(homeId).setValue(home)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddHome.this, "Home added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddHome.this, HomePage.class);
                    startActivity(intent);
                    finish(); // Finish this activity to prevent going back to it on back press
                })
                .addOnFailureListener(e -> Toast.makeText(AddHome.this, "Failed to add home", Toast.LENGTH_SHORT).show());
    }
}
