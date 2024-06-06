package com.example.roofmate_mvp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    private TextView userIdTextView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);

        userIdTextView = findViewById(R.id.usernameTextView);

        // Initialize the Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the user ID from the Intent
        String userId = getIntent().getStringExtra("userId");

        // Display the user ID (for demonstration purposes)
        userIdTextView.setText("User ID: " + userId);
        if (userId != null) {
            // Fetch and display the username
            getUsernameAndDisplay(userId);
        } else {
            Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void getUsernameAndDisplay(String userId) {
        // Fetch the username from the database using the user ID
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the User object
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Display the username
                        userIdTextView.setText("Username: " + user.username);
                    } else {
                        Toast.makeText(Profile.this, "User data is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Profile.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to read user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tool1) {
            Intent intent = new Intent(Profile.this, Profile.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("userId",currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool2) {
            Intent intent = new Intent(Profile.this, Profile.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("userId",currentUser.getUid());
            startActivity(intent);
            return true;
        } else
        if (id == R.id.tool9) {
            Intent intent = new Intent(Profile.this, HomePage.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("userId",currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool3) {
            Intent intent = new Intent(Profile.this, AddHome.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid",currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool4) {
            Intent intent = new Intent(Profile.this, HomeSearch.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid",currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool5) {
            Intent intent = new Intent(Profile.this, Usersearch.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid",currentUser.getUid());
            startActivity(intent);
            return true;
        }
        else if (id == R.id.tool10) {
            Intent intent = new Intent(Profile.this, OwnHomes.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid",currentUser.getUid());
            startActivity(intent);
            return true;
        }
        else if (id == R.id.tool13) {
            Intent intent = new Intent(Profile.this, Usersearch.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid",currentUser.getUid());
            startActivity(intent);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
}
