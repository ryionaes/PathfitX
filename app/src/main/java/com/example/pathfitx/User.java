package com.example.pathfitx;

public class User {
    private String username;
    private String profileImageUri;
    private double weight;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String profileImageUri, double weight) {
        this.username = username;
        this.profileImageUri = profileImageUri;
        this.weight = weight;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
