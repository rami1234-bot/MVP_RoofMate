package com.example.roofmate_mvp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter12 extends ArrayAdapter<String> {

    private Context context;
    private List<String> uids;
    private DatabaseReference mDatabase;

    public UserAdapter12(Context context, List<String> uids) {
        super(context, 0, uids);
        this.context = context;
        this.uids = uids;
        this.mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String userId = uids.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder();
            holder.textViewUsername = convertView.findViewById(R.id.usernameTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Fetch the username from the database using the user ID
        mDatabase.child(userId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.getValue(String.class);
                    if (username != null) {
                        holder.textViewUsername.setText(username);
                    } else {
                        holder.textViewUsername.setText("Unknown User");
                    }
                } else {
                    holder.textViewUsername.setText("Unknown User");
                }
                Log.d("UserAdapter12", "UserID: " + userId + " Username: " + holder.textViewUsername.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.textViewUsername.setText("Error");
                Log.e("UserAdapter12", "Database error: " + databaseError.getMessage());
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewUsername;
    }
}
