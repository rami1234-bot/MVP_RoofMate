package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Profile extends BaseActivity
{
    private TextView userIdTextView;
    private TextView uni;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private Button sendMessageButton;
    private Button blockUnblockButton;
    private Button submitReviewButton;

    private EditText reviewText;
    private RecyclerView reviewStarsRecyclerView;
    private String otherUserId;
    private LinearLayout interestsLinearLayout;
    private User user;
    private String username = "";
    private String fcmcd = "";
    private int selectedStars = 0;  // This should be set based on user selection in RecyclerView

    // UI elements for reviews
    private ListView reviewsListView;
    private ReviewsAdapter reviewAdapter;
    private List<Review> reviewList;

    private DatabaseReference userRef;
    private Switch avgSwitch;
    private User currentUser1;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        avgSwitch = findViewById(R.id.avgSwitch);

        // Initialize UI elements
        userIdTextView = findViewById(R.id.usernameTextView);
        sendMessageButton = findViewById(R.id.sendmes);
        blockUnblockButton = findViewById(R.id.blockUnblockButton);
        interestsLinearLayout = findViewById(R.id.interestsLinearLayout);
        reviewText = findViewById(R.id.reviewText);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        reviewStarsRecyclerView = findViewById(R.id.reviewStarsRecyclerView);
        uni = findViewById(R.id.universityNameTextView);

        reviewsListView = findViewById(R.id.reviewsListView);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);


        // Set up RecyclerView for review stars
        reviewStarsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ReviewAdapter reviewStarsAdapter = new ReviewAdapter(new ReviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int stars) {
                selectedStars = stars;
            }
        });
        reviewStarsRecyclerView.setAdapter(reviewStarsAdapter);

        // Initialize review list and adapter
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewsAdapter(this, reviewList);
        reviewsListView.setAdapter(reviewAdapter);

        // Get the User object from the Intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        fcmcd = intent.getStringExtra("fcm12");

        // Initialize the Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the user ID from the Intent
        otherUserId = getIntent().getStringExtra("userid");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (otherUserId.equals(currentUser.getUid())) {
            blockUnblockButton.setVisibility(View.INVISIBLE);
            submitReviewButton.setVisibility(View.INVISIBLE);
            reviewText.setVisibility(View.INVISIBLE);
            sendMessageButton.setVisibility(View.INVISIBLE);
            reviewStarsRecyclerView.setVisibility(View.INVISIBLE);
        }

        // Fetch and display user data
        if (otherUserId != null) {
            getUsernameAndDisplay(otherUserId);
            fetchReviews(otherUserId); // Fetch reviews for the user
        } else {
            Toast.makeText(this, R.string.user_id_missing, Toast.LENGTH_SHORT).show();
        }

        // Set up submit review button listener
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });



        // Existing logic for sendMessageButton and blockUnblockButton...
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null && otherUserId != null) {
                    checkAndNavigateToChatRoom(currentUser.getUid(), otherUserId);
                } else {
                    Toast.makeText(Profile.this, "User information is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        blockUnblockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBlockStatus(currentUser.getUid(), otherUserId);
            }
        });


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser1 = dataSnapshot.getValue(User.class);
                if (currentUser1 != null) {
                    // Set the switch state based on the avg property
                    avgSwitch.setChecked(currentUser1.isAvg());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(Profile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up a listener for the switch
        avgSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentUser1 != null) {


                currentUser1.setAvg(isChecked);
                userRef.setValue(currentUser1);
            }
        });
    }

    private void getUsernameAndDisplay(String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        userIdTextView.setText(user.getUsername());
                        username = user.getUsername();
                        uni.setText(user.getUniversityName());
                        displayInterests(user.getInterests());
                        checkBlockStatus(currentUser.getUid(), otherUserId);
                    } else {
                        Toast.makeText(Profile.this, R.string.user_data_null, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Profile.this, R.string.user_not_found, Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, getString(R.string.read_user_data_failed, databaseError.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayInterests(List<String> interests) {
        interestsLinearLayout.removeAllViews();
        if (interests != null && !interests.isEmpty()) {
            for (String interest : interests) {
                TextView interestTextView = new TextView(this);
                interestTextView.setText(interest);
                interestTextView.setPadding(8, 8, 8, 8);
                interestTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.interest_background));
                interestsLinearLayout.addView(interestTextView);
            }
        } else {
            TextView noInterestsTextView = new TextView(this);
            noInterestsTextView.setText(R.string.no_interests_available);
            interestsLinearLayout.addView(noInterestsTextView);
        }
    }

    private void createChatRoom(String chatRoomId, String userId1, String userId2, boolean isBlocked) {
        ChatRoom chatRoom = new ChatRoom(chatRoomId, isBlocked);
        mDatabase.child("chatrooms").child(chatRoomId).setValue(chatRoom)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToChatActivity(chatRoomId);
                    } else {
                        Toast.makeText(Profile.this, "Failed to create chat room", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToChatActivity(String chatRoomId) {
        Intent intent = new Intent(Profile.this, Chat.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.putExtra("otherUsername", username);
        intent.putExtra("fcm", fcmcd);
        intent.putExtra("otheruserid", otherUserId);
        startActivity(intent);
    }

    private void checkAndNavigateToChatRoom(final String userId1, final String userId2) {
        final String chatRoomId1 = userId1 + userId2;
        final String chatRoomId2 = userId2 + userId1;

        mDatabase.child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatRoomId1)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId1).getValue(ChatRoom.class);
                    if (chatRoom != null && chatRoom.getBlocked()) {
                        Toast.makeText(Profile.this, "You are blocked and can't chat", Toast.LENGTH_SHORT).show();
                    } else {
                        navigateToChatActivity(chatRoomId1);
                    }
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId2).getValue(ChatRoom.class);
                    if (chatRoom != null && chatRoom.getBlocked()) {
                        Toast.makeText(Profile.this, "You are blocked and can't chat", Toast.LENGTH_SHORT).show();
                    } else {
                        navigateToChatActivity(chatRoomId2);
                    }
                } else {
                    createChatRoom(chatRoomId1, userId1, userId2, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to check chat room: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBlockStatus(String userId1, String userId2) {
        final String chatRoomId1 = userId1 + userId2;
        final String chatRoomId2 = userId2 + userId1;

        mDatabase.child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatRoomId1)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId1).getValue(ChatRoom.class);
                    if (chatRoom != null && chatRoom.getBlocked()) {
                        blockUnblockButton.setText("Unblock");
                    } else {
                        blockUnblockButton.setText("Block");
                    }
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId2).getValue(ChatRoom.class);
                    if (chatRoom != null && chatRoom.getBlocked()) {
                        blockUnblockButton.setText("Unblock");
                    } else {
                        blockUnblockButton.setText("Block");
                    }
                } else {
                    blockUnblockButton.setText("Block");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to check block status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleBlockStatus(String userId1, String userId2) {
        final String chatRoomId1 = userId1 + userId2;
        final String chatRoomId2 = userId2 + userId1;

        mDatabase.child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatRoomId1)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId1).getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        boolean newBlockStatus = !chatRoom.getBlocked();
                        mDatabase.child("chatrooms").child(chatRoomId1).child("blocked").setValue(newBlockStatus);
                        blockUnblockButton.setText(newBlockStatus ? "Unblock" : "Block");
                    }
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId2).getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        boolean newBlockStatus = !chatRoom.getBlocked();
                        mDatabase.child("chatrooms").child(chatRoomId2).child("blocked").setValue(newBlockStatus);
                        blockUnblockButton.setText(newBlockStatus ? "Unblock" : "Block");
                    }
                } else {
                    createChatRoom(chatRoomId1, userId1, userId2, true);
                    blockUnblockButton.setText("Unblock");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to toggle block status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview() {
        String reviewTextValue = reviewText.getText().toString().trim();

        if (reviewTextValue.isEmpty()) {
            Toast.makeText(Profile.this, "Review text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedStars == 0) {
            Toast.makeText(Profile.this, "Please select a star rating", Toast.LENGTH_SHORT).show();
            return;
        }

        Review newReview = new Review(selectedStars, reviewTextValue, currentUser.getUid());

        // Get the other user's object
        mDatabase.child("users").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User otherUser = dataSnapshot.getValue(User.class);

                if (otherUser != null) {
                    List<Review> otherUserReviews = otherUser.getReviews();
                    if (otherUserReviews == null) {
                        otherUserReviews = new ArrayList<>();
                    }
                    otherUserReviews.add(newReview);
                    otherUser.setReviews(otherUserReviews);

                    // Save the updated user object back into the database
                    mDatabase.child("users").child(otherUserId).setValue(otherUser)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Profile.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                    reviewText.setText(""); // Clear the review text field
                                    fetchReviews(otherUserId); // Refresh the reviews list
                                } else {
                                    Toast.makeText(Profile.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Profile.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to retrieve user information: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviews(String userId) {
        mDatabase.child("users").child(userId).child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList.clear(); // Clear the existing list
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    reviewList.add(review);
                }
                reviewAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to fetch reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToLists(String userId1, String userId2) {
        DatabaseReference currentUserRef = mDatabase.child("users").child(userId1);
        DatabaseReference otherUserRef = mDatabase.child("users").child(userId2);

        // Add user2 to user1's sent list
        currentUserRef.child("sent").child(userId2).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    // Add user1 to user2's received list
                    otherUserRef.child("received").child(userId1).setValue(true)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(Profile.this, "Users added to respective lists", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Profile.this, "Failed to add user1 to user2's received list", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile.this, "Failed to add user2 to user1's sent list", Toast.LENGTH_SHORT).show();
                });
    }
}
