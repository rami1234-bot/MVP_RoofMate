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

public class Requests extends BaseActivity {

    private ListView listView;
    private List<String> uids;
    private UserAdapter12 adapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private User u1;

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
                    fetchOtherUserDataAndNavigate(otherUserId);
                } else {
                    Toast.makeText(Requests.this, "User information is missing", Toast.LENGTH_SHORT).show();
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

                        List<String> sharedList = getUniqueStringsInList2(list1, list2);

                        Toast.makeText(Requests.this, "Shared Chats: " + sharedList.size(), Toast.LENGTH_SHORT).show();

                        // Optionally, you can update the ListView with the shared chat list
                        uids.clear();
                        uids.addAll(sharedList);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(Requests.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Requests.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static List<String> getUniqueStringsInList2(List<String> list1, List<String> list2) {
        return list2.stream()
                .filter(element -> !list1.contains(element))
                .distinct()
                .collect(Collectors.toList());
    }

    private void fetchOtherUserDataAndNavigate(String otherUserId) {
        mDatabase.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User otherUser = dataSnapshot.getValue(User.class);
                    if (otherUser != null) {
                        String fcmToken = otherUser.getFcmToken(); // Assuming the FCM token is stored in the user object

                        Intent intent = new Intent(Requests.this, Profile.class);
                        intent.putExtra("userid", otherUserId);
                        intent.putExtra("fcm12", fcmToken);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Requests.this, "Other user data is missing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Requests.this, "Other user not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Requests.this, "Failed to fetch other user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
