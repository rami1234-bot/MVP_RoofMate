package com.example.roofmate_mvp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable { // to enable passing user in intent

    private static final long serialVersionUID = 1L; // this line for serialization

    private String username;
    private String email;
    private String password;
    private String userid;
    private int follower;
    private String profileImageUrl;
    private String phoneNumber; // Added phone number field
    private List<String> interests;
    private List<Review> reviews;
    List<String> shared ;

    public List<String> getSent() {
        return sent;
    }

    public void setSent(List<String> sent) {
        this.sent = sent;
    }

    private List<String> sent;

    public List<String> getReceived() {
        return received;
    }

    public void setReceived(List<String> received) {
        this.received = received;
    }

    private List<String> received;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    private String age;
    private String gender;


    private List<String> wishlist;


    public List<String> getShared() {
        return shared;
    }

    public void setShared(List<String> shared) {
        this.shared = shared;
    }
    public void update() {
        if (sent != null && received != null) {
            if (shared == null) {
                shared = new ArrayList<>();
            } else {
                shared.clear(); // Clear existing shared list
            }

            // Iterate through sent and add to shared if also in received
            for (String sentItem : sent) {
                if (received.contains(sentItem) && !shared.contains(sentItem)) {
                    shared.add(sentItem);
                }
            }

            // Iterate through received and add to shared if also in sent
            for (String receivedItem : received) {
                if (sent.contains(receivedItem) && !shared.contains(receivedItem)) {
                    shared.add(receivedItem);
                }
            }
        }
    }



    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    private String fcmToken;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public User() {
//        this.userid = "";
//        this.username = "";
//        this.email = "";
//        this.password = "";
//        this.phoneNumber = "";
//        this.follower = 0;
//        this.interests = new ArrayList<>();
//        this.wishlist = new ArrayList<>();
//        this.reviews = new ArrayList<>();
//        this.sent = new ArrayList<>();
//        this.received = new ArrayList<>();
//        this.fcmToken =  fcmToken ;
    }

    public User(String userId, String username, String email, String password, String phoneNumber, String age1, String gender, String pushyToken) {
        this.userid = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.age = age1;
        this.gender = gender;
        this.fcmToken = pushyToken;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
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

    // Method to add a review
    public void addReview(Review review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        reviews.add(review);
    }


    public void addSent(String uid) {
        if (sent == null) {
            sent = new ArrayList<>();
        }
        sent.add(uid);
    }
    public void addReceived(String uid) {
        if (received == null) {
            received = new ArrayList<>();
        }
        received.add(uid);
    }

    // Method to get the average rating
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        int totalStars = 0;
        for (Review review : reviews) {
            totalStars += review.getStars();
        }
        return (double) totalStars / reviews.size();
    }

//    public List<String> getSharedSentAndReceived() {
//        List<String> sharedList = new ArrayList<>();
//
//        if (sent != null && received != null) {
//            // Convert received list to a HashMap for efficient lookup
//            Map<String, Boolean> receivedMap = new HashMap<>();
//            for (String receivedItem : received) {
//                receivedMap.put(receivedItem, true); // Using Boolean as value for key-value pair
//            }
//
//            // Check each sent item against receivedMap
//            for (String sentItem : sent) {
//                if (receivedMap.containsKey(sentItem) && !sharedList.contains(sentItem)) {
//                    sharedList.add(sentItem);
//                    Log.i(TAG, "Found shared item: " + sentItem);
//                }
//            }
//        }
//
//        return sharedList;
//    }
public void fromHashMap(HashMap<String, Object> hashMap) {
    if (hashMap.containsKey("userid")) {
        this.userid = (String) hashMap.get("userid");
    }
    if (hashMap.containsKey("username")) {
        this.username = (String) hashMap.get("username");
    }
    if (hashMap.containsKey("email")) {
        this.email = (String) hashMap.get("email");
    }
    if (hashMap.containsKey("password")) {
        this.password = (String) hashMap.get("password");
    }
    if (hashMap.containsKey("phoneNumber")) {
        this.phoneNumber = (String) hashMap.get("phoneNumber");
    }
    if (hashMap.containsKey("follower")) {
        this.follower = ((Long) hashMap.get("follower")).intValue();
    }
    if (hashMap.containsKey("profileImageUrl")) {
        this.profileImageUrl = (String) hashMap.get("profileImageUrl");
    }
    if (hashMap.containsKey("interests")) {
        this.interests = (List<String>) hashMap.get("interests");
    }
    if (hashMap.containsKey("reviews")) {
        // Assuming reviews are stored as a list of HashMaps, convert them to Review objects
        List<HashMap<String, Object>> reviewMaps = (List<HashMap<String, Object>>) hashMap.get("reviews");
        List<Review> reviewList = new ArrayList<>();
        for (HashMap<String, Object> reviewMap : reviewMaps) {
            Review review = new Review();
            review.fromHashMap(reviewMap);
            reviewList.add(review);
        }
        this.reviews = reviewList;
    }
    if (hashMap.containsKey("sent")) {
        this.sent = (List<String>) hashMap.get("sent");
    }
    if (hashMap.containsKey("received")) {
        this.received = (List<String>) hashMap.get("received");
    }
    if (hashMap.containsKey("wishlist")) {
        this.wishlist = (List<String>) hashMap.get("wishlist");
    }
    if (hashMap.containsKey("shared")) {
        this.shared = (List<String>) hashMap.get("shared");
    }
    if (hashMap.containsKey("fcmToken")) {
        this.fcmToken = (String) hashMap.get("fcmToken");
    }
    if (hashMap.containsKey("gender")) {
        this.gender = (String) hashMap.get("gender");
    }
}


}
