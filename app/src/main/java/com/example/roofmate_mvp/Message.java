package com.example.roofmate_mvp;

import java.sql.Timestamp;

public class Message {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String content ;

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    String senderid;
    Timestamp date ;
    public Message(String s, String c){
        content = c;
        senderid = s;


    }



}
