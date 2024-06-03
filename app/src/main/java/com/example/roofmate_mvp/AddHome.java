package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddHome extends AppCompatActivity {

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText rentEditText;
    private EditText roommsEditText;

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
        roommsEditText = findViewById(R.id.rooms);
    }

    public void submitPost(View view) {
        // Get user input
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String rentString = rentEditText.getText().toString().trim();
        String roomsString = roommsEditText.getText().toString().trim();
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
        String ownerid =currentUser.getUid() ;  // Replace with actual owner ID
        List<Image> imageList = new ArrayList<>();  // Replace with actual image list if any
        Home home = new Home(rent, name, ownerid,description,rooms);
        home.setImageList(imageList);

        // Save to Firebase under "homes"
        homesRef.push().setValue(home)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddHome.this, "home added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddHome .this, "Failed to add home", Toast.LENGTH_SHORT).show());

        Intent intent = new Intent(AddHome.this, HomePage.class);
        startActivity(intent);
    }
}
