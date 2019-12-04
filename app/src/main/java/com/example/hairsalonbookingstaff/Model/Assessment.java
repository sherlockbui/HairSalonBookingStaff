package com.example.hairsalonbookingstaff.Model;

public class Assessment {
    String date, commend, rating, time;

    public Assessment(String date, String commend, String rating, String time) {
        this.date = date;
        this.commend = commend;
        this.rating = rating;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
