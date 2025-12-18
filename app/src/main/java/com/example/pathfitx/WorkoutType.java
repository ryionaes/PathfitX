package com.example.pathfitx;

import java.io.Serializable;

public class WorkoutType implements Serializable {
    private String name;
    private int imageResId;

    public WorkoutType(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
