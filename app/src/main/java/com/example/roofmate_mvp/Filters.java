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

import java.util.ArrayList;
import java.util.List;

public class Filters extends AppCompatActivity {

    private SeekBar ageRangeSeekBar;
    private TextView ageRangeValue;
    private Spinner locationSpinner;
    private RadioGroup genderRadioGroup;
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
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
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

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        String gender = selectedGenderButton.getText().toString();

        // Get the user ID from the intent or shared preferences
        String userId = getIntent().getStringExtra("userId");

        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update filters in the database
        mDatabase.child("users").child(userId).child("minAge").setValue(minAge);
        mDatabase.child("users").child(userId).child("maxAge").setValue(maxAge);
        mDatabase.child("users").child(userId).child("filterLocations").setValue(location);
        mDatabase.child("users").child(userId).child("filterGenders").setValue(gender)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Filters saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save filters", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
