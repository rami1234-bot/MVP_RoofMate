package com.example.roofmate_mvp;

import java.io.Serializable;
import java.util.HashMap;

public class Review implements Serializable {


    private String desk;
    private int star;
    private String uid;

    public Review(int star, String desk, String uid) {
        this.desk = desk;
        this.star = star;
        this.uid = uid;
    }


    public void fromHashMap(HashMap<String, Object> hashMap) {
        if (hashMap.containsKey("desk")) {
            this.desk = (String) hashMap.get("desk");
        }
        if (hashMap.containsKey("star")) {
            this.star = ((Long) hashMap.get("star")).intValue();
        }
        if (hashMap.containsKey("uid")) {
            this.uid = (String) hashMap.get("uid");
        }
    }

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public String getText() {
        return desk;
    }

    public void setText(String desk) {
        this.desk = desk;
    }

    public int getStars() {
        return star;
    }

    public void setStars(int star) {
        this.star = star;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
