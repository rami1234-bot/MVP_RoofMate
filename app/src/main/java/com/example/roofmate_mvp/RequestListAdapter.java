package com.example.roofmate_mvp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> receivedList;
    private DatabaseReference mDatabase;
    private String currentUserId;

    public RequestListAdapter(Context context, List<String> receivedList) {
        super(context, 0, receivedList);
        this.context = context;
        this.receivedList = receivedList;
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser != null ? currentUser.getUid() : null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.request_list_item, parent, false);
        }

        String userId = getItem(position);
        TextView userIdTextView = convertView.findViewById(R.id.userIdTextView);
        Button acceptButton = convertView.findViewById(R.id.acceptButton);

        userIdTextView.setText(userId);

        acceptButton.setOnClickListener(v -> {
            addToSentList(userId);
        });

        return convertView;
    }

    private void addToSentList(String userId) {
        if (currentUserId == null) return;

        mDatabase.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                if (currentUser != null) {
                    if (currentUser.getSent() == null) {
                        currentUser.setSent(new ArrayList<>());
                    }
                    if (!currentUser.getSent().contains(userId)) {
                        currentUser.getSent().add(userId);
                    }

                    mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User otherUser = snapshot.getValue(User.class);
                            if (otherUser != null) {
                                if (otherUser.getReceived() == null) {
                                    otherUser.setReceived(new ArrayList<>());
                                }
                                otherUser.getReceived().remove(currentUserId);

                                mDatabase.child(currentUserId).setValue(currentUser)
                                        .addOnSuccessListener(aVoid -> {
                                            mDatabase.child(userId).setValue(otherUser)
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show();
                                                        receivedList.remove(userId);
                                                        notifyDataSetChanged();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(context, "Failed to update other user", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to update current user", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("RequestListAdapter", "Database error: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RequestListAdapter", "Database error: " + error.getMessage());
            }
        });
    }
}
