package com.example.pathfitx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkoutPlanDatabase {

    private static final Map<String, WorkoutPlan> allWorkoutPlans = new LinkedHashMap<>();

    static {
        // Push Day
        List<Exercise> pushDayExercises = new ArrayList<>();
        pushDayExercises.add(ExerciseDatabase.getExercise("Push-Ups", 3, 15, 0));
        pushDayExercises.add(ExerciseDatabase.getExercise("Barbell Bench Press", 3, 8, 50));
        pushDayExercises.add(ExerciseDatabase.getExercise("Incline Dumbbell Press", 3, 10, 15));
        pushDayExercises.add(ExerciseDatabase.getExercise("Overhead Barbell Press", 3, 8, 30));
        pushDayExercises.add(ExerciseDatabase.getExercise("Dumbbell Lateral Raises", 3, 12, 5));
        allWorkoutPlans.put("Push Day", new WorkoutPlan("Push Day", pushDayExercises));

        // Pull Day
        List<Exercise> pullDayExercises = new ArrayList<>();
        pullDayExercises.add(ExerciseDatabase.getExercise("Pull-Ups", 3, 5, 0));
        pullDayExercises.add(ExerciseDatabase.getExercise("Deadlift", 3, 5, 80));
        pullDayExercises.add(ExerciseDatabase.getExercise("Barbell Bent-Over Rows", 3, 8, 40));
        pullDayExercises.add(ExerciseDatabase.getExercise("Lat Pulldowns", 3, 10, 40));
        pullDayExercises.add(ExerciseDatabase.getExercise("Barbell Bicep Curl", 3, 10, 15));
        allWorkoutPlans.put("Pull Day", new WorkoutPlan("Pull Day", pullDayExercises));

        // Leg Day
        List<Exercise> legDayExercises = new ArrayList<>();
        legDayExercises.add(ExerciseDatabase.getExercise("Squats", 3, 10, 60));
        legDayExercises.add(ExerciseDatabase.getExercise("Romanian Deadlift", 3, 10, 50));
        legDayExercises.add(ExerciseDatabase.getExercise("Leg Press", 3, 12, 100));
        legDayExercises.add(ExerciseDatabase.getExercise("Walking Lunges", 3, 12, 10));
        legDayExercises.add(ExerciseDatabase.getExercise("Standing Calf Raises", 3, 15, 20));
        allWorkoutPlans.put("Leg Day", new WorkoutPlan("Leg Day", legDayExercises));

        // Cardio
        List<Exercise> cardioExercises = new ArrayList<>();
        cardioExercises.add(ExerciseDatabase.getExercise("Running", 1, 30, 0));
        cardioExercises.add(ExerciseDatabase.getExercise("Jump Rope", 3, 5, 0));
        cardioExercises.add(ExerciseDatabase.getExercise("Burpees", 3, 10, 0));
        cardioExercises.add(ExerciseDatabase.getExercise("Cycling", 1, 30, 0));
        cardioExercises.add(ExerciseDatabase.getExercise("Rowing Machine", 1, 15, 0));
        allWorkoutPlans.put("Cardio", new WorkoutPlan("Cardio", cardioExercises));

        // Core
        List<Exercise> coreExercises = new ArrayList<>();
        coreExercises.add(ExerciseDatabase.getExercise("Plank", 3, 60, 0));
        coreExercises.add(ExerciseDatabase.getExercise("Crunches", 3, 20, 0));
        coreExercises.add(ExerciseDatabase.getExercise("Russian Twists", 3, 15, 0));
        coreExercises.add(ExerciseDatabase.getExercise("Hanging Leg Raises", 3, 12, 0));
        coreExercises.add(ExerciseDatabase.getExercise("Bicycle Crunches", 3, 20, 0));
        allWorkoutPlans.put("Core", new WorkoutPlan("Core", coreExercises));

        // Custom Plan
        allWorkoutPlans.put("Custom Plan", new WorkoutPlan("Custom Plan", new ArrayList<>()));
    }

    public static List<WorkoutPlan> getAllWorkoutPlans() {
        return new ArrayList<>(allWorkoutPlans.values());
    }

    public static WorkoutPlan getWorkoutPlan(String name) {
        return allWorkoutPlans.get(name);
    }
}
