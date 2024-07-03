package com.example.roofmate_mvp;

public class Review {

    public String getDesk() {
        return desk;
    }

    public void setDesk(String desk) {
        this.desk = desk;
    }

    String desk ;

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    int star;


    public Review(int s,String d){

        this.desk=d;
        this.star=s;
    }
}
