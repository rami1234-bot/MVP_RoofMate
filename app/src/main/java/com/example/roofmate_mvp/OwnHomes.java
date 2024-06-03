package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.roofmate_mvp.HomeInfo;
import com.example.roofmate_mvp.PictureAdapter;
import com.example.roofmate_mvp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OwnHomes extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listViewOwnHomes;
    private PictureAdapter pictureAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_homes);

        toolbar = findViewById(R.id.tlbr);
        listViewOwnHomes = findViewById(R.id.listView_own_homes);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Homes");
        }

        fetchUserHomesFromDatabase();

        // Set click listener for ListView items
        listViewOwnHomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedHomeName = (String) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(OwnHomes.this, HomeInfo.class);
                intent.putExtra("home_name", selectedHomeName);
                startActivity(intent);
            }
        });
    }

    private void fetchUserHomesFromDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("homes");
            Query query = databaseReference.orderByChild("ownerUid").equalTo(currentUser.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> ownHomes = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String homeName = snapshot.child("name").getValue(String.class);
                        ownHomes.add(homeName);
                    }
                    pictureAdapter = new PictureAdapter(OwnHomes.this, ownHomes);
                    listViewOwnHomes.setAdapter(pictureAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
