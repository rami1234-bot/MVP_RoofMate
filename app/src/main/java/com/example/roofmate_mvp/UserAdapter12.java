package com.example.roofmate_mvp;

import android.content.Context;
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

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }

        TextView textViewUsername = convertView.findViewById(R.id.usernameTextView);

        // Fetch the username from the database using the user ID
        mDatabase.child(userId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.getValue(String.class);
                    textViewUsername.setText(username);
                } else {
                    textViewUsername.setText("Unknown User");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                textViewUsername.setText("Error");
            }
        });

        return convertView;
    }
}
