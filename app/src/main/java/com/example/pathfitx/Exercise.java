package com.example.pathfitx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Exercise implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Category {
        STRENGTH, CARDIO, HIIT, YOGA
    }

    public enum BodyPart {
        CHEST, BACK, LEGS, SHOULDERS, ARMS, CORE, FULL_BODY, VARIES
    }

    private String title;
    private String details;
    private int imageResId;
    private String tags; // e.g., "Hard", "Medium", "Easy"
    private Category category;
    private BodyPart bodyPart;
    private List<String> muscleTargets = new ArrayList<>();

    private int sets = 3;
    private int reps = 10;
    private int kg = 10;

    private boolean isAddedToWorkout = false;

    public Exercise() {}

    public Exercise(String title, String tags, int imageResId, Category category, BodyPart bodyPart, List<String> muscleTargets) {
        this.title = title;
        this.tags = tags;
        this.imageResId = imageResId;
        this.category = category;
        this.bodyPart = bodyPart;
        this.muscleTargets = muscleTargets != null ? muscleTargets : new ArrayList<>();
        updateDetails();
    }

    // Getters
    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public int getImageResId() { return imageResId; }
    public String getTags() { return tags; }
    public Category getCategory() { return category; }
    public BodyPart getBodyPart() { return bodyPart; }
    public List<String> getMuscleTargets() { return muscleTargets; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public int getKg() { return kg; }
    public boolean isAddedToWorkout() { return isAddedToWorkout; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDetails(String details) { this.details = details; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setTags(String tags) { this.tags = tags; }
    public void setCategory(Category category) { this.category = category; }
    public void setBodyPart(BodyPart bodyPart) { this.bodyPart = bodyPart; }
    public void setMuscleTargets(List<String> muscleTargets) { this.muscleTargets = muscleTargets; }
    public void setSets(int sets) { this.sets = Math.max(0, sets); updateDetails(); }
    public void setReps(int reps) { this.reps = Math.max(0, reps); updateDetails(); }
    public void setKg(int kg) { this.kg = Math.max(0, kg); updateDetails(); }
    public void setAddedToWorkout(boolean added) { isAddedToWorkout = added; updateDetails(); }

    private void updateDetails() {
        if (isAddedToWorkout) {
            this.details = sets + " Sets • " + reps + " Reps • " + kg + " kg";
        } else {
            this.details = tags + " • " + (muscleTargets != null ? muscleTargets.size() : 0) + " Muscle Targets";
        }
    }
}
