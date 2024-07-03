package com.example.roofmate_mvp;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class Home {
    private String id;
    private int rent;
    private String name;
    private List<Image> imageList = new ArrayList<>();
    private int rooms;
    private String disk = "";
    private String ownerid;
    private boolean available = true;
    private double latitude;
    private double longitude;

    // Constructors
    public Home() {
    }

    public Home(String idhome,String name, String description, int rent, int rooms, double latitude, double longitude, String ownerId) {
        this.name = name;
        this.disk = description;
        this.rent = rent;
        this.rooms = rooms;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ownerid = ownerId;
        this.id = idhome;
    }

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Function to add an image to the imageList
    public void addImage(Image image) {
        imageList.add(image);
    }
}
