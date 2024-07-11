package com.example.roofmate_mvp;

public class Review {
    private String desk;
    private int star;
    private String uid;

    public Review(int star, String desk, String uid) {
        this.desk = desk;
        this.star = star;
        this.uid = uid;
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
