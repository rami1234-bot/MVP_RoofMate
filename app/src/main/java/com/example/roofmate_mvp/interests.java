package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class interests extends AppCompatActivity {

    private CheckBox musicCheckBox;
    private CheckBox sportsCheckBox;
    private CheckBox travelCheckBox;
    private CheckBox readingCheckBox;
    private CheckBox cookingCheckBox;
    private CheckBox gamingCheckBox;
    private CheckBox dancingCheckBox;
    private CheckBox photographyCheckBox;
    private CheckBox artCheckBox;
    private CheckBox fishingCheckBox;
    private CheckBox gardeningCheckBox;
    private CheckBox hikingCheckBox;
    private CheckBox knittingCheckBox;
    private CheckBox cyclingCheckBox;
    private CheckBox yogaCheckBox;
    private CheckBox fitnessCheckBox;
    private CheckBox swimmingCheckBox;
    private CheckBox bakingCheckBox;
    private CheckBox writingCheckBox;
    private CheckBox meditationCheckBox;
    private CheckBox boardgamesCheckBox;
    private CheckBox volunteeringCheckBox;
    private CheckBox languagelearningCheckBox;
    private CheckBox astrologyCheckBox;
    private CheckBox technologyCheckBox;
    private CheckBox carsCheckBox;
    private Button saveInterestsButton;

    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrests);

        // Initialize Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get userId from intent
        userId = getIntent().getStringExtra("userId");

        // Initialize views
        musicCheckBox = findViewById(R.id.interest_music);
        sportsCheckBox = findViewById(R.id.interest_sports);
        travelCheckBox = findViewById(R.id.interest_travel);
        readingCheckBox = findViewById(R.id.interest_reading);
        cookingCheckBox = findViewById(R.id.interest_cooking);
        gamingCheckBox = findViewById(R.id.interest_gaming);
        dancingCheckBox = findViewById(R.id.interest_dancing);
        photographyCheckBox = findViewById(R.id.interest_photography);
        artCheckBox = findViewById(R.id.interest_art);
        fishingCheckBox = findViewById(R.id.interest_fishing);
        gardeningCheckBox = findViewById(R.id.interest_gardening);
        hikingCheckBox = findViewById(R.id.interest_hiking);
        knittingCheckBox = findViewById(R.id.interest_knitting);
        cyclingCheckBox =findViewById(R.id.interest_cycling);
        yogaCheckBox = findViewById(R.id.interest_yoga);
        fitnessCheckBox = findViewById(R.id.interest_fitness);
        swimmingCheckBox = findViewById(R.id.interest_swimming);
        bakingCheckBox = findViewById(R.id.interest_baking);
        writingCheckBox = findViewById(R.id.interest_writing);
        meditationCheckBox = findViewById(R.id.interest_meditation);
        boardgamesCheckBox = findViewById(R.id.interest_boardgames);
        volunteeringCheckBox = findViewById(R.id.interest_volunteering);
        languagelearningCheckBox = findViewById(R.id.interest_languagelearning);
        astrologyCheckBox = findViewById(R.id.interest_astrology);
        technologyCheckBox = findViewById(R.id.interest_technology);
        carsCheckBox = findViewById(R.id.interest_cars);
        saveInterestsButton = findViewById(R.id.saveInterestsButton);

        saveInterestsButton.setOnClickListener(v -> saveInterests());
    }

    private void saveInterests() {
        ArrayList<String> selectedInterests = new ArrayList<>();
        if (musicCheckBox.isChecked()) selectedInterests.add("Music");
        if (sportsCheckBox.isChecked()) selectedInterests.add("Sports");
        if (travelCheckBox.isChecked()) selectedInterests.add("Travel");
        if (readingCheckBox.isChecked()) selectedInterests.add("Reading");
        if (cookingCheckBox.isChecked()) selectedInterests.add("Cooking");
        if (gamingCheckBox.isChecked()) selectedInterests.add("Gaming");
        if (dancingCheckBox.isChecked()) selectedInterests.add("Dancing");
        if (photographyCheckBox.isChecked()) selectedInterests.add("Photography");
        if (artCheckBox.isChecked()) selectedInterests.add("Art");
        if (fishingCheckBox.isChecked()) selectedInterests.add("Fishing");
        if (gardeningCheckBox.isChecked()) selectedInterests.add("Gardening");
        if (hikingCheckBox.isChecked()) selectedInterests.add("Hiking");
        if (knittingCheckBox.isChecked()) selectedInterests.add("Knitting");
        if (cyclingCheckBox.isChecked()) selectedInterests.add("Cycling");
        if (yogaCheckBox.isChecked()) selectedInterests.add("Yoga");
        if (fitnessCheckBox.isChecked()) selectedInterests.add("Fitness");
        if (swimmingCheckBox.isChecked()) selectedInterests.add("Swimming");
        if (bakingCheckBox.isChecked()) selectedInterests.add("Baking");
        if (writingCheckBox.isChecked()) selectedInterests.add("Writing");
        if (meditationCheckBox.isChecked()) selectedInterests.add("Meditation");
        if (boardgamesCheckBox.isChecked()) selectedInterests.add("Board Games");
        if (volunteeringCheckBox.isChecked()) selectedInterests.add("Volunteering");
        if (languagelearningCheckBox.isChecked()) selectedInterests.add("Language Learning");
        if (astrologyCheckBox.isChecked()) selectedInterests.add("Astrology");
        if (technologyCheckBox.isChecked()) selectedInterests.add("Technology");
        if (carsCheckBox.isChecked()) selectedInterests.add("Cars");

        if (selectedInterests.isEmpty()) {
            Toast.makeText(interests.this, "Please select at least one interest", Toast.LENGTH_SHORT).show();
        } else {
            // Save interests to database
            mDatabase.child("users").child(userId).child("interests").setValue(selectedInterests)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(interests.this, "Interests Saved", Toast.LENGTH_SHORT).show();
                            // Redirect to HomePage
                            Intent intent = new Intent(interests.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(interests.this, "Failed to save interests", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

