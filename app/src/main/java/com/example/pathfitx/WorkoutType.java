package com.example.pathfitx;

import java.io.Serializable;

public class WorkoutType implements Serializable {
    private String name;
    private String imageUrl;

    public WorkoutType(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
