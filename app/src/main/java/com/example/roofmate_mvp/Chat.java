package com.example.roofmate_mvp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat extends BaseActivity {
    private ListView messagesListView;
    private EditText messageEditText;
    private Button sendMessageButton;
    private String chatRoomId;
    private DatabaseReference mDatabase;
    private List<Message> messagesList;
    private MessageAdapt adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesListView = findViewById(R.id.messagesListView);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        // Retrieve chat room ID and username from intent extras
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        String username = getIntent().getStringExtra("oth");

        // Set the toolbar title to the username
        getSupportActionBar().setTitle(username);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomId).child("messages");

        messagesList = new ArrayList<>();
        adapter = new MessageAdapt(this, messagesList);
        messagesListView.setAdapter(adapter);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchMessages();
    }

    private void fetchMessages() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messagesList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Chat.this, "Failed to load messages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(messageText, FirebaseAuth.getInstance().getCurrentUser().getUid());
            mDatabase.push().setValue(message)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            messageEditText.setText("");
                        } else {
                            Toast.makeText(Chat.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
