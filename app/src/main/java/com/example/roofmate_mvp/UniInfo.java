package com.example.roofmate_mvp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class UniInfo extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText universityIdEditText;
    private ImageView universityPictureImageView;
    private Button selectImageButton;
    private EditText universityNameEditText;
    private Button nextButton;

    private Uri imageUri;
    private User user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uni_info);

        // Initialize Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        universityIdEditText = findViewById(R.id.universityIdEditText);
        universityPictureImageView = findViewById(R.id.universityPictureImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        universityNameEditText = findViewById(R.id.universityNameEditText);
        nextButton = findViewById(R.id.nextButton);

        // Retrieve user object from the intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            // Pre-fill user information if available
            universityIdEditText.setText(user.getUniversityId());
            universityNameEditText.setText(user.getUniversityName());

            // Load and display the user picture if available
            if (user.getUniversityPicture() != null) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(user.getUniversityPicture())));
                    universityPictureImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Set onClick listener for select image button
        selectImageButton.setOnClickListener(v -> openImageChooser());

        // Set onClick listener for next button
        nextButton.setOnClickListener(v -> {
            String universityId = universityIdEditText.getText().toString().trim();
            String universityName = universityNameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(universityId) || TextUtils.isEmpty(universityName)) {
                Toast.makeText(UniInfo.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Update user object with university details
                if (user != null) {
                    user.setUniversityId(universityId);
                    if (imageUri != null) {
                        user.setUniversityPicture(imageUri.toString()); // Save image URI if available
                    }
                    user.setUniversityName(universityName);

                    // Save updated user information to the database
                    mDatabase.child("users").child(user.getUserid()).setValue(user)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UniInfo.this, "University information saved successfully", Toast.LENGTH_SHORT).show();
                                    // Navigate to interests activity with updated user
                                    Intent interestsIntent = new Intent(UniInfo.this, interests.class);
                                    interestsIntent.putExtra("user", user);
                                    startActivity(interestsIntent);
                                    finish();
                                } else {
                                    Toast.makeText(UniInfo.this, "Failed to save university information", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                universityPictureImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
