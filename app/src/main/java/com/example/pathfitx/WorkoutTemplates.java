package com.example.pathfitx;

import java.util.ArrayList;
import java.util.List;

public class WorkoutTemplates {

    private static Exercise createExercise(String title, int sets, int reps, int kg) {
        Exercise exercise = new Exercise(title, "", R.drawable.ic_launcher_background);
        exercise.setSets(sets);
        exercise.setReps(reps);
        exercise.setKg(kg);
        exercise.setAddedToWorkout(true);
        return exercise;
    }

    public static List<Exercise> getPushDay() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createExercise("Bench Press", 3, 8, 30));
        exercises.add(createExercise("Overhead Shoulder Press", 3, 10, 20));
        exercises.add(createExercise("Incline Dumbbell Press", 3, 10, 15));
        exercises.add(createExercise("Lateral Raises", 3, 12, 5));
        exercises.add(createExercise("Triceps Pushdowns", 3, 12, 15));
        return exercises;
    }

    public static List<Exercise> getPullDay() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createExercise("Deadlifts", 3, 6, 60));
        exercises.add(createExercise("Pull-ups/Lat Pulldowns", 3, 10, 0));
        exercises.add(createExercise("Bent-Over Rows", 3, 10, 30));
        exercises.add(createExercise("Face Pulls", 3, 15, 10));
        exercises.add(createExercise("Bicep Curls", 3, 12, 10));
        return exercises;
    }

    public static List<Exercise> getLegDay() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createExercise("Barbell Squats", 3, 8, 50));
        exercises.add(createExercise("Romanian Deadlifts", 3, 10, 40));
        exercises.add(createExercise("Leg Extensions", 3, 15, 25));
        exercises.add(createExercise("Leg Curls", 3, 15, 25));
        exercises.add(createExercise("Standing Calf Raises", 3, 15, 20));
        return exercises;
    }

    public static List<Exercise> getUpperBody() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createExercise("Dumbbell Lateral Raises", 4, 15, 5));
        exercises.add(createExercise("Dumbbell Front Raises", 3, 10, 5));
        exercises.add(createExercise("Bent-Over Reverse Flyes", 3, 15, 5));
        exercises.add(createExercise("Hammer Curls", 3, 10, 10));
        exercises.add(createExercise("Concentration Curls", 3, 12, 8));
        exercises.add(createExercise("Overhead Dumbbell Triceps Extension", 3, 10, 15));
        exercises.add(createExercise("Cable Rope Triceps Extension", 3, 15, 15));
        return exercises;
    }

    public static List<Exercise> getCardioCore() {
        List<Exercise> exercises = new ArrayList<>();
        Exercise cardio = createExercise("Cardio (Your Choice)", 1, 30, 0);
        cardio.setDetails("30-45 Minutes of Jogging, Cycling, etc.");
        exercises.add(cardio);
        exercises.add(createExercise("Crunches", 3, 20, 0));
        exercises.add(createExercise("Russian Twists", 3, 20, 5));
        exercises.add(createExercise("Leg Raises", 3, 15, 0));
        Exercise plank = createExercise("Plank", 3, 60, 0);
        plank.setDetails("3 sets of 60 seconds");
        exercises.add(plank);
        return exercises;
    }
}
