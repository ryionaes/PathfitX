package com.example.pathfitx;

import java.io.Serializable;

public class Exercise implements Serializable {
    private static final long serialVersionUID = 1L; // Add this for serialization

    private String title;
    private String details; // This will now be used for the display text
    private int imageResId;
    private String tags; // e.g., "Legs • Hard"

    private int sets = 3;
    private int reps = 10;
    private int kg = 10;

    private boolean isAddedToWorkout = false;

    // No-argument constructor required for Firestore
    public Exercise() {}

    public Exercise(String title, String tags, int imageResId) {
        this.title = title;
        this.tags = tags;
        this.imageResId = imageResId;
        updateDetails();
    }

    // Getters
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public int getImageResId() { return imageResId; }
    public String getTags() { return tags; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public int getKg() { return kg; }
    public boolean isAddedToWorkout() { return isAddedToWorkout; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDetails(String details) { this.details = details; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setTags(String tags) { this.tags = tags; }

    public void setSets(int sets) {
        this.sets = Math.max(0, sets);
        updateDetails();
    }

    public void setReps(int reps) {
        this.reps = Math.max(0, reps);
        updateDetails();
    }

    public void setKg(int kg) {
        this.kg = Math.max(0, kg);
        updateDetails();
    }

    public void setAddedToWorkout(boolean added) {
        isAddedToWorkout = added;
        updateDetails();
    }

    // Helper to update the details string
    private void updateDetails() {
        if (isAddedToWorkout) {
            this.details = sets + " Sets • " + reps + " Reps • " + kg + " kg";
        } else {
            this.details = this.tags;
        }
    }
}
