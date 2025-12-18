package com.example.pathfitx;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkoutPlanDatabase {

    private static final Map<String, WorkoutPlan> allWorkoutPlans = new LinkedHashMap<>();

    static {
        populateWorkoutPlans();
    }

    private static void populateWorkoutPlans() {
        // 1. PUSH DAY (Chest, Shoulders, Triceps)
        List<Exercise> pushDay = new ArrayList<>();
        pushDay.add(ExerciseDatabase.getExercise("Barbell Bench Press", 3, 10, 40));
        pushDay.add(ExerciseDatabase.getExercise("Dumbbell Bench Press", 3, 12, 15));
        pushDay.add(ExerciseDatabase.getExercise("Seated Dumbbell Shoulder Press", 3, 10, 10));
        pushDay.add(ExerciseDatabase.getExercise("Tricep Rope Pushdowns", 3, 15, 12));
        pushDay.add(ExerciseDatabase.getExercise("Push-Ups", 3, 20, 0));
        allWorkoutPlans.put("Push Day", new WorkoutPlan("Push Day", pushDay));

        // 2. PULL DAY (Back, Biceps, Rear Delts)
        List<Exercise> pullDay = new ArrayList<>();
        pullDay.add(ExerciseDatabase.getExercise("Pull-Ups", 3, 8, 0));
        pullDay.add(ExerciseDatabase.getExercise("Deadlift", 3, 5, 60));
        pullDay.add(ExerciseDatabase.getExercise("Seated Cable Row", 3, 12, 30));
        pullDay.add(ExerciseDatabase.getExercise("Dumbbell Bicep Curl", 3, 12, 10));
        pullDay.add(ExerciseDatabase.getExercise("Face Pulls", 3, 15, 15));
        allWorkoutPlans.put("Pull Day", new WorkoutPlan("Pull Day", pullDay));

        // 3. LEG DAY (Quads, Hamstrings, Calves)
        List<Exercise> legDay = new ArrayList<>();
        legDay.add(ExerciseDatabase.getExercise("Barbell Back Squat", 3, 8, 50));
        legDay.add(ExerciseDatabase.getExercise("Leg Press", 3, 12, 80));
        legDay.add(ExerciseDatabase.getExercise("Romanian Deadlift", 3, 10, 40));
        legDay.add(ExerciseDatabase.getExercise("Leg Extensions", 3, 15, 20));
        legDay.add(ExerciseDatabase.getExercise("Standing Calf Raises", 4, 20, 10));
        // FIXED: Pinalitan ang 'new LegDay' ng 'new WorkoutPlan'
        allWorkoutPlans.put("Leg Day", new WorkoutPlan("Leg Day", legDay));

        // 4. CORE & CARDIO
        List<Exercise> coreCardio = new ArrayList<>();
        coreCardio.add(ExerciseDatabase.getExercise("Plank", 3, 60, 0));
        coreCardio.add(ExerciseDatabase.getExercise("Russian Twists", 3, 20, 5));
        coreCardio.add(ExerciseDatabase.getExercise("Hanging Leg Raises", 3, 12, 0));
        coreCardio.add(ExerciseDatabase.getExercise("Running", 1, 15, 0));
        coreCardio.add(ExerciseDatabase.getExercise("Mountain Climbers", 3, 30, 0));
        allWorkoutPlans.put("Cardio & Core", new WorkoutPlan("Cardio & Core", coreCardio));

        // 5. FULL BODY (Mix)
        List<Exercise> fullBody = new ArrayList<>();
        fullBody.add(ExerciseDatabase.getExercise("Push-Ups", 3, 15, 0));
        fullBody.add(ExerciseDatabase.getExercise("Squats", 3, 15, 0));
        fullBody.add(ExerciseDatabase.getExercise("Inverted Rows", 3, 10, 0));
        fullBody.add(ExerciseDatabase.getExercise("Arnold Press", 3, 10, 10));
        fullBody.add(ExerciseDatabase.getExercise("Mountain Climbers", 3, 10, 0));
        allWorkoutPlans.put("Full Body", new WorkoutPlan("Full Body", fullBody));

        // Custom Plan Placeholder
        allWorkoutPlans.put("Custom Plan", new WorkoutPlan("Custom Plan", new ArrayList<>()));
    }

    public static List<WorkoutPlan> getAllWorkoutPlans() {
        return new ArrayList<>(allWorkoutPlans.values());
    }

    public static WorkoutPlan getWorkoutPlan(String name) {
        return allWorkoutPlans.get(name);
    }
}