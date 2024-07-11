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

public class Profile extends AppCompatActivity {
    private TextView userIdTextView;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private Button sendMessageButton;
    private Button blockUnblockButton;
    private Button submitReviewButton;

    Button sendrequest;
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

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        userIdTextView = findViewById(R.id.usernameTextView);
        sendMessageButton = findViewById(R.id.sendmes);
        blockUnblockButton = findViewById(R.id.blockUnblockButton);
        interestsLinearLayout = findViewById(R.id.interestsLinearLayout);
        reviewText = findViewById(R.id.reviewText);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        reviewStarsRecyclerView = findViewById(R.id.reviewStarsRecyclerView);
        reviewsListView = findViewById(R.id.reviewsListView);

        sendrequest = findViewById(R.id.sendrequest);

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



        if(otherUserId.equals(currentUser.getUid())){
            blockUnblockButton.setVisibility(View.INVISIBLE);
            submitReviewButton.setVisibility(View.INVISIBLE);
            reviewText.setVisibility(View.INVISIBLE);
            sendMessageButton.setVisibility(View.INVISIBLE);
            reviewStarsRecyclerView.setVisibility(View.INVISIBLE);
            sendrequest.setVisibility(View.INVISIBLE);
           // Toast.makeText(this, currentUser.getUid()+otherUserId, Toast.LENGTH_SHORT).show();
          //  Toast.makeText(this, otherUserId, Toast.LENGTH_SHORT).show();



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

        sendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToLists(currentUser.getUid(),otherUserId);
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

//    private void createOrNavigateChatRoom(final String userId1, final String userId2) {
//        final String chatRoomId1 = userId1 + userId2;
//        final String chatRoomId2 = userId2 + userId1;
//
//        mDatabase.child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild(chatRoomId1)) {
//                    navigateToChatActivity(chatRoomId1);
//                } else if (dataSnapshot.hasChild(chatRoomId2)) {
//                    navigateToChatActivity(chatRoomId2);
//                } else {
//                    createChatRoom(chatRoomId1, userId1, userId2, false);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(Profile.this, "Failed to check chat rooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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
                Toast.makeText(Profile.this, "Failed to check chat rooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleBlockStatus(final String userId1, final String userId2) {
        final String chatRoomId1 = userId1 + userId2;
        final String chatRoomId2 = userId2 + userId1;

        mDatabase.child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatRoomId1)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId1).getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        chatRoom.setBlocked(!chatRoom.getBlocked());
                        mDatabase.child("chatrooms").child(chatRoomId1).setValue(chatRoom)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, chatRoom.getBlocked() ? "User blocked" : "User unblocked", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Profile.this, "Failed to update block status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId2).getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        chatRoom.setBlocked(!chatRoom.getBlocked());
                        mDatabase.child("chatrooms").child(chatRoomId2).setValue(chatRoom)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, chatRoom.getBlocked() ? "User blocked" : "User unblocked", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Profile.this, "Failed to update block status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    createChatRoom(chatRoomId1, userId1, userId2, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to check chat rooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBlockStatus(final String userId1, final String userId2) {
        final String chatRoomId1 = userId1 + userId2;
        final String chatRoomId2 = userId2 + userId1;

        mDatabase.child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatRoomId1)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId1).getValue(ChatRoom.class);
                    if (chatRoom != null && chatRoom.getBlocked()) {
                        blockUnblockButton.setText(R.string.unblock_user);
                    } else {
                        blockUnblockButton.setText("block");
                    }
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId2).getValue(ChatRoom.class);
                    if (chatRoom != null && chatRoom.getBlocked()) {
                        blockUnblockButton.setText("unblock");
                    } else {
                        blockUnblockButton.setText(R.string.block_user);
                    }
                } else {
                    blockUnblockButton.setText(R.string.block_user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to check block status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchReviews(String userId) {
        mDatabase.child("users").child(userId).child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    if (review != null) {
                        reviewList.add(review);
                    }
                }
                reviewAdapter.notifyDataSetChanged();
                calculateAverageRating(reviewList);

                // Log reviews to verify
                for (Review review : reviewList) {
                    Log.d("Profile", "Review: " + review.getStars() + " - " + review.getText());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to fetch reviews: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void calculateAverageRating(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return;
        }

        int totalStars = 0;
        for (Review review : reviews) {
            totalStars += review.getStars();
        }
        double averageRating = (double) totalStars / reviews.size();

        // Update UI with average rating
        TextView averageRatingTextView = findViewById(R.id.averageRatingTextView);
        averageRatingTextView.setText("averege rating"+ averageRating);
    }
    private void submitReview() {
        String reviewTextValue = reviewText.getText().toString().trim();
        if (selectedStars == 0 || reviewTextValue.isEmpty()) {
            Toast.makeText(this, "Please select a rating and write a review.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Review newReview = new Review(selectedStars, reviewTextValue, currentUser.getUid());

        // Add the new review to the local list
        reviewList.add(newReview);
        reviewAdapter.notifyDataSetChanged(); // Notify adapter of data change

        // Update the Firebase database with the new review
        if (otherUserId != null) {
            mDatabase.child("users").child(otherUserId).child("reviews").push().setValue(newReview)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Review submitted successfully.", Toast.LENGTH_SHORT).show();
                            // Clear review text field
                            reviewText.setText("");
                            // Reset selected stars
                            selectedStars = 0;
                            // Calculate and display updated average rating
                            calculateAverageRating(reviewList);
                        } else {
                            Toast.makeText(Profile.this, "Failed to submit review.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Other user information is missing.", Toast.LENGTH_SHORT).show();
        }
    }





    private void addToLists(String currentUserId, String otherUserId) {
        // Add current user's ID to other user's receivedFrom list
        mDatabase.child("users").child(otherUserId).child("receivedFrom").child(currentUserId).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Profile.this, "Added to received list", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profile.this, "Failed to add to received list", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add other user's ID to current user's sentTo list
        mDatabase.child("users").child(currentUserId).child("sentTo").child(otherUserId).setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Profile.this, "Added to sent list", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profile.this, "Failed to add to sent list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
