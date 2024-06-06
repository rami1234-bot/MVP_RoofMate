package com.example.roofmate_mvp;

public class Message {
    private String content;
    private String senderid;
    private long date;

    public Message(String content, String senderid) {
        this.content = content;
        this.senderid = senderid;
        this.date = System.currentTimeMillis(); // Current time in milliseconds
    }

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
