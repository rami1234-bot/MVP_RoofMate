package com.example.roofmate_mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapt extends ArrayAdapter<Message> {
    private DatabaseReference userDatabaseReference;

    public MessageAdapt(Context context, List<Message> messages) {
        super(context, 0, messages);
        // Initialize Firebase Database reference for users
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
        }

        TextView messageTextView = convertView.findViewById(R.id.messageTextView);

        // Fetch the sender's name
        fetchSenderName(message.getSenderid(), messageTextView, message.getContent());

        // Set message bubble background based on sender ID
        if (isCurrentUser(message.getSenderid())) {
            messageTextView.setBackgroundResource(R.drawable.messagebubble);
        } else {
            messageTextView.setBackgroundResource(R.drawable.messagebubble1);
        }

        return convertView;
    }

    // Mock method to determine if the message is from the current user
    private boolean isCurrentUser(String senderId) {
        // Replace with actual logic to check if the sender is the current user
        // For example, compare senderId with FirebaseAuth.getInstance().getCurrentUser().getUid()
        return senderId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    // Method to fetch sender's name based on sender ID from Firebase
    private void fetchSenderName(String senderId, TextView messageTextView, String messageContent) {
        userDatabaseReference.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String senderName = snapshot.child("username").getValue(String.class);
                if (senderName == null) {
                    senderName = "Unknown";
                }
                messageTextView.setText(senderName + ": " + messageContent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageTextView.setText("Unknown: " + messageContent);
            }
        });
    }
}
