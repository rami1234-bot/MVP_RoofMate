package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Profile extends BaseActivity {
    private TextView userIdTextView;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private Button sendMessageButton;
    private Button blockUnblockButton;
    private String otherUserId;
    private LinearLayout interestsLinearLayout;
    private User user;
    private String username = "";

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userIdTextView = findViewById(R.id.usernameTextView);
        sendMessageButton = findViewById(R.id.sendmes);
        blockUnblockButton = findViewById(R.id.blockUnblockButton);
        interestsLinearLayout = findViewById(R.id.interestsLinearLayout);

        // Get the User object from the Intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        // Initialize the Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the user ID from the Intent
        otherUserId = getIntent().getStringExtra("userid");

        // Check if the current user is viewing their own profile
        if (user != null && otherUserId != null && otherUserId.equals(user.getUserid())) {
            Intent editProfileIntent = new Intent(Profile.this, EditProfile.class);
            editProfileIntent.putExtra("user", user);
            startActivity(editProfileIntent);
            finish();
            return;
        }

        // Display the user ID (for demonstration purposes)
        userIdTextView.setText(getString(R.string.user_id_label, otherUserId));
        if (otherUserId != null) {
            // Fetch and display the username
            getUsernameAndDisplay(otherUserId);
        } else {
            Toast.makeText(this, R.string.user_id_missing, Toast.LENGTH_SHORT).show();
        }

        // Set the button click listener
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

        // Set the block/unblock button click listener
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

    private void createOrNavigateChatRoom(final String userId1, final String userId2) {
        final String chatRoomId1 = userId1 + userId2;
        final String chatRoomId2 = userId2 + userId1;

        mDatabase.child("chatrooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatRoomId1)) {
                    navigateToChatActivity(chatRoomId1);
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    navigateToChatActivity(chatRoomId2);
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
                    updateBlockStatus(chatRoomId1);
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    updateBlockStatus(chatRoomId2);
                } else {
                    createChatRoom(chatRoomId1, userId1, userId2, true);
                    blockUnblockButton.setText("Unblock");
                    Toast.makeText(Profile.this, "User blocked", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to check chat rooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBlockStatus(final String chatRoomId) {
        mDatabase.child("chatrooms").child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        boolean isBlocked = chatRoom.getBlocked();
                        chatRoom.setBlocked(!isBlocked);
                        mDatabase.child("chatrooms").child(chatRoomId).setValue(chatRoom)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        blockUnblockButton.setText(isBlocked ? "Block" : "Unblock");
                                        Toast.makeText(Profile.this, isBlocked ? "User unblocked" : "User blocked", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Profile.this, "Failed to update block status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to read chat room data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                    updateButtonStatus(chatRoomId1);
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    updateButtonStatus(chatRoomId2);
                } else {
                    blockUnblockButton.setText("Block");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to check chat rooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonStatus(final String chatRoomId) {
        mDatabase.child("chatrooms").child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        blockUnblockButton.setText(chatRoom.getBlocked() ? "Unblock" : "Block");
                    } else {
                        blockUnblockButton.setText("Block");
                    }
                } else {
                    blockUnblockButton.setText("Block");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to read chat room data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
