package com.example.roofmate_mvp;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

public class Home {
    int rent;
    String name;
    List<Image> imageList = new ArrayList<>();
    String ownerid;

    // Constructors
    public Home() {
    }

    public Home(int rent, String name, String ownerid) {
        this.rent = rent;
        this.name = name;
        this.ownerid = ownerid;
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

