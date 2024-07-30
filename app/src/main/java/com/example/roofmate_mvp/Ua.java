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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Ua extends ArrayAdapter<String> {

    private Context context;
    private List<String> uids;
    private DatabaseReference mDatabase;

    public Ua(Context context, List<String> uids) {
        super(context, 0, uids);
        this.context = context;
        this.uids = uids;
        this.mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    public Ua(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String userId = uids.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user1, parent, false);
            holder = new ViewHolder();
            holder.textViewUsername = convertView.findViewById(R.id.usernameTextView);
            holder.textViewEmail = convertView.findViewById(R.id.emailTextView);
            holder.removeButton = convertView.findViewById(R.id.removeButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Fetch the username and email from the database using the user ID
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    if (username != null) {
                        holder.textViewUsername.setText(username);
                    } else {
                        holder.textViewUsername.setText("Unknown User");
                    }
                    if (email != null) {
                        holder.textViewEmail.setText(email);
                    } else {
                        holder.textViewEmail.setText("No Email");
                    }
                } else {
                    holder.textViewUsername.setText("Unknown User");
                    holder.textViewEmail.setText("No Email");
                }
                Log.d("UserAdapter12", "UserID: " + userId + " Username: " + holder.textViewUsername.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.textViewUsername.setText("Error");
                holder.textViewEmail.setText("Error");
                Log.e("UserAdapter12", "Database error: " + databaseError.getMessage());
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            removeFavorite(userId, position);
        });

        return convertView;
    }

    private void removeFavorite(String userIdToRemove, int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference userFavoritesRef = mDatabase.child(auth.getCurrentUser().getUid()).child("favorites");

        userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot favoriteSnapshot : dataSnapshot.getChildren()) {
                        String favoriteUserId = favoriteSnapshot.getValue(String.class);
                        if (favoriteUserId != null && favoriteUserId.equals(userIdToRemove)) {
                            favoriteSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                        uids.remove(position);
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to remove favorite", Toast.LENGTH_SHORT).show();
                                    });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserAdapter12", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private static class ViewHolder {
        TextView textViewUsername;
        TextView textViewEmail;
        Button removeButton;
    }
}
