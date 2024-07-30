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


    private int minAge;
    private int maxAge;
    private List<String> filterLocations;
    private List<String> filterGenders;
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

    private String smoking; // New field
    private List<String> religion; // New field
    private String allergies; // New field
    private String pets; // New field

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    private String livingSituation="No apartment";
    private List<String> favorites;



    public List<String> getFavorites() {
        return favorites;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public List<String> getReligion() {
        return religion;
    }

    public void setReligion(List<String> religion) {
        this.religion = religion;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getPets() {
        return pets;
    }

    public void setPets(String pets) {
        this.pets = pets;
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
        this.minAge = 18;
        this.maxAge = 99;
        this.filterLocations = new ArrayList<>();
        this.filterGenders = new ArrayList<>();
    }

    // New constructor with six parameters
    public User(String userId, String username, String email, String password, String phoneNumber, String pushyToken) {
        this.userid = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.fcmToken = pushyToken;
        this.follower = 0;
        this.interests = new ArrayList<>();
        this.wishlist = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.sent = new ArrayList<>();
        this.received = new ArrayList<>();
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
        this.userid = (String) hashMap.getOrDefault("userid", userid);
        this.username = (String) hashMap.getOrDefault("username", username);
        this.email = (String) hashMap.getOrDefault("email", email);
        this.password = (String) hashMap.getOrDefault("password", password);
        this.phoneNumber = (String) hashMap.getOrDefault("phoneNumber", phoneNumber);
        this.follower = ((Long) hashMap.getOrDefault("follower", (long) follower)).intValue();
        this.profileImageUrl = (String) hashMap.getOrDefault("profileImageUrl", profileImageUrl);
        this.interests = (List<String>) hashMap.getOrDefault("interests", interests);

        List<HashMap<String, Object>> reviewMaps = (List<HashMap<String, Object>>) hashMap.getOrDefault("reviews", new ArrayList<>());
        List<Review> reviewList = new ArrayList<>();
        for (HashMap<String, Object> reviewMap : reviewMaps) {
            Review review = new Review();
            review.fromHashMap(reviewMap);
            reviewList.add(review);
        }
        this.reviews = reviewList;

        this.sent = (List<String>) hashMap.getOrDefault("sent", sent);
        this.received = (List<String>) hashMap.getOrDefault("received", received);
        this.wishlist = (List<String>) hashMap.getOrDefault("wishlist", wishlist);
        this.shared = (List<String>) hashMap.getOrDefault("shared", shared);
        this.fcmToken = (String) hashMap.getOrDefault("fcmToken", fcmToken);
        this.gender = (String) hashMap.getOrDefault("gender", gender);
        this.avg = (Boolean) hashMap.getOrDefault("avg", avg);
    }

    public void updateShared() {
        if (sent != null && received != null) {
            if (shared == null) {
                shared = new ArrayList<>();
            } else {
                shared.clear(); // Clear existing shared list
            }

            for (String sentItem : sent) {
                if (received.contains(sentItem) && !shared.contains(sentItem)) {
                    shared.add(sentItem);
                }
            }

            for (String receivedItem : received) {
                if (sent.contains(receivedItem) && !shared.contains(receivedItem)) {
                    shared.add(receivedItem);
                }
            }
        }
    }
}
