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

public class AddHome extends BaseActivity {

    private static final int MAP_ACTIVITY_REQUEST_CODE = 1;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText rentEditText;
    private EditText roomsEditText;
    private Button locationButton;

    private FirebaseDatabase database;
    private DatabaseReference homesRef;

    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

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
        locationButton = findViewById(R.id.locbut);

        locationButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddHome.this, MapActivity.class);
            startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
        });

        checkIfUserHasHome();
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

    private void checkIfUserHasHome() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();
        homesRef.orderByChild("ownerid").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot homeSnapshot : dataSnapshot.getChildren()) {
                        Home home = homeSnapshot.getValue(Home.class);
                        if (home != null) {
                            // Home exists for this user, navigate to EditHome activity
                            Intent intent = new Intent(AddHome.this, HomeEdit.class);
                            intent.putExtra("homeId", home.getId());
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddHome.this, "Failed to check home existence: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = database.getReference("users").child(userId);

        // Generate a unique key for the home in Firebase
        String homeId = homesRef.push().getKey(); // This creates a new unique ID

        // Create a new Home object with the generated ID
        Home home = new Home(homeId, name, description, rent, rooms, selectedLatitude, selectedLongitude, userId);

        // Save the new home to the database
        homesRef.child(homeId).setValue(home).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update living situation of the user
                userRef.child("livingSituation").setValue("Has an apartment").addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        Toast.makeText(this, "Home added and living situation updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update living situation: " + userTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Failed to add home: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
