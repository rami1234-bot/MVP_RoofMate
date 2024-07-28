package com.example.roofmate_mvp;

import com.example.roofmate_mvp.Review;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable { // to enable passing user in intent

    private static final long serialVersionUID = 1L; // this line for serialization

    private String universityId;
    private String universityPicture; // URI of the picture
    private String universityName;

    private String username;
    private String email;
    private String password;
    private String userid;
    private int follower;
    private String profileImageUrl;
    private String phoneNumber; // Added phone number field
    private List<String> interests;
    private List<Review> reviews;
    private List<String> shared;
    private List<String> sent;
    private List<String> received;
    private String age;
    private String gender;
    private List<String> wishlist;
    private String fcmToken;
    private boolean avg = true; // New boolean variable

    // Getters and setters for new variable
    public boolean isAvg() {
        return avg;
    }

    public void setAvg(boolean avg) {
        this.avg = avg;
    }

    // Existing getters and setters
    public List<String> getSent() {
        return sent;
    }

    public void setSent(List<String> sent) {
        this.sent = sent;
    }

    public List<String> getReceived() {
        return received;
    }

    public void setReceived(List<String> received) {
        this.received = received;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    private String livingSituation;
    private List<String> favorites;



    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public void addFavorite(String userId) {
        if (favorites == null) {
            favorites = new ArrayList<>();
        }
        if (!favorites.contains(userId)) {
            favorites.add(userId);
        }
    }


    public String getLivingSituation() {
        return livingSituation;
    }

    public void setLivingSituation(String livingSituation) {
        this.livingSituation = livingSituation;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getShared() {
        return shared;
    }

    public void setShared(List<String> shared) {
        this.shared = shared;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    // Getter and Setter for universityPicture
    public String getUniversityPicture() {
        return universityPicture;
    }

    public void setUniversityPicture(String universityPicture) {
        this.universityPicture = universityPicture;
    }

    // Getter and Setter for universityName
    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public User() {
        // Default constructor
    }

    public List<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }
    private List<String> dislikes;


    public User(String username, String email, String password, String userId, String phoneNumber, String age, String gender, String livingSituation, String pushyToken) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userid = userId;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.livingSituation = livingSituation;
        this.fcmToken = pushyToken;
    }

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

    public void addToWishlist(String homeId) {
        if (wishlist == null) {
            wishlist = new ArrayList<>();
        }
        if (!wishlist.contains(homeId)) {
            wishlist.add(homeId);
        }
    }

    public void removeFromWishlist(String homeId) {
        if (wishlist != null && wishlist.contains(homeId)) {
            wishlist.remove(homeId);
        }
    }

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
        if (hashMap.containsKey("avg")) {
            this.avg = (Boolean) hashMap.get("avg");
        }
    }
}
