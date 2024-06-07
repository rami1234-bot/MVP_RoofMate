package com.example.roofmate_mvp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private FirebaseUser currentUser;
    private Button sendMesButton;
    private String otherUserId;

    private User user;

    private String s = "";

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);

        userIdTextView = findViewById(R.id.usernameTextView);
        sendMesButton = findViewById(R.id.sendmes);

        // Get the User object from the Intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        // Initialize the Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get the user ID from the Intent
        otherUserId = getIntent().getStringExtra("userId");

        // Check if the current user is viewing their own profile
        if (user != null && otherUserId != null && otherUserId.equals(user.getUserid())) {
            Intent editPIntent = new Intent(Profile.this, EditProfile.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();  // Close the current activity
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
        sendMesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null && otherUserId != null) {
                    createOrNavigateChatRoom(currentUser.getUid(), otherUserId);
                } else {
                    Toast.makeText(Profile.this, "User information is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void getUsernameAndDisplay(String userId) {
        // Fetch the username from the database using the user ID
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the User object
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Display the username
                        userIdTextView.setText(getString(R.string.username_label, user.username));
                        s = user.getUsername();

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
                    createChatRoom(chatRoomId1, userId1, userId2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Failed to check chat rooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChatRoom(String chatRoomId, String userId1, String userId2) {
        ChatRoom chatRoom = new ChatRoom(chatRoomId);
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
        intent.putExtra("oth", s);

        startActivity(intent);
    }
}
