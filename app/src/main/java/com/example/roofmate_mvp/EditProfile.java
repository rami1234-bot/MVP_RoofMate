package com.example.roofmate_mvp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private Button saveBtn;
    private Button goBack;
    private Button editInterests;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveBtn = findViewById(R.id.saveBtn);
        goBack = findViewById(R.id.backBtn);
        editInterests = findViewById(R.id.editInterestsBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the User object from the Intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        // Load data into EditTexts
        if (user != null) {
            usernameEditText.setText(user.getUsername());
            emailEditText.setText(user.getEmail());
            passwordEditText.setText(user.getPassword());
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        // Set click listener for the back button
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity to return to the previous one
                finish();
            }
        });

        editInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, interests.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
    }

    private void saveUserProfile() {
        String newUsername = usernameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        if (currentUser != null) {
            DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());

            if (!newUsername.isEmpty()) {
                userRef.child("username").setValue(newUsername);
            }
            if (!newEmail.isEmpty()) {
                userRef.child("email").setValue(newEmail);
            }
            if (!newPassword.isEmpty()) {
                userRef.child("password").setValue(newPassword);
            }

            Toast.makeText(EditProfile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EditProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
        }
    }
}
