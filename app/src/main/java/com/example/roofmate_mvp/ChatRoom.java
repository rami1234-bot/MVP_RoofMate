package com.example.roofmate_mvp;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    String id ;
    List<Message> mlist = new ArrayList<>();

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    Boolean isBlocked = false;
    public ChatRoom(String id){

        this.id = id;


    }
    public void AddMessage(Message m ){

        mlist.add(m);
    }


}
