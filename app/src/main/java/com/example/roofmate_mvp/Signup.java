package com.example.roofmate_mvp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;

public class Signup extends AppCompatActivity {

    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private Button signupButton;
    private Button goBackButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup2);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        signupButton = findViewById(R.id.signupButton);
        goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setBackgroundColor(Color.TRANSPARENT);
        signupButton.setVisibility(View.VISIBLE);
        signupButton.setBackgroundColor(Color.TRANSPARENT);

        // Set onClick listener for sign-up button
        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(Signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(email, username, password, phoneNumber);
            }
        });

        // Set onClick listener for go back button
        goBackButton.setOnClickListener(v -> finish());
    }

    private void registerUser(String email, String username, String password, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // Register for Pushy notifications and get the token
                            new RegisterForPushyTask(userId, username, email, password, phoneNumber).execute();
                        }
                    } else {
                        Toast.makeText(Signup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class RegisterForPushyTask extends AsyncTask<Void, Void, String> {
        private String userId, username, email, password, phoneNumber;

        public RegisterForPushyTask(String userId, String username, String email, String password, String phoneNumber) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.password = password;
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Register the device for push notifications
                return Pushy.register(getApplicationContext());
            } catch (PushyException e) {
                // Registration failed
                return null;
            }
        }

        @Override
        protected void onPostExecute(String pushyToken) {
            if (pushyToken != null) {
                // Save user to database with the Pushy token
                saveUserToDatabase(userId, username, email, password, phoneNumber, pushyToken);
            } else {
                Toast.makeText(Signup.this, "Pushy registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserToDatabase(String userId, String username, String email, String password, String phoneNumber, String pushyToken) {
        User newUser = new User(userId, username, email, password, phoneNumber, pushyToken);

        mDatabase.child("users").child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Signup.this, interests.class);
                        intent.putExtra("user", newUser);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Signup.this, "Failed to save user information", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
