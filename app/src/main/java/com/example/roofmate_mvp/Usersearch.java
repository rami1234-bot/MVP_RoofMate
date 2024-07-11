package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Usersearch extends BaseActivity implements UserAdapter.OnUserClickListener {

    private Button SearchByInterests;
    private ListView listViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> filteredUserList;
    private DatabaseReference databaseReference;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersearch);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Set up the button to go to the interests search
        SearchByInterests = findViewById(R.id.Search_interests);
        SearchByInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Usersearch.this, InterestSearch.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        // Set up ListView and Adapter
        listViewUsers = findViewById(R.id.listViewUsers);
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
        userAdapter = new UserAdapter(this, filteredUserList);
        userAdapter.setOnUserClickListener(this);
        listViewUsers.setAdapter(userAdapter);

        // Fetch users from Firebase
        fetchUsersFromFirebase();

        // Set up SearchView
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });
    }

    private void fetchUsersFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("FirebaseData", "snapshot: " + snapshot.toString());
                    try {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                        Toast.makeText(Usersearch.this, "Error deserializing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                filteredUserList.clear();
                filteredUserList.addAll(userList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(Usersearch.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterUsers(String query) {
        filteredUserList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredUserList.addAll(userList);
        } else {
            for (User user : userList) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredUserList.add(user);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    //woidaiwo[dajiwodaojiwdjiawiod

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(Usersearch.this, Profile.class);
        intent.putExtra("userid", user.getUserid());
        intent.putExtra("fcm12", user.getFcmToken());
        startActivity(intent);
        Toast.makeText(Usersearch.this, "wdawd"+user.getFcmToken(), Toast.LENGTH_SHORT).show();

    }
}
