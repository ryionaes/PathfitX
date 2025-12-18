package com.example.pathfitx;

import com.google.firebase.firestore.Exclude;
import java.util.Date;

public class WorkoutHistory {
    private String documentId;
    private String workoutName;
    private int durationSeconds;
    private int totalVolume;
    private int completionRate;
    private Date timestamp;
    private int exercisesCount;
    private double caloriesBurned;

    public WorkoutHistory() {} // Required for Firestore

    public WorkoutHistory(String workoutName, int durationSeconds, int totalVolume, int completionRate, Date timestamp, int exercisesCount, double caloriesBurned) {
        this.workoutName = workoutName;
        this.durationSeconds = durationSeconds;
        this.totalVolume = totalVolume;
        this.completionRate = completionRate;
        this.timestamp = timestamp;
        this.exercisesCount = exercisesCount;
        this.caloriesBurned = caloriesBurned;
    }

    @Exclude
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getWorkoutName() { return workoutName; }
    public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }

    public int getDurationSeconds() { return durationSeconds; }
    public int getTotalVolume() { return totalVolume; }
    public int getCompletionRate() { return completionRate; }
    public Date getTimestamp() { return timestamp; }
    public int getExercisesCount() { return exercisesCount; }
    public double getCaloriesBurned() { return caloriesBurned; }
}
