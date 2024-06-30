package com.example.roofmate_mvp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class test extends AppCompatActivity {
    SearchView searchView;
    ListView myListView;
    ArrayList<String> interestsList;
    ArrayAdapter<String> adapter;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        searchView = findViewById(R.id.searchview);
        myListView = findViewById(R.id.list);

        interestsList = new ArrayList<>(Arrays.asList("Music", "Sports", "Travel", "Reading", "Cooking",
                "Gaming", "Dancing", "Photography", "Art", "Fishing", "Gardening", "Hiking", "Knitting",
                "Cycling", "Yoga", "Fitness", "Swimming", "Baking", "Writing", "Meditation", "Board Games",
                "Volunteering", "Language Learning", "Astrology", "Technology", "Cars"));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, interestsList);
        myListView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedInterest = adapter.getItem(position);
                saveInterestToDatabase(selectedInterest);
            }
        });
    }

    private void saveInterestToDatabase(String interest) {
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("users").child(userId).child("interests").push().setValue(interest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(test.this, "Interest " + interest + " saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(test.this, "Failed to save interest", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
