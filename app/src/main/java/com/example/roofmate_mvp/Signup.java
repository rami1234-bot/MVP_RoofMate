package com.example.roofmate_mvp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Signup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private Button goBackButton;
    private ImageView profileImageView;
    private Button selectProfileImageButton;

    private Uri profileImageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);
        goBackButton = findViewById(R.id.goBackButton);
        profileImageView = findViewById(R.id.profileImageView);
        selectProfileImageButton = findViewById(R.id.selectProfileImageButton);

        // Set onClick listener for select profile image button
        selectProfileImageButton.setOnClickListener(v -> openImagePicker());

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

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUser(String email, String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            uploadProfileImage(userId, username, email, password);
                        }
                    } else {
                        Toast.makeText(Signup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadProfileImage(String userId, String username, String email, String password) {
        if (profileImageUri != null) {
            StorageReference profileImageRef = mStorage.child("profile_images").child(userId + ".jpg");
            profileImageRef.putFile(profileImageUri).addOnSuccessListener(taskSnapshot -> {
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String profileImageUrl = uri.toString();
                    saveUserToDatabase(userId, username, email, password, profileImageUrl);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(Signup.this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            saveUserToDatabase(userId, username, email, password, null);
        }
    }

    private void saveUserToDatabase(String userId, String username, String email, String password, @Nullable String profileImageUrl) {
        User newUser = new User(userId, username, email, password, profileImageUrl);

        mDatabase.child("users").child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signup.this, HomePage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Signup.this, "Failed to save user information", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
