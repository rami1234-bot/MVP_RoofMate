package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.stream.Collectors;

public class Chatlist extends BaseActivity {

    private ListView listView;
    private List<String> uids;
    private UserAdapter12 adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User u1;
    private User u2;
    private String userId2;
    private String username = ""; // Add this to store the username
    private String fcmcd = ""; // Add this to store the FCM token

    List<String> list1;
    List<String> list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Toolbar toolbar = findViewById(R.id.tlbr);
            setSupportActionBar(toolbar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        listView = findViewById(R.id.listView);
        uids = new ArrayList<>();

        adapter = new UserAdapter12(this, uids);
        listView.setAdapter(adapter);

        // Get the current user
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            loadCurrentUser(userId);
        }

        // Set item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String otherUserId = uids.get(position);
                if (mAuth.getCurrentUser() != null && otherUserId != null) {
                    checkAndNavigateToChatRoom(mAuth.getCurrentUser().getUid(), otherUserId);
                } else {
                    Toast.makeText(Chatlist.this, "User information is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCurrentUser(String userId) {
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    u1 = dataSnapshot.getValue(User.class);
                    if (u1 != null) {
                        list1 = new ArrayList<>();
                        list2 = new ArrayList<>();

                        if (dataSnapshot.hasChild("sentTo")) {
                            for (DataSnapshot snapshot : dataSnapshot.child("sentTo").getChildren()) {
                                list1.add(snapshot.getKey());
                            }
                        }

                        if (dataSnapshot.hasChild("receivedFrom")) {
                            for (DataSnapshot snapshot : dataSnapshot.child("receivedFrom").getChildren()) {
                                list2.add(snapshot.getKey());
                            }
                        }

                        List<String> sharedList = getSharedStrings(list1, list2);

                        Toast.makeText(Chatlist.this, "Shared Chats: " + sharedList.size(), Toast.LENGTH_SHORT).show();

                        // Optionally, you can update the ListView with the shared chat list
                        uids.clear();
                        uids.addAll(sharedList);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(Chatlist.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Chatlist.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static List<String> getSharedStrings(List<String> list1, List<String> list2) {
        return list1.stream()
                .filter(list2::contains)
                .distinct()
                .collect(Collectors.toList());
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
                        Toast.makeText(Chatlist.this, "You are blocked and can't chat", Toast.LENGTH_SHORT).show();
                    } else {
                        navigateToChatActivity(chatRoomId1, userId2);
                    }
                } else if (dataSnapshot.hasChild(chatRoomId2)) {
                    ChatRoom chatRoom = dataSnapshot.child(chatRoomId2).getValue(ChatRoom.class);
                    if (chatRoom != null && chatRoom.getBlocked()) {
                        Toast.makeText(Chatlist.this, "You are blocked and can't chat", Toast.LENGTH_SHORT).show();
                    } else {
                        navigateToChatActivity(chatRoomId2, userId2);
                    }
                } else {
                    createChatRoom(chatRoomId1, userId1, userId2, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Chatlist.this, "Failed to check chat rooms: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChatRoom(String chatRoomId, String userId1, String userId2, boolean isBlocked) {
        ChatRoom chatRoom = new ChatRoom(chatRoomId, isBlocked);
        mDatabase.child("chatrooms").child(chatRoomId).setValue(chatRoom)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToChatActivity(chatRoomId, userId2);
                    } else {
                        Toast.makeText(Chatlist.this, "Failed to create chat room", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToChatActivity(String chatRoomId, String otherUserId) {
        // Fetch the username and FCM token of the other user
        mDatabase.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        username = user.getUsername();
                        fcmcd = user.getFcmToken(); // Assuming the FCM token is stored in the user object

                        Intent intent = new Intent(Chatlist.this, Chat.class);
                        intent.putExtra("chatRoomId", chatRoomId);
                        intent.putExtra("otherUsername", username);
                        intent.putExtra("fcm", fcmcd);
                        intent.putExtra("otheruserid", otherUserId);

                        startActivity(intent);
                    } else {
                        Toast.makeText(Chatlist.this, "User data is missing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Chatlist.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Chatlist.this, "Failed to fetch user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
