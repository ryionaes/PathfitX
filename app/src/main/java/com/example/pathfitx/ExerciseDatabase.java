package com.example.pathfitx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseDatabase {

    private static final Map<String, Exercise> allExercises = new HashMap<>();

    static {
        populateExercises();
    }

    private static void populateExercises() {
        // ==========================================
        // 1. CHEST (Pectorals) - Average MET: 3.5 - 6.0
        // ==========================================
        addExercise("Push-Ups", "Medium", R.drawable.ex_push_up, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Triceps", "General", "Maintain Weight"), 3.8);
        addExercise("Barbell Bench Press", "Hard", R.drawable.ex_bench_press, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Weight", "General"), 6.0);
        addExercise("Dumbbell Bench Press", "Medium", R.drawable.ex_dumbbell_bench_press, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Muscle"), 5.0);
        addExercise("Incline Barbell Bench Press", "Hard", R.drawable.ex_incline_bench_press, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "General"), 6.0);
        addExercise("Decline Bench Press", "Medium", R.drawable.ex_decline_bench_press, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Lower Chest", "General"), 5.0);
        addExercise("Cable Crossovers", "Medium", R.drawable.ex_cable_crossover, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Definition"), 3.5);
        addExercise("Landmine Press", "Medium", R.drawable.ex_landmine_press, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Shoulders"), 4.0);
        addExercise("Floor Press", "Medium", R.drawable.ex_floor_press, Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Triceps"), 4.0);

        // ==========================================
        // 2. BACK (Lats, Traps, Rhomboids) - Average MET: 3.5 - 8.0
        // ==========================================
        addExercise("Deadlift", "Hard", R.drawable.ex_deadlift, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("General", "Gain Weight", "Back", "Legs"), 8.0);
        addExercise("Pull-Ups", "Hard", R.drawable.ex_pull_up, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("General", "Back", "Lats"), 8.0);
        addExercise("Chin-Ups", "Medium", R.drawable.ex_chin_up, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Biceps", "General"), 7.0);
        addExercise("Lat Pulldowns", "Medium", R.drawable.ex_lat_pulldown, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Gain Muscle"), 4.5);
        addExercise("Seated Cable Row", "Medium", R.drawable.ex_seated_cable, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Gain Muscle"), 4.5);
        addExercise("T-Bar Row", "Hard", R.drawable.ex_tbar, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Middle Back", "General"), 6.0);
        addExercise("Face Pulls", "Easy", R.drawable.ex_face_pull, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Rear Delts", "Upper Back", "General"), 3.0);
        addExercise("Hyperextensions", "Easy", R.drawable.ex_hyperextension, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "General"), 2.8);
        addExercise("Inverted Rows", "Medium", R.drawable.ex_inverted_row, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Bodyweight"), 4.5);
        addExercise("Rack Pulls", "Hard", R.drawable.ex_rack_pull, Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Traps", "Strength"), 7.0);

        // ==========================================
        // 3. LEGS - Average MET: 4.0 - 8.0
        // ==========================================
        addExercise("Squats", "Medium", R.drawable.ex_squats, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Glutes", "General"), 5.0);
        addExercise("Barbell Back Squat", "Hard", R.drawable.ex_barbell_squat, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Gain Weight", "General"), 7.0);
        addExercise("Bodyweight Squats", "Easy", R.drawable.ex_body_squat, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General", "Maintain Weight"), 4.0);
        addExercise("Leg Press", "Medium", R.drawable.ex_leg_press, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Gain Weight", "General"), 5.0);
        addExercise("Walking Lunges", "Medium", R.drawable.ex_walking_lunges, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General"), 5.0);
        addExercise("Romanian Deadlift", "Hard", R.drawable.ex_romanian_deadlift, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Glutes", "Gain Weight"), 6.0);
        addExercise("Stiff-Legged Deadlift", "Medium", R.drawable.ex_stiff_legged, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Legs"), 5.5);
        addExercise("Leg Extensions", "Easy", R.drawable.ex_leg_extension, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Gain Muscle", "General"), 3.0);
        addExercise("Lying Leg Curls", "Easy", R.drawable.ex_lying_leg, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Legs"), 3.0);
        addExercise("Seated Leg Curls", "Easy", R.drawable.ex_seated_leg_curls, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "General"), 3.0);
        addExercise("Standing Calf Raises", "Easy", R.drawable.ex_standing_calf, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Calves", "Gain Muscle", "General"), 3.0);
        addExercise("Seated Calf Raises", "Easy", R.drawable.ex_seated_calf, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Calves", "Soleus"), 3.0);

        // ==========================================
        // 4. SHOULDERS - Average MET: 3.0 - 6.0
        // ==========================================
        addExercise("Seated Dumbbell Shoulder Press", "Medium", R.drawable.ex_seated_shoulder_press, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "General"), 4.5);
        addExercise("Arnold Press", "Medium", R.drawable.ex_arnold_press, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "General"), 4.5);
        addExercise("Reverse Pec Deck", "Easy", R.drawable.ex_reverse_pec, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Shoulders"), 3.0);
        addExercise("Upright Row", "Medium", R.drawable.ex_upright_row, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Traps", "General"), 4.0);
        addExercise("Push Press", "Hard", R.drawable.ex_push_press, Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Power"), 7.0);

        // ==========================================
        // 5. ARMS - Average MET: 2.5 - 4.5
        // ==========================================
        addExercise("Dumbbell Bicep Curl", "Easy", R.drawable.ex_dumbbell_bicep_curl, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Gain Muscle", "General"), 3.0);
        addExercise("Hammer Curls", "Easy", R.drawable.ex_hammer_curls, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Brachialis", "Forearms", "General"), 3.0);
        addExercise("Incline Dumbbell Curls", "Medium", R.drawable.ex_incline_dumbbell_curl, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Stretch"), 3.5);
        addExercise("Preacher Curls", "Medium", R.drawable.ex_preacher, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Isolation", "General"), 3.0);
        addExercise("Concentration Curls", "Easy", R.drawable.ex_concentration_curl, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Peak", "Arms"), 2.5);
        addExercise("Cable Bicep Curls", "Easy", R.drawable.ex_cable_bicep_curl, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"), 3.0);
        addExercise("Spider Curls", "Medium", R.drawable.ex_spider_curl, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"), 3.0);
        addExercise("Zottman Curls", "Medium", R.drawable.ex_zottman_curl, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Forearms", "Arms"), 3.5);
        addExercise("Skull Crushers", "Medium", R.drawable.ex_skull_crushers, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "General", "Arms"), 3.5);
        addExercise("Tricep Rope Pushdowns", "Easy", R.drawable.ex_tricep_rope_pushdown, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"), 2.8);
        addExercise("Tricep Bar Pushdowns", "Easy", R.drawable.ex_tricep_bar_pushdown, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"), 2.8);
        addExercise("Close-Grip Bench Press", "Medium", R.drawable.ex_close_grip_press, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Chest", "Arms"), 5.0);
        addExercise("Tricep Kickbacks", "Easy", R.drawable.ex_tricep_kickbacks, Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"), 2.5);

        // ==========================================
        // 6. CORE - Average MET: 3.0 - 6.0
        // ==========================================
        addExercise("Plank", "Medium", R.drawable.ex_plank, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability", "General", "Maintain Weight"), 3.5);
        addExercise("Crunches", "Easy", R.drawable.ex_crunches, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "General"), 3.0);
        addExercise("Sit-Ups", "Medium", R.drawable.ex_sit_up, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "General"), 4.0);
        addExercise("Russian Twists", "Medium", R.drawable.ex_russian_twist, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Core", "General"), 4.0);
        addExercise("Hanging Leg Raises", "Hard", R.drawable.ex_leg_raises, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core", "General"), 5.0);
        addExercise("Lying Leg Raises", "Medium", R.drawable.ex_lying_leg_raises, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "General"), 3.5);
        addExercise("Ab Wheel Rollouts", "Hard", R.drawable.ex_ab_rollout, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"), 5.0);
        addExercise("Cable Woodchoppers", "Medium", R.drawable.ex_cable_woodchoppers, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Core"), 4.0);
        addExercise("V-Ups", "Hard", R.drawable.ex_v_ups, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "Core"), 6.0);
        addExercise("Dead Bugs", "Easy", R.drawable.ex_dead_bug, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"), 2.5);
        addExercise("Flutter Kicks", "Medium", R.drawable.ex_flutter_kick, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core"), 4.0);
        addExercise("Scissor Kicks", "Medium", R.drawable.ex_scissor_kicks, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core"), 4.0);
        addExercise("Pallof Press", "Medium", R.drawable.ex_pallof_press, Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Anti-Rotation", "Core"), 3.0);

        // ==========================================
        // 7. CARDIO / HIIT
        // ==========================================
        addExercise("Running", "Medium", R.drawable.ex_running, Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Cardio", "General", "Maintain Weight"), 9.8);
        addExercise("Jump Rope", "Medium", R.drawable.ex_jump_rope, Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "General", "Boost Energy Level"), 11.0);
        addExercise("Mountain Climbers", "Medium", R.drawable.ex_mountain_climbers, Exercise.Category.HIIT, Exercise.BodyPart.CORE, Arrays.asList("Lose Weight", "Core", "General", "Boost Energy Level"), 8.0);
        addExercise("Butt Kicks", "Medium", R.drawable.ex_butt_kick, Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Cardio", "General", "Boost Energy Level"), 8.0);
        addExercise("Box Jumps", "Hard", R.drawable.ex_box_jump, Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Explosive", "General", "Boost Energy Level"), 9.0);
        addExercise("Kettlebell Swings", "Medium", R.drawable.ex_kettlebell_swing, Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Hips", "General"), 8.0);
        addExercise("Rowing Machine", "Hard", R.drawable.ex_rowing_machine, Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "Increase Endurance", "General"), 8.5);
        addExercise("Stair Climber", "Medium", R.drawable.ex_stair_climber, Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Lose Weight", "Increase Endurance"), 9.0);
        addExercise("Battle Ropes", "Hard", R.drawable.ex_battle_ropes, Exercise.Category.HIIT, Exercise.BodyPart.ARMS, Arrays.asList("Full Body", "General"), 10.0);
        addExercise("Farmer's Walk", "Medium", R.drawable.ex_farmers_walk, Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Grip", "Core", "General"), 5.5);
        addExercise("Bear Crawl", "Medium", R.drawable.ex_bear_crawl, Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Full Body", "Core"), 8.0);

        // ==========================================
        // 8. YOGA / FLEXIBILITY
        // ==========================================
        addExercise("Bird Dog", "Easy", R.drawable.ex_bird_dog, Exercise.Category.YOGA, Exercise.BodyPart.CORE, Arrays.asList("Core", "Balance", "General"), 2.0);
        addExercise("Fire Hydrants", "Easy", R.drawable.ex_fire_hydrant, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Hips", "General"), 2.5);
        addExercise("Donkey Kicks", "Easy", R.drawable.ex_donkey_kicks, Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "General"), 2.5);
        addExercise("Foam Rolling", "Easy", R.drawable.ex_foam_rolling, Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Recovery"), 1.8);
        addExercise("Pigeon Pose", "Easy", R.drawable.ex_pigeon, Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Improve Flexibility", "Hips"), 1.8);
        addExercise("Cat-Cow Stretches", "Easy", R.drawable.ex_cat_cow, Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Improve Flexibility", "Spine"), 2.0);
    }

    private static void addExercise(String title, String difficulty, int imageResId, Exercise.Category category, Exercise.BodyPart bodyPart, List<String> muscleTargets, double met) {
        List<String> combinedTagsList = new ArrayList<>(muscleTargets);
        combinedTagsList.add(difficulty);
        String tagsString = String.join(", ", combinedTagsList);

        allExercises.put(title, new Exercise(title, tagsString, imageResId, category, bodyPart, muscleTargets, met));
    }

    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(allExercises.values());
    }

    public static Exercise getExercise(String title, int sets, int reps, int kg) {
        Exercise template = allExercises.get(title);
        if (template != null) {
            Exercise newExercise = new Exercise(
                    template.getTitle(),
                    template.getTags(),
                    template.getImageResId(),
                    template.getCategory(),
                    template.getBodyPart(),
                    new ArrayList<>(template.getMuscleTargets()),
                    template.getMet()
            );
            newExercise.setSets(sets);
            newExercise.setReps(reps);
            newExercise.setKg(kg);
            newExercise.setAddedToWorkout(true);
            return newExercise;
        }
        return null;
    }

    public static double calculateCalories(double weight, double met, double durationInMinutes) {
        if (durationInMinutes <= 0) return 0;
        return (met * 3.5 * weight / 200) * durationInMinutes;
    }
}