package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class HomeInfo extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HomeInfo";
    private Toolbar toolbar;
    private TextView tvHouseName, tvHouseDescription;
    private Button btnToggleWishlist;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private  Button contact;
    private String ownerid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_info);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        contact = findViewById(R.id.btn_contact_owner);
        contact.setOnClickListener(this);
        toolbar = findViewById(R.id.tlbr);
        tvHouseName = findViewById(R.id.tv_house_name);
        tvHouseDescription = findViewById(R.id.tv_house_description);
        btnToggleWishlist = findViewById(R.id.btn_add_to_wishlist);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Home Information");
        }

        Intent intent = getIntent();
        ownerid= intent.getStringExtra("ownerid");
        String homeName = intent.getStringExtra("home_name");
        String homeDescription = intent.getStringExtra("home_description");
        String homeId = intent.getStringExtra("home_id");
        tvHouseName.setText(homeName);
        tvHouseDescription.setText(homeDescription);

        // Check if the home is in the user's wishlist and update the button text accordingly
        checkWishlistStatus(homeId);

        // Set the click listener for the button
        btnToggleWishlist.setOnClickListener(v -> toggleWishlist(homeId));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to add or remove home from wishlist
    private void toggleWishlist(String homeId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getWishlist() != null && user.getWishlist().contains(homeId)) {
                            // Remove from wishlist
                            user.removeFromWishlist(homeId);
                            userRef.setValue(user); // Update user object in database
                            btnToggleWishlist.setText("Add to Wishlist");
                            Toast.makeText(HomeInfo.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            // Add to wishlist
                            user.addToWishlist(homeId);
                            userRef.setValue(user); // Update user object in database
                            btnToggleWishlist.setText("Remove from Wishlist");
                            Toast.makeText(HomeInfo.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read user data", databaseError.toException());
                }
            });
        }
    }

    // Method to check if the home is in the user's wishlist and update button text
    private void checkWishlistStatus(String homeId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getWishlist() != null && user.getWishlist().contains(homeId)) {
                        btnToggleWishlist.setText("Remove from Wishlist");
                    } else {
                        btnToggleWishlist.setText("Add to Wishlist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Failed to read user data", databaseError.toException());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this,Profile.class);

        intent.putExtra("userid",ownerid);
        startActivity(intent);
    }
}
