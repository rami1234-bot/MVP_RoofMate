package com.example.roofmate_mvp;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id ;
    List<Message> mlist = new ArrayList<>();

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    Boolean isBlocked = false;
    public ChatRoom(String id,Boolean isb){

        this.id = id;
        this.isBlocked = isb;

    }
    public ChatRoom(){


    }
    public void AddMessage(Message m ){

        mlist.add(m);
    }


}
