package com.example.roofmate_mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfilePagerAdapter extends RecyclerView.Adapter<ProfilePagerAdapter.ProfileViewHolder> {

    private List<User> userList;
    private OnProfileActionListener listener;

    public ProfilePagerAdapter(List<User> userList, OnProfileActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prof, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        User user = userList.get(position);
        holder.profileName.setText(user.getUsername());
        holder.averageRating.setText("Average Rating: " + user.getAverageRating());
        holder.ageTextView.setText("Age: " + user.getAge());
        holder.genderTextView.setText("Gender: " + user.getGender());

        List<String> interests = user.getInterests();
        holder.interestsLinearLayout.removeAllViews();
        for (String interest : interests) {
            TextView interestTextView = new TextView(holder.itemView.getContext());
            interestTextView.setText(interest);
            interestTextView.setPadding(8, 8, 8, 8);
            interestTextView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.interest_background));
            holder.interestsLinearLayout.addView(interestTextView);
        }

        // Handle send request button click
        holder.sendRequestButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSendRequestClick(position);
            }
        });

        // Handle dislike button click
        holder.dislikeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDislikeClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView profileName;
        TextView averageRating;
        TextView ageTextView;
        TextView genderTextView;
        LinearLayout interestsLinearLayout;
        Button sendRequestButton;
        Button dislikeButton;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.usernameTextView);
            averageRating = itemView.findViewById(R.id.averageRatingTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            genderTextView = itemView.findViewById(R.id.genderTextView);
            interestsLinearLayout = itemView.findViewById(R.id.interestsLinearLayout);
            sendRequestButton = itemView.findViewById(R.id.sendrequest);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
        }
    }

    private void addToLists(String userId1, String userId2) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get current user object
        mDatabase.child("users").child(userId1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);

                // Get other user object
                mDatabase.child("users").child(userId2).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User otherUser = snapshot.getValue(User.class);

                        if (currentUser != null && otherUser != null) {
                            // Initialize lists if null
                            if (currentUser.getSent() == null) {
                                currentUser.setSent(new ArrayList<>());
                            }
                            if (otherUser.getReceived() == null) {
                                otherUser.setReceived(new ArrayList<>());
                            }

                            // Update lists
                            currentUser.getSent().add(userId2);
                            otherUser.getReceived().add(userId1);

                            // Save updated users back to the database
                            mDatabase.child("users").child(userId1).setValue(currentUser)
                                    .addOnSuccessListener(aVoid -> {
                                        mDatabase.child("users").child(userId2).setValue(otherUser)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Successfully updated both users
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Failed to update other user
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed to update current user
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void addToDislikeList(String userId1, String userId2) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get current user object
        mDatabase.child("users").child(userId1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);

                if (currentUser != null) {
                    // Initialize dislike list if null
                    if (currentUser.getDislikes() == null) {
                        currentUser.setDislikes(new ArrayList<>());
                    }

                    // Update dislike list
                    currentUser.getDislikes().add(userId2);

                    // Save updated user back to the database
                    mDatabase.child("users").child(userId1).setValue(currentUser)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully updated the dislike list
                            })
                            .addOnFailureListener(e -> {
                                // Failed to update the dislike list
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
