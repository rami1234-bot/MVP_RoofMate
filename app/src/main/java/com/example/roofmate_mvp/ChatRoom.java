package com.example.roofmate_mvp;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    String id ;
    List<Message> mlist = new ArrayList<>();
    public ChatRoom(String id){

        this.id = id;


    }
    public void AddMessage(Message m ){

        mlist.add(m);
    }


}
