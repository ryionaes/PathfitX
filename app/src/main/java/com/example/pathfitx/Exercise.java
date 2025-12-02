package com.example.pathfitx;

public class Exercise {
    String title;
    String details;
    // In a real app, this would be a URL string or resource ID
    int imageResId;

    public Exercise(String title, String details, int imageResId) {
        this.title = title;
        this.details = details;
        this.imageResId = imageResId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public int getImageResId() { return imageResId; }
}
