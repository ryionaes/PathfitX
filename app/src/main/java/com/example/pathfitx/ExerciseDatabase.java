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
        addExercise("Bench Press", "Hard", "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Anterior Delts", "Triceps"), 5.0);
        addExercise("Incline Dumbbell Press", "Medium", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Anterior Delts"), 5.0);
        addExercise("Chest Flies (Dumbbell or Cable)", "Medium", "https://plus.unsplash.com/premium_photo-1664303499312-917c50e4047b?q=80&w=2071&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest (Pectorals)"), 3.0);

        // Strength - Back
        addExercise("Deadlift", "Hard", "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Hamstrings", "Glutes", "Lower Back", "Traps", "Core"), 8.0);
        addExercise("Pull Ups", "Medium", "https://images.unsplash.com/photo-1598971639058-211a74a96aea?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps", "Middle Back"), 8.0);
        addExercise("Pull-ups/Lat Pulldowns", "Medium", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps", "Mid Back"), 6.0);
        addExercise("Bent-Over Rows", "Medium", "https://images.unsplash.com/photo-1603287681836-e174ce718028?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Rhomboids", "Traps", "Lats", "Biceps"), 6.0);
        addExercise("Seated Row Machine", "Medium", "https://images.unsplash.com/photo-1590487988256-9ed24133863e?q=80&w=2028&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Middle Back", "Lats", "Biceps"), 4.0);

        // Strength - Legs
        addExercise("Barbell Squat", "Hard", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hamstrings"), 7.0);
        addExercise("Leg Press", "Medium", "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hamstrings"), 5.0);
        addExercise("Romanian Deadlifts", "Hard", "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Glutes", "Lower Back"), 8.0);
        addExercise("Leg Extensions", "Easy", "https://images.unsplash.com/photo-1574680096145-d05b474e2155?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quadriceps"), 3.0);
        addExercise("Leg Curls", "Easy", "https://images.unsplash.com/photo-1574680096145-d05b474e2155?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings"), 3.0);
        addExercise("Standing Calf Raises", "Easy", "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Gastrocnemius", "Soleus"), 3.0);
        addExercise("Hip Thrusts", "Hard", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Hamstrings"), 6.0);
        addExercise("Goblet Squats", "Medium", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes"), 5.0);

        // Strength - Shoulders
        addExercise("Overhead Shoulder Press", "Medium", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Delts", "Triceps"), 5.0);
        addExercise("Lateral Raises", "Easy", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Medial Delts"), 3.0);
        addExercise("Face Pulls", "Easy", "https://images.unsplash.com/photo-1598971639058-211a74a96aea?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Traps"), 3.0);
        addExercise("Dumbbell Front Raises", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Anterior Delts"), 3.0);
        addExercise("Bent-Over Reverse Flyes", "Easy", "https://images.unsplash.com/photo-1603287681836-e174ce718028?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Upper Back"), 3.0);

        // Strength - Arms
        addExercise("Bicep Curls", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Forearms"), 4.0);
        addExercise("Triceps Pushdowns", "Easy", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps"), 3.0);
        addExercise("Hammer Curls", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Brachialis", "Brachioradialis"), 4.0);
        addExercise("Concentration Curls", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps"), 3.0);
        addExercise("Overhead Dumbbell Triceps Extension", "Medium", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps (Long Head)"), 4.0);
        addExercise("Cable Rope Triceps Extension", "Easy", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps (Lateral/Medial)"), 3.0);

        // Strength - Core
        addExercise("Crunches", "Easy", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Rectus Abdominis"), 3.0);
        addExercise("Russian Twists", "Medium", "https://images.unsplash.com/photo-1518611012118-696072aa579a?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Abs"), 4.0);
        addExercise("Leg Raises", "Medium", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Hip Flexors"), 4.0);
        addExercise("Plank", "Medium", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Shoulders", "Glutes"), 3.0);

        // Cardio
        addExercise("Running", "Medium", "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?q=80&w=2070&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Legs", "Cardiovascular System"), 10.0);
        addExercise("Cycling", "Easy", "https://images.unsplash.com/photo-1517649763962-0c623066013b?q=80&w=2070&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Cardiovascular System"), 8.0);
        addExercise("Cardio (Your choice)", "Varies", "https://images.unsplash.com/photo-1538805060504-d1d52d153163?q=80&w=2070&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.VARIES, Arrays.asList("Cardiovascular System"), 7.0);
        addExercise("Jumping Rope", "Medium", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Cardiovascular"), 12.0);
        addExercise("Stair Climbing / Step Machine", "Hard", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Quads", "Cardiovascular"), 9.0);
        addExercise("Rowing Machine", "Hard", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Upper Back", "Core", "Cardiovascular"), 12.0);

        // HIIT
        addExercise("Burpees", "Hard", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Cardiovascular"), 12.0);
        addExercise("Mountain Climbers", "Medium", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.CORE, Arrays.asList("Core", "Quads", "Shoulders", "Cardiovascular"), 8.0);
        addExercise("Jump Squats", "Hard", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Cardiovascular"), 8.0);
        addExercise("High Knees", "Medium", "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?q=80&w=2070&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Core", "Cardiovascular"), 8.0);
        addExercise("Box Jumps", "Hard", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Hips", "Cardiovascular"), 8.0);

        // Yoga
        addExercise("Downward Dog", "Easy", "https://images.unsplash.com/photo-1544367563-12123d8959f9?q=80&w=2070&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Hamstrings", "Calves", "Shoulders", "Back"), 2.0);
        addExercise("Warrior Pose", "Easy", "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=1999&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hips", "Core"), 2.5);
        addExercise("Child’s Pose", "Easy", "https://images.unsplash.com/photo-1544367563-12123d8959f9?q=80&w=2070&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Back", "Hips"), 1.0);
        addExercise("Cobra Pose", "Easy", "https://images.unsplash.com/photo-1544367563-12123d8959f9?q=80&w=2070&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "Abdominal Stretch", "Chest Opening"), 1.5);
        addExercise("Tree Pose", "Medium", "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=1999&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Balance", "Glutes", "Core", "Ankles"), 2.5);
    }

    private static void addExercise(String title, String tags, String imageUrl, Exercise.Category category, Exercise.BodyPart bodyPart, List<String> muscleTargets, double met) {
        allExercises.put(title, new Exercise(title, tags, imageUrl, category, bodyPart, muscleTargets, met));
    }

    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(allExercises.values());
    }

    public static Exercise getExercise(String title, int sets, int reps, int kg) {
        Exercise template = allExercises.get(title);
        if (template != null) {
            Exercise newExercise = new Exercise(template.getTitle(), template.getTags(), template.getImageUrl(), template.getCategory(), template.getBodyPart(), new ArrayList<>(template.getMuscleTargets()), template.getMet());
            newExercise.setSets(sets);
            newExercise.setReps(reps);
            newExercise.setKg(kg);
            newExercise.setAddedToWorkout(true);
            return newExercise;
        }
        return null; // Or throw an exception
    }
    public static double calculateCalories(double weightInKg, double met, double durationInMinutes) {
        return met * 3.5 * weightInKg / 200 * durationInMinutes;
    }

}
