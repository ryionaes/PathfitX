package com.example.pathfitx; // Use your actual package name

import java.util.ArrayList;
import java.util.List;

public class SelectedWorkoutRepository {
    // Static instance (The Singleton pattern)
    private static SelectedWorkoutRepository instance;

    // The actual list of selected exercises
    private List<Exercise> selectedExercises;

    // Private constructor so only this class can create itself
    private SelectedWorkoutRepository() {
        selectedExercises = new ArrayList<>();
    }

    // Update an existing exercise
    public void updateExercise(int position, Exercise updatedExercise) {
        if (position >= 0 && position < selectedExercises.size()) {
            selectedExercises.set(position, updatedExercise);
        }
    }

    // Remove an exercise
    public void removeExercise(Exercise exercise) {
        selectedExercises.remove(exercise);
    }

    // Public method to get the single instance
    public static synchronized SelectedWorkoutRepository getInstance() {
        if (instance == null) {
            instance = new SelectedWorkoutRepository();
        }
        return instance;
    }

    // Methods to add and get data
    public void addExercise(Exercise exercise) {
        // Optional: Check if already added to avoid duplicates
        selectedExercises.add(exercise);
    }

    public List<Exercise> getSelectedExercises() {
        return selectedExercises;
    }

    // Optional: Clear list for a new day
    public void clearList() {
        selectedExercises.clear();
    }
}