package com.example.pathfitx;

public class Exercise {
    String title;
    String details;
    // In a real app, this would be a URL string or resource ID
    int imageResId;
    private String tags; // e.g., "Legs • Hard" for the Explore screen

    // New fields for editable data
    private int sets = 3; // Default values
    private int reps = 10;
    private int kg = 10;

    private boolean isAddedToWorkout = false;

    public Exercise(String title, String details, int imageResId) {
        this.title = title;
        this.details = details;
        this.imageResId = imageResId;
    }

    // Getters
    public String getTitle() { return title; }
    public int getImageResId() { return imageResId; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public int getKg() { return kg; }

    // Smart getter for details string based on context
    public String getDetails() {
        if (isAddedToWorkout) {
            return sets + " Sets • " + reps + " Reps • " + kg + " kg";
        } else {
            return tags;
        }
    }

    // Setters
    public void setSets(int sets) { this.sets = Math.max(0, sets); }
    public void setReps(int reps) { this.reps = Math.max(0, reps); }
    public void setKg(int kg) { this.kg = Math.max(0, kg); }

    public void setAddedToWorkout(boolean added) {
        this.isAddedToWorkout = added;
    }
}
