package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.roofmate_mvp.Profile;
import com.example.roofmate_mvp.R;
import com.example.roofmate_mvp.User;
import com.example.roofmate_mvp.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Usersearch extends AppCompatActivity {

    private ListView listViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private List<User> filteredUserList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersearch);

        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);

        // Initialize Firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, handle accordingly
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("users");

            // Set up ListView and Adapter
            listViewUsers = findViewById(R.id.listViewUsers);
            userList = new ArrayList<>();
            filteredUserList = new ArrayList<>();
            userAdapter = new UserAdapter(this, filteredUserList);
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

            // Set item click listener for ListView
            listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
                // Retrieve the selected user
                User selectedUser = filteredUserList.get(position);

                // Start Profile activity with selected user's ID
                Intent intent = new Intent(Usersearch.this, Profile.class);
                intent.putExtra("userId", selectedUser.getUserid());
                startActivity(intent);
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void fetchUsersFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                filteredUserList.addAll(userList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tool1) {
            Intent intent = new Intent(Usersearch.this, HomePage.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("userId", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool2) {
            Intent intent = new Intent(Usersearch.this, Profile.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("userId", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool3) {
            Intent intent = new Intent(Usersearch.this, AddHome.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool4) {
            Intent intent = new Intent(Usersearch.this, HomeSearch.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool5) {
            Intent intent = new Intent(Usersearch.this, Usersearch.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool10) {
            Intent intent = new Intent(Usersearch.this, OwnHomes.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else if (id == R.id.tool13) {
            Intent intent = new Intent(Usersearch.this, Usersearch.class);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            intent.putExtra("uid", currentUser.getUid());
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
