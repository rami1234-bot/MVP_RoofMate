package com.example.roofmate_mvp;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class Home {
    int rent;
    String name;
    List<Image> imageList = new ArrayList<>();

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    int rooms ;

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    String disk = "";
    String ownerid;
    boolean available= true;

    // Constructors
    public Home() {
    }

    public Home(int rent, String name, String ownerid,String disk ,int rooms ) {
        this.rent = rent;
        this.name = name;
        this.ownerid = ownerid;
        this.disk = disk;
        this.rooms = rooms;
    }

    // Getter and Setter methods
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

    // Function to add an image to the imageList
    public void addImage(Image image) {
        imageList.add(image);
    }
}

