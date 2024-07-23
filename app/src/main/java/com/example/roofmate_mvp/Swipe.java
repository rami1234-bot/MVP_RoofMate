package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Swipe extends BaseActivity {

    private ViewPager2 viewPager;
    private ProfilePagerAdapter adapter;
    private Button prevButton;
    private Button nextButton;
    private DatabaseReference mDatabase;
    private List<User> userList; // Make userList a member variable

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        viewPager = findViewById(R.id.viewPager);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize user list and adapter
        userList = new ArrayList<>();
        adapter = new ProfilePagerAdapter(userList);
        viewPager.setAdapter(adapter);

        // Fetch random users from the database
        fetchRandomUsers();

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem > 0) {
                    viewPager.setCurrentItem(currentItem - 1);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < adapter.getItemCount() - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                }
            }
        });
    }

    private void fetchRandomUsers() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear(); // Clear userList before adding new users
                List<User> allUsers = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        allUsers.add(user);
                    }
                }
                // Shuffle the user list
                Collections.shuffle(allUsers);

                // Add users to userList (maximum 2 or the size of allUsers)
                int userCount = Math.min(2, allUsers.size());
                userList.addAll(allUsers.subList(0, userCount));

                // Notify adapter about data changes
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Swipe.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                Log.e("Swipe", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
