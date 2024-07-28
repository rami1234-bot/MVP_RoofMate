package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Swipe extends BaseActivity implements OnProfileActionListener {

    private ViewPager2 viewPager;
    private ProfilePagerAdapter adapter;
    private Button prevButton;
    private Button nextButton;
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;
    private List<User> userList;
    private User currentUser;
    private String livingSituationFilter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        viewPager = findViewById(R.id.viewPager);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        userRef = mDatabase.child(FirebaseAuth.getInstance().getUid());

        userList = new ArrayList<>();
        adapter = new ProfilePagerAdapter(userList, this); // Pass 'this' as the listener
        viewPager.setAdapter(adapter);

        // Get filter criteria from Intent
        livingSituationFilter = getIntent().getStringExtra("livingSituation");

        fetchCurrentUser();

        prevButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });
    }

    @Override
    public void onSendRequestClick(int position) {
        int nextPosition = (position + 1) % adapter.getItemCount();
        viewPager.setCurrentItem(nextPosition, true);
    }

    @Override
    public void onDislikeClick(int position) {
        int nextPosition = (position + 1) % adapter.getItemCount();
        viewPager.setCurrentItem(nextPosition, true);
    }

    private void fetchCurrentUser() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    fetchFilteredUsers();
                } else {
                    Toast.makeText(Swipe.this, "Failed to load current user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Swipe.this, "Failed to load current user", Toast.LENGTH_SHORT).show();
                Log.e("Swipe", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void fetchFilteredUsers() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> allUsers = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && !user.getUserid().equals(currentUser.getUserid()) && user.isAvg()) {
                        allUsers.add(user);
                    }
                }

                List<User> filteredUsers = new ArrayList<>();
                List<String> currentUserInterests = currentUser.getInterests();

                if (currentUserInterests != null) {
                    for (User user : allUsers) {
                        List<String> userInterests = user.getInterests();
                        if (userInterests != null) {
                            for (String interest : currentUserInterests) {
                                if (userInterests.contains(interest)) {
                                    filteredUsers.add(user);
                                    break;
                                }
                            }
                        }
                    }
                }

                // Apply living situation filter
                if (livingSituationFilter != null) {
                    filteredUsers.removeIf(user -> !livingSituationFilter.equals(user.getLivingSituation()));
                }

                if (filteredUsers.isEmpty() && !allUsers.isEmpty()) {
                    Collections.shuffle(allUsers);
                    int userCount = Math.min(10, allUsers.size());
                    filteredUsers.addAll(allUsers.subList(0, userCount));
                }

                if (!filteredUsers.isEmpty()) {
                    Collections.shuffle(filteredUsers);
                }

                userList.clear();
                userList.addAll(filteredUsers);
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
