package com.example.roofmate_mvp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeInfo extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvHouseName, tvHouseDescription;
    private ListView listViewPictures;
    private Button btnAddToWishlist; // Updated button name

    private List<String> pictureUrls; // This will store the URLs of the pictures
    //private PictureAdapter pictureAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_info);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.tlbr);
        tvHouseName = findViewById(R.id.tv_house_name);
        tvHouseDescription = findViewById(R.id.tv_house_description);
        listViewPictures = findViewById(R.id.listView_pictures);
        btnAddToWishlist = findViewById(R.id.btn_add_to_wishlist); // Updated button reference
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Home Information");
        }

        Intent intent = getIntent();
        String homeName = intent.getStringExtra("home_name");
        String homeDescription = intent.getStringExtra("home_description");
        tvHouseName.setText(homeName);
        tvHouseDescription.setText(homeDescription);

        // Sample data for pictures
        pictureUrls = new ArrayList<>();
        pictureUrls.add("https://example.com/pic1.jpg");
        pictureUrls.add("https://example.com/pic2.jpg");
        pictureUrls.add("https://example.com/pic3.jpg");

        // Initialize and set adapter for ListView
       // pictureAdapter = new PictureAdapter(this, pictureUrls);
        //listViewPictures.setAdapter(pictureAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.tool1) {
            Intent intent = new Intent(HomeInfo.this, HomePage.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // Method to add home to wishlist
// Method to add home to wishlist
    // Method to add home to wishlist
    public void addToWishlist(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String homeId = getIntent().getStringExtra("home_id"); // Get the ID of the home from the intent
            DatabaseReference userRef = mDatabase.child("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        user.addToWishlist(homeId); // Add home ID to wishlist
                        userRef.setValue(user); // Update user object in database
                        Toast.makeText(HomeInfo.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read user data", databaseError.toException());
                }
            });
        }
    }


}
