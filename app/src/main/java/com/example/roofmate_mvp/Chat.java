package com.example.roofmate_mvp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {
    private ListView messagesListView;
    private EditText messageEditText;
    private Button sendMessageButton;
    private String chatRoomId;
    private String otherId;
    private String username;
    private DatabaseReference mDatabase;
    private List<Message> messagesList;
    private MessageAdapt adapter;
    private FirebaseAuth mAuth;
    private String fcm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        messagesListView = findViewById(R.id.listView);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        // Retrieve intent extras
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        otherId = getIntent().getStringExtra("otheruserid");
        fcm = getIntent().getStringExtra("fcm");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomId).child("messages");

        messagesList = new ArrayList<>();
        adapter = new MessageAdapt(this, messagesList);
        messagesListView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        sendMessageButton.setOnClickListener(v -> sendMessage());

        fetchMessages();
        fetchUsername();
    }

    private void fetchMessages() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messagesList.add(message);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chat.this, "Failed to load messages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUsername() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        username = user.getUsername();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Chat.this, "Failed to load username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(messageText, mAuth.getCurrentUser().getUid());
            mDatabase.push().setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    messageEditText.setText("");
                    sendNotification(message, fcm);
                } else {
                    Toast.makeText(Chat.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void sendNotification(Message message, String fcmToken) {

        if(!fcm.equals("")||fcm==null){


        List<String> deviceTokens = new ArrayList<>();
        deviceTokens.add(fcmToken);

        String[] to = deviceTokens.toArray(new String[0]);

        Map<String, String> payload = new HashMap<>();
        payload.put("message", username + ": " + message.getContent());
        payload.put("color", "FF5733");

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "username");
        notification.put("body", message.getContent());
        notification.put("icon", "loop"); // Customize the icon
        notification.put("badge", 1);
        notification.put("sound", "default");
        notification.put("priority", "high");
        notification.put("color", "#FF5733");

        PushyAPI.PushyPushRequest push = new PushyAPI.PushyPushRequest(payload, to, notification);

        try {
            PushyAPI.sendPush(push);
            runOnUiThread(() -> Toast.makeText(Chat.this, "Notification sent successfully", Toast.LENGTH_SHORT).show());
        } catch (Exception exc) {
            runOnUiThread(() -> Toast.makeText(Chat.this, "Failed to send notification: " + exc.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }}

}
