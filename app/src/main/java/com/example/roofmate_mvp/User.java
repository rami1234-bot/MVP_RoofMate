package com.example.roofmate_mvp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable { // to enable passing user in intent

    private static final long serialVersionUID = 1L; //  this line for serialization

    public String username;
    public String email;
    public String password;
    public String userid;
    public int follower = 0;
    public String profileImageUrl;
    private List<String> interests;
    private List<String> wishlist;

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userid, String username, String email, String password) {
        this.userid = userid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.follower = 0;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


    public List<String> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<String> wishlist) {
        this.wishlist = wishlist;
    }

    // Method to add a home to the wishlist
    public void addToWishlist(String homeId) {
        if (wishlist == null) {
            wishlist = new ArrayList<>();
        }
        if (!wishlist.contains(homeId)) {
            wishlist.add(homeId);
        }
    }

    // Method to remove a home from the wishlist
    public void removeFromWishlist(String homeId) {
        if (wishlist != null && wishlist.contains(homeId)) {
            wishlist.remove(homeId);
        }
    }
}
