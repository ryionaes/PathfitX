package com.example.pathfitx;

import java.util.ArrayList;
import java.util.List;

public class WorkoutTemplates {

    public static List<Exercise> getPushDay() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(ExerciseDatabase.getExercise("Bench Press", 3, 8, 30));
        exercises.add(ExerciseDatabase.getExercise("Overhead Shoulder Press", 3, 10, 20));
        exercises.add(ExerciseDatabase.getExercise("Incline Dumbbell Press", 3, 10, 15));
        exercises.add(ExerciseDatabase.getExercise("Lateral Raises", 3, 12, 5));
        exercises.add(ExerciseDatabase.getExercise("Triceps Pushdowns", 3, 12, 15));
        return exercises;
    }

    public static List<Exercise> getPullDay() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(ExerciseDatabase.getExercise("Pull-ups/Lat Pulldowns", 3, 10, 0));
        exercises.add(ExerciseDatabase.getExercise("Bent-Over Rows", 3, 10, 30));
        exercises.add(ExerciseDatabase.getExercise("Face Pulls", 3, 15, 10));
        exercises.add(ExerciseDatabase.getExercise("Bicep Curls", 3, 12, 10));
        return exercises;
    }

    public static List<Exercise> getLegDay() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(ExerciseDatabase.getExercise("Barbell Squat", 3, 8, 50));
        exercises.add(ExerciseDatabase.getExercise("Deadlift", 3, 6, 60));
        exercises.add(ExerciseDatabase.getExercise("Romanian Deadlifts", 3, 10, 40));
        exercises.add(ExerciseDatabase.getExercise("Leg Extensions", 3, 15, 25));
        exercises.add(ExerciseDatabase.getExercise("Leg Curls", 3, 15, 25));
        exercises.add(ExerciseDatabase.getExercise("Standing Calf Raises", 3, 15, 20));
        return exercises;
    }

    public static List<Exercise> getUpperBody() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(ExerciseDatabase.getExercise("Dumbbell Lateral Raises", 4, 15, 5));
        exercises.add(ExerciseDatabase.getExercise("Dumbbell Front Raises", 3, 10, 5));
        exercises.add(ExerciseDatabase.getExercise("Bent-Over Reverse Flyes", 3, 15, 5));
        exercises.add(ExerciseDatabase.getExercise("Hammer Curls", 3, 10, 10));
        exercises.add(ExerciseDatabase.getExercise("Concentration Curls", 3, 12, 8));
        exercises.add(ExerciseDatabase.getExercise("Overhead Dumbbell Triceps Extension", 3, 10, 15));
        exercises.add(ExerciseDatabase.getExercise("Cable Rope Triceps Extension", 3, 15, 15));
        return exercises;
    }

    public static List<Exercise> getCardioCore() {
        List<Exercise> exercises = new ArrayList<>();
        Exercise cardio = ExerciseDatabase.getExercise("Cardio (Your choice)", 1, 30, 0);
        if (cardio != null) {
            cardio.setDetails("30-45 Minutes of Jogging, Cycling, etc.");
            exercises.add(cardio);
        }
        exercises.add(ExerciseDatabase.getExercise("Crunches", 3, 20, 0));
        exercises.add(ExerciseDatabase.getExercise("Russian Twists", 3, 20, 5));
        exercises.add(ExerciseDatabase.getExercise("Leg Raises", 3, 15, 0));
        Exercise plank = ExerciseDatabase.getExercise("Plank", 3, 60, 0);
        if (plank != null) {
            plank.setDetails("3 sets of 60 seconds");
            exercises.add(plank);
        }
        return exercises;
    }
}
