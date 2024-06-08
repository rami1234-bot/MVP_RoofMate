package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

public class OwnHomes extends BaseActivity {

    private ListView homeListView;
    private HomeAdapter homeAdapter;
    private List<Home> homeList = new ArrayList<>();

    private FirebaseDatabase database;
    private DatabaseReference homesRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_homes);

        // Initialize Firebase Database and get current user
        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        homesRef = database.getReference("homes");

        // Initialize ListView and Adapter
        homeListView = findViewById(R.id.homeListView);
        homeAdapter = new HomeAdapter(this, homeList);
        homeListView.setAdapter(homeAdapter);

        // Load user's homes from Firebase
        loadUserHomes();
    }

    private void loadUserHomes() {
        String userId = currentUser.getUid();
        homesRef.orderByChild("ownerid").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                homeList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Home home = snapshot.getValue(Home.class);
                    homeList.add(home);
                }
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OwnHomes.this, "Failed to load homes", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
