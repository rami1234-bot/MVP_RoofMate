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

        holder.sendRequestButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSendRequestClick(position);
            }
        });

        holder.dislikeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDislikeClick(position);
            }
        });

        // Handle add to favorites button click
        holder.addToFavoritesButton.setOnClickListener(v -> {
            addToFavorites(user.getUserid(), holder.itemView);
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
        Button addToFavoritesButton;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.usernameTextView);
            averageRating = itemView.findViewById(R.id.averageRatingTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            genderTextView = itemView.findViewById(R.id.genderTextView);
            interestsLinearLayout = itemView.findViewById(R.id.interestsLinearLayout);
            sendRequestButton = itemView.findViewById(R.id.sendrequest);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
            addToFavoritesButton = itemView.findViewById(R.id.addToFavoritesButton);
        }
    }

    private void addToFavorites(String userIdToAdd, View itemView) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null) {
            String currentUserId = currentFirebaseUser.getUid();

            // Get current user object
            mDatabase.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User currentUser = snapshot.getValue(User.class);

                    if (currentUser != null) {
                        // Initialize favorites list if null
                        if (currentUser.getFavorites() == null) {
                            currentUser.setFavorites(new ArrayList<>());
                        }

                        // Check if the user is already in the favorites list
                        if (currentUser.getFavorites().contains(userIdToAdd)) {
                            Toast.makeText(itemView.getContext(), "User already in favorites", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Check if the maximum number of favorites is reached
                        if (currentUser.getFavorites().size() >= 3) {
                            Toast.makeText(itemView.getContext(), "Maximum favorites reached", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Add to favorites
                        currentUser.getFavorites().add(userIdToAdd);

                        // Save updated user back to the database
                        mDatabase.child("users").child(currentUserId).setValue(currentUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(itemView.getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(itemView.getContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show();
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
}
