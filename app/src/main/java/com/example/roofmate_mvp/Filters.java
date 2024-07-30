package com.example.roofmate_mvp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Filters extends AppCompatActivity {

    private SeekBar ageRangeSeekBar;
    private TextView ageRangeValue;
    private Spinner locationSpinner;
    private Spinner religionSpinner;
    private RadioGroup genderRadioGroup;
    private RadioGroup smokingRadioGroup;
    private RadioGroup allergiesRadioGroup;
    private RadioGroup petsRadioGroup;
    private Button saveFiltersButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ageRangeSeekBar = findViewById(R.id.ageRangeSeekBar);
        ageRangeValue = findViewById(R.id.ageRangeValue);
        locationSpinner = findViewById(R.id.locationSpinner);
        religionSpinner = findViewById(R.id.religionSpinner);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        smokingRadioGroup = findViewById(R.id.smokingRadioGroup);
        allergiesRadioGroup = findViewById(R.id.allergiesRadioGroup);
        petsRadioGroup = findViewById(R.id.petsRadioGroup);
        saveFiltersButton = findViewById(R.id.saveFiltersButton);

        ageRangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ageRangeValue.setText("0-" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        saveFiltersButton.setOnClickListener(v -> saveFilters());
    }

    private void saveFilters() {
        int maxAge = ageRangeSeekBar.getProgress();
        int minAge = 0;

        String location = locationSpinner.getSelectedItem().toString();
        String religion = religionSpinner.getSelectedItem().toString();

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        String gender = selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";

        int selectedSmokingId = smokingRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedSmokingButton = findViewById(selectedSmokingId);
        String smoking = selectedSmokingButton != null ? selectedSmokingButton.getText().toString() : "";

        int selectedAllergiesId = allergiesRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedAllergiesButton = findViewById(selectedAllergiesId);
        String allergies = selectedAllergiesButton != null ? selectedAllergiesButton.getText().toString() : "";

        int selectedPetsId = petsRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedPetsButton = findViewById(selectedPetsId);
        String pets = selectedPetsButton != null ? selectedPetsButton.getText().toString() : "";

        // Get the user ID from the intent or shared preferences
        String userId = getIntent().getStringExtra("userId");

        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to update filters
        Map<String, Object> filters = new HashMap<>();
        filters.put("minAge", minAge);
        filters.put("maxAge", maxAge);
        filters.put("filterLocations", location);
        filters.put("filterReligions", religion);
        filters.put("filterGenders", gender);
        filters.put("filterSmoking", smoking);
        filters.put("filterAllergies", allergies);
        filters.put("filterPets", pets);

        // Update filters in the database
        mDatabase.child("users").child(userId).updateChildren(filters)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Filters saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save filters", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
