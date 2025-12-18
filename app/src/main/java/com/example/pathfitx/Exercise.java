package com.example.pathfitx;

import java.io.Serializable;
import java.util.List;

public class Exercise implements Serializable {
    private String title;
    private String tags;
    private int imageResId; // Gagamitin natin ito para sa classmate's drawables [cite: 57, 60]
    private String imageUrl; // I-keep natin ito just in case (deprecated)
    private Category category;
    private BodyPart bodyPart;
    private List<String> muscleTargets;
    private double met;
    private String details;

    // Workout Progress State
    private int sets;
    private int reps;
    private int kg;
    private int completedSets = 0;
    private boolean isAddedToWorkout = false;

    public enum Category { STRENGTH, CARDIO, HIIT, YOGA }
    public enum BodyPart { CHEST, BACK, LEGS, SHOULDERS, ARMS, CORE, FULL_BODY }

    // Empty Constructor para sa Firebase parsing [cite: 192]
    public Exercise() {}

    // Main Constructor na gagamitin ng ExerciseDatabase natin kanina [cite: 57, 60]
    public Exercise(String title, String tags, int imageResId, Category category, BodyPart bodyPart, List<String> muscleTargets, double met) {
        this.title = title;
        this.tags = tags;
        this.imageResId = imageResId;
        this.category = category;
        this.bodyPart = bodyPart;
        this.muscleTargets = muscleTargets;
        this.met = met;
    }

    // Copy constructor to create a new instance from a template
    public Exercise(Exercise other) {
        this.title = other.title;
        this.tags = other.tags;
        this.imageResId = other.imageResId;
        this.imageUrl = other.imageUrl;
        this.category = other.category;
        this.bodyPart = other.bodyPart;
        this.muscleTargets = other.muscleTargets != null ? new java.util.ArrayList<>(other.muscleTargets) : null;
        this.met = other.met;
        this.details = other.details;
        this.sets = other.sets;
        this.reps = other.reps;
        this.kg = other.kg;
        this.completedSets = other.completedSets;
        this.isAddedToWorkout = other.isAddedToWorkout;
    }


    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTags() { return tags; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BodyPart getBodyPart() { return bodyPart; }
    public void setBodyPart(BodyPart bodyPart) { this.bodyPart = bodyPart; }

    public List<String> getMuscleTargets() { return muscleTargets; }
    public void setMuscleTargets(List<String> muscleTargets) { this.muscleTargets = muscleTargets; }

    public double getMet() { return met; }
    public void setMet(double met) { this.met = met; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public int getKg() { return kg; }
    public void setKg(int kg) { this.kg = kg; }

    public int getCompletedSets() { return completedSets; }
    public void setCompletedSets(int completedSets) { this.completedSets = completedSets; }

    public boolean isAddedToWorkout() { return isAddedToWorkout; }
    public void setAddedToWorkout(boolean addedToWorkout) { isAddedToWorkout = addedToWorkout; }

    public void setDetails(String details) {
        this.details = details;
    }

    // Helper method para sa UI [cite: 73]
    public String getDetails() {
        if (details != null && !details.isEmpty()) {
            return details;
        }
        if (muscleTargets != null) {
            return String.join(", ", muscleTargets);
        }
        return "";
    }
}
