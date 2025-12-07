package com.example.pathfitx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseDatabase {

    private static final Map<String, Exercise> allExercises = new HashMap<>();

    static {
        // Strength - Chest
        addExercise("Bench Press", "Hard", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Anterior Delts", "Triceps"));
        addExercise("Incline Dumbbell Press", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Anterior Delts"));
        addExercise("Chest Flies (Dumbbell or Cable)", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest (Pectorals)"));

        // Strength - Back
        addExercise("Deadlift", "Hard", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Hamstrings", "Glutes", "Lower Back", "Traps", "Core"));
        addExercise("Pull Ups", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps", "Middle Back"));
        addExercise("Pull-ups/Lat Pulldowns", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps", "Mid Back"));
        addExercise("Bent-Over Rows", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Rhomboids", "Traps", "Lats", "Biceps"));
        addExercise("Seated Row Machine", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Middle Back", "Lats", "Biceps"));

        // Strength - Legs
        addExercise("Barbell Squat", "Hard", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hamstrings"));
        addExercise("Leg Press", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hamstrings"));
        addExercise("Romanian Deadlifts", "Hard", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Glutes", "Lower Back"));
        addExercise("Leg Extensions", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quadriceps"));
        addExercise("Leg Curls", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings"));
        addExercise("Standing Calf Raises", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Gastrocnemius", "Soleus"));
        addExercise("Hip Thrusts", "Hard", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Hamstrings"));
        addExercise("Goblet Squats", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes"));

        // Strength - Shoulders
        addExercise("Overhead Shoulder Press", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Delts", "Triceps"));
        addExercise("Lateral Raises", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Medial Delts"));
        addExercise("Face Pulls", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Traps"));
        addExercise("Dumbbell Front Raises", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Anterior Delts"));
        addExercise("Bent-Over Reverse Flyes", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Upper Back"));

        // Strength - Arms
        addExercise("Bicep Curls", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Forearms"));
        addExercise("Triceps Pushdowns", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps"));
        addExercise("Hammer Curls", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Brachialis", "Brachioradialis"));
        addExercise("Concentration Curls", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps"));
        addExercise("Overhead Dumbbell Triceps Extension", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps (Long Head)"));
        addExercise("Cable Rope Triceps Extension", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps (Lateral/Medial)"));

        // Strength - Core
        addExercise("Crunches", "Easy", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Rectus Abdominis"));
        addExercise("Russian Twists", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Abs"));
        addExercise("Leg Raises", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Hip Flexors"));
        addExercise("Plank", "Medium", R.drawable.ic_launcher_background, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Shoulders", "Glutes"));

        // Cardio
        addExercise("Running", "Medium", R.drawable.ic_launcher_background, Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Legs", "Cardiovascular System"));
        addExercise("Cycling", "Easy", R.drawable.ic_launcher_background, Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Cardiovascular System"));
        addExercise("Cardio (Your choice)", "Varies", R.drawable.ic_launcher_background, Exercise.Category.CARDIO, Exercise.BodyPart.VARIES, Arrays.asList("Cardiovascular System"));
        addExercise("Jumping Rope", "Medium", R.drawable.ic_launcher_background, Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Cardiovascular"));
        addExercise("Stair Climbing / Step Machine", "Hard", R.drawable.ic_launcher_background, Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Quads", "Cardiovascular"));
        addExercise("Rowing Machine", "Hard", R.drawable.ic_launcher_background, Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Upper Back", "Core", "Cardiovascular"));

        // HIIT
        addExercise("Burpees", "Hard", R.drawable.ic_launcher_background, Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Cardiovascular"));
        addExercise("Mountain Climbers", "Medium", R.drawable.ic_launcher_background, Exercise.Category.HIIT, Exercise.BodyPart.CORE, Arrays.asList("Core", "Quads", "Shoulders", "Cardiovascular"));
        addExercise("Jump Squats", "Hard", R.drawable.ic_launcher_background, Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Cardiovascular"));
        addExercise("High Knees", "Medium", R.drawable.ic_launcher_background, Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Core", "Cardiovascular"));
        addExercise("Box Jumps", "Hard", R.drawable.ic_launcher_background, Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Hips", "Cardiovascular"));

        // Yoga
        addExercise("Downward Dog", "Easy", R.drawable.ic_launcher_background, Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Hamstrings", "Calves", "Shoulders", "Back"));
        addExercise("Warrior Pose", "Easy", R.drawable.ic_launcher_background, Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hips", "Core"));
        addExercise("Child’s Pose", "Easy", R.drawable.ic_launcher_background, Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Back", "Hips"));
        addExercise("Cobra Pose", "Easy", R.drawable.ic_launcher_background, Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "Abdominal Stretch", "Chest Opening"));
        addExercise("Tree Pose", "Medium", R.drawable.ic_launcher_background, Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Balance", "Glutes", "Core", "Ankles"));
    }

    private static void addExercise(String title, String tags, int imageResId, Exercise.Category category, Exercise.BodyPart bodyPart, List<String> muscleTargets) {
        allExercises.put(title, new Exercise(title, tags, imageResId, category, bodyPart, muscleTargets));
    }

    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(allExercises.values());
    }

    public static Exercise getExercise(String title, int sets, int reps, int kg) {
        Exercise template = allExercises.get(title);
        if (template != null) {
            Exercise newExercise = new Exercise(template.getTitle(), template.getTags(), template.getImageResId(), template.getCategory(), template.getBodyPart(), new ArrayList<>(template.getMuscleTargets()));
            newExercise.setSets(sets);
            newExercise.setReps(reps);
            newExercise.setKg(kg);
            newExercise.setAddedToWorkout(true);
            return newExercise;
        }
        return null; // Or throw an exception
    }
}
