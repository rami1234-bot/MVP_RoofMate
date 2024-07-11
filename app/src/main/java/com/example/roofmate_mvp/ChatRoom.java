package com.example.roofmate_mvp;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private String id;
    private List<Message> mlist = new ArrayList<>();
    private Boolean isBlocked = false;

    public ChatRoom(String id, Boolean isBlocked) {
        this.id = id;
        this.isBlocked = isBlocked;
    }

    public ChatRoom() {
        // Default constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public void addMessage(Message message) {
        mlist.add(message);
        sendNotification(message);
    }

    private void sendNotification(Message message) {
        // Get the recipient's FCM token from the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(message.getSenderid());

        userRef.child("fcmToken").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String recipientToken = task.getResult().getValue(String.class);
                if (recipientToken != null) {
                    FirebaseMessaging fm = FirebaseMessaging.getInstance();
                    RemoteMessage.Builder messageBuilder = new RemoteMessage.Builder(recipientToken)
                            .setMessageId(Integer.toString(message.getContent().hashCode()))
                            .addData("message", message.getContent())
                            .addData("senderId", message.getSenderid())
                            .addData("timestamp", Long.toString(message.getDate()));

                    fm.send(messageBuilder.build());
                }
            } else {
                Log.w("ChatRoom", "Fetching FCM registration token failed", task.getException());
            }
        });
    }
}
