package com.example.roofmate_mvp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends AppCompatActivity {

    private ListView listView;
    private Ua adapter;
    private List<String> favoriteUserIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = findViewById(R.id.tlbr);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.listView);
        favoriteUserIds = new ArrayList<>();
        adapter = new Ua(this, favoriteUserIds);
        listView.setAdapter(adapter);

        fetchFavoriteUsers();
    }

    private void fetchFavoriteUsers() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null) {
            String currentUserId = currentFirebaseUser.getUid();
            FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("favorites")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            favoriteUserIds.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String userId = dataSnapshot.getValue(String.class);
                                if (userId != null) {
                                    favoriteUserIds.add(userId);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Wishlist.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
