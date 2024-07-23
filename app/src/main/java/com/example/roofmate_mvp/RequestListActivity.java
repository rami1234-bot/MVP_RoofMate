package com.example.roofmate_mvp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestListActivity extends BaseActivity {

    private ListView listView;
    private RequestListAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private List<String> receivedList;
    private List<String> sentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        listView = findViewById(R.id.listView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser != null ? currentUser.getUid() : null;

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        receivedList = new ArrayList<>();
        sentList = new ArrayList<>();

        fetchReceivedAndSentLists();
    }

    private void fetchReceivedAndSentLists() {
        if (currentUserId == null) return;

        mDatabase.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    receivedList = currentUser.getReceived() != null ? currentUser.getReceived() : new ArrayList<>();
                    sentList = currentUser.getSent() != null ? currentUser.getSent() : new ArrayList<>();

                    List<String> filteredReceivedList = new ArrayList<>();
                    for (String receivedId : receivedList) {
                        if (!sentList.contains(receivedId)) {
                            filteredReceivedList.add(receivedId);
                        }
                    }

                    adapter = new RequestListAdapter(RequestListActivity.this, filteredReceivedList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestListActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                Log.e("RequestListActivity", "Database error: " + error.getMessage());
            }
        });
    }
}
