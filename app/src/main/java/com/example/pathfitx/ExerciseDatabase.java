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
        addExercise("Bench Press", "Hard", "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Anterior Delts", "Triceps"));
        addExercise("Incline Dumbbell Press", "Medium", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Anterior Delts"));
        addExercise("Chest Flies (Dumbbell or Cable)", "Medium", "https://plus.unsplash.com/premium_photo-1664303499312-917c50e4047b?q=80&w=2071&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest (Pectorals)"));

        // Strength - Back
        addExercise("Deadlift", "Hard", "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Hamstrings", "Glutes", "Lower Back", "Traps", "Core"));
        addExercise("Pull Ups", "Medium", "https://images.unsplash.com/photo-1598971639058-211a74a96aea?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps", "Middle Back"));
        addExercise("Pull-ups/Lat Pulldowns", "Medium", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps", "Mid Back"));
        addExercise("Bent-Over Rows", "Medium", "https://images.unsplash.com/photo-1603287681836-e174ce718028?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Rhomboids", "Traps", "Lats", "Biceps"));
        addExercise("Seated Row Machine", "Medium", "https://images.unsplash.com/photo-1590487988256-9ed24133863e?q=80&w=2028&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Middle Back", "Lats", "Biceps"));

        // Strength - Legs
        addExercise("Barbell Squat", "Hard", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hamstrings"));
        addExercise("Leg Press", "Medium", "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hamstrings"));
        addExercise("Romanian Deadlifts", "Hard", "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Glutes", "Lower Back"));
        addExercise("Leg Extensions", "Easy", "https://images.unsplash.com/photo-1574680096145-d05b474e2155?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quadriceps"));
        addExercise("Leg Curls", "Easy", "https://images.unsplash.com/photo-1574680096145-d05b474e2155?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings"));
        addExercise("Standing Calf Raises", "Easy", "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Gastrocnemius", "Soleus"));
        addExercise("Hip Thrusts", "Hard", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Hamstrings"));
        addExercise("Goblet Squats", "Medium", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes"));

        // Strength - Shoulders
        addExercise("Overhead Shoulder Press", "Medium", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Delts", "Triceps"));
        addExercise("Lateral Raises", "Easy", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Medial Delts"));
        addExercise("Face Pulls", "Easy", "https://images.unsplash.com/photo-1598971639058-211a74a96aea?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Traps"));
        addExercise("Dumbbell Front Raises", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Anterior Delts"));
        addExercise("Bent-Over Reverse Flyes", "Easy", "https://images.unsplash.com/photo-1603287681836-e174ce718028?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Upper Back"));

        // Strength - Arms
        addExercise("Bicep Curls", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Forearms"));
        addExercise("Triceps Pushdowns", "Easy", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps"));
        addExercise("Hammer Curls", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Brachialis", "Brachioradialis"));
        addExercise("Concentration Curls", "Easy", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps"));
        addExercise("Overhead Dumbbell Triceps Extension", "Medium", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps (Long Head)"));
        addExercise("Cable Rope Triceps Extension", "Easy", "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps (Lateral/Medial)"));

        // Strength - Core
        addExercise("Crunches", "Easy", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Rectus Abdominis"));
        addExercise("Russian Twists", "Medium", "https://images.unsplash.com/photo-1518611012118-696072aa579a?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Abs"));
        addExercise("Leg Raises", "Medium", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Hip Flexors"));
        addExercise("Plank", "Medium", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Shoulders", "Glutes"));

        // Cardio
        addExercise("Running", "Medium", "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?q=80&w=2070&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Legs", "Cardiovascular System"));
        addExercise("Cycling", "Easy", "https://images.unsplash.com/photo-1517649763962-0c623066013b?q=80&w=2070&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Cardiovascular System"));
        addExercise("Cardio (Your choice)", "Varies", "https://images.unsplash.com/photo-1538805060504-d1d52d153163?q=80&w=2070&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.VARIES, Arrays.asList("Cardiovascular System"));
        addExercise("Jumping Rope", "Medium", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Cardiovascular"));
        addExercise("Stair Climbing / Step Machine", "Hard", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Quads", "Cardiovascular"));
        addExercise("Rowing Machine", "Hard", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Upper Back", "Core", "Cardiovascular"));

        // HIIT
        addExercise("Burpees", "Hard", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Cardiovascular"));
        addExercise("Mountain Climbers", "Medium", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.CORE, Arrays.asList("Core", "Quads", "Shoulders", "Cardiovascular"));
        addExercise("Jump Squats", "Hard", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Cardiovascular"));
        addExercise("High Knees", "Medium", "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?q=80&w=2070&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Core", "Cardiovascular"));
        addExercise("Box Jumps", "Hard", "https://images.unsplash.com/photo-1566241134883-a341b5275861?q=80&w=2070&auto=format&fit=crop", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Hips", "Cardiovascular"));

        // Yoga
        addExercise("Downward Dog", "Easy", "https://images.unsplash.com/photo-1544367563-12123d8959f9?q=80&w=2070&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Hamstrings", "Calves", "Shoulders", "Back"));
        addExercise("Warrior Pose", "Easy", "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=1999&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "Hips", "Core"));
        addExercise("Child’s Pose", "Easy", "https://images.unsplash.com/photo-1544367563-12123d8959f9?q=80&w=2070&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Back", "Hips"));
        addExercise("Cobra Pose", "Easy", "https://images.unsplash.com/photo-1544367563-12123d8959f9?q=80&w=2070&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "Abdominal Stretch", "Chest Opening"));
        addExercise("Tree Pose", "Medium", "https://images.unsplash.com/photo-1506126613408-eca07ce68773?q=80&w=1999&auto=format&fit=crop", Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Balance", "Glutes", "Core", "Ankles"));
    }

    private static void addExercise(String title, String tags, String imageUrl, Exercise.Category category, Exercise.BodyPart bodyPart, List<String> muscleTargets) {
        allExercises.put(title, new Exercise(title, tags, imageUrl, category, bodyPart, muscleTargets));
    }

    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(allExercises.values());
    }

    public static Exercise getExercise(String title, int sets, int reps, int kg) {
        Exercise template = allExercises.get(title);
        if (template != null) {
            Exercise newExercise = new Exercise(template.getTitle(), template.getTags(), template.getImageUrl(), template.getCategory(), template.getBodyPart(), new ArrayList<>(template.getMuscleTargets()));
            newExercise.setSets(sets);
            newExercise.setReps(reps);
            newExercise.setKg(kg);
            newExercise.setAddedToWorkout(true);
            return newExercise;
        }
        return null; // Or throw an exception
    }
}
