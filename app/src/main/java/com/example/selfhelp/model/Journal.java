package com.example.selfhelp.model;

import com.google.firebase.Timestamp;

public class Journal {
    private String username;
    private String title;
    private String thoughts;
    private Timestamp timeAdded;
    private String userId;
    private String imageUrl;

    public Journal(String username, String title, String thoughts, Timestamp timeAdded, String userId, String imageUrl) {
        this.username = username;
        this.title = title;
        this.thoughts = thoughts;
        this.timeAdded = timeAdded;
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public Journal() {
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
