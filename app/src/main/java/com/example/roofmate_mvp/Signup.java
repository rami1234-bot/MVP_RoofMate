package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private Button goBackButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);
        goBackButton = findViewById(R.id.goBackButton);

        // Set onClick listener for sign-up button
        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(Signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(email, username, password);
            }
        });

        // Set onClick listener for go back button
        goBackButton.setOnClickListener(v -> finish());
    }

    private void registerUser(String email, String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            saveUserToDatabase(userId, username, email, password);
                        }
                    } else {
                        Toast.makeText(Signup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String username, String email, String password) {
        User newUser = new User(userId, username, email, password);

        mDatabase.child("users").child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup.this, interests.class);
                        intent.putExtra("user",newUser);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Signup.this, "Failed to save user information", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
