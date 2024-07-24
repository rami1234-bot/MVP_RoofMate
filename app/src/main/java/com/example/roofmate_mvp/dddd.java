package com.example.roofmate_mvp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dddd extends BaseActivity {
    private EditText reportText;
    private Button submitReportButton;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dddd);

        // Initialize UI elements
        reportText = findViewById(R.id.reportText);
        submitReportButton = findViewById(R.id.submitReportButton);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Fetch the current user's username
        if (currentUser != null) {
            DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    currentUsername = dataSnapshot.child("username").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(dddd.this, "Failed to fetch username", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set up submit report button listener
        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void submitReport() {
        String reportTextValue = reportText.getText().toString().trim();

        if (reportTextValue.isEmpty()) {
            Toast.makeText(dddd.this, "Report text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        long timestamp = System.currentTimeMillis();
        String username = currentUsername;

        Report report = new Report(userId, username, reportTextValue, timestamp);

        // Save the report to the database
        mDatabase.child("reports").push().setValue(report)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(dddd.this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                        reportText.setText(""); // Clear the report text field
                    } else {
                        Toast.makeText(dddd.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}