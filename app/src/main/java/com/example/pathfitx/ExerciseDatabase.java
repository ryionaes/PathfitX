package com.example.pathfitx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseDatabase {

    private static final Map<String, Exercise> allExercises = new HashMap<>();

    static {
        // ==========================================
        // 1. CHEST (Pectorals) - Average MET: 3.5 - 6.0
        // ==========================================
        addExercise("Push-Ups", "Medium", "https://www.realsimple.com/thmb/-PW_i-RMLQ4cj3-okjqccGGUock=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/health-benefits-of-pushups-GettyImages-498315681-7008d40842444270868c88b516496884.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Triceps", "General"), 3.8);
        addExercise("Barbell Bench Press", "Hard", "https://ironbullstrength.com/cdn/shop/articles/how-to-increase-your-bench-press.jpg?v=1715282936", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Weight", "General"), 6.0);
        addExercise("Dumbbell Bench Press", "Medium", "https://flybirdfitness.com/cdn/shop/articles/Dumbbell_Bench_Press_d1e4b67f-6686-4948-9beb-5921f41b36a3.webp?v=1739964443", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Muscle"), 5.0);
        addExercise("Incline Barbell Bench Press", "Hard", "https://samsfitness.com.au/wp-content/uploads/2023/11/ATX-MPX-500-incline-bench-press-smith-machine.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "General"), 6.0);
        addExercise("Incline Dumbbell Press", "Medium", "https://www.meridian-fitness.co.uk/wp-content/uploads/2022/04/incline-dumbbell-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Gain Muscle"), 5.0);
        addExercise("Decline Bench Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/decline-bench-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Lower Chest", "General"), 5.0);
        addExercise("Chest Fly (Machine or Dumbbell)", "Medium", "https://www.trainheroic.com/wp-content/uploads/2022/12/Dumbbell-Flys.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Muscle"), 3.5);
        addExercise("Cable Crossovers", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/cable-crossover-1.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Definition"), 3.5);
        addExercise("Pec Deck Machine", "Easy", "https://i.ytimg.com/vi/O-OnKP6PrIs/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLB-6XQdZ9_2u4wK5d3rmopZ_9G39w", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Isolation"), 3.0);
        addExercise("Weighted Dips", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/weighted-dip.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Lower Chest", "Triceps", "Gain Weight"), 6.0);
        addExercise("Diamond Push-Ups", "Hard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRzh91E9762Z2V73eQZtY0R5E9G763e03195w&s", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Inner Chest", "Triceps"), 4.5);
        addExercise("Dumbbell Pullovers", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-pullover.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Lats"), 3.5);
        addExercise("Landmine Press", "Medium", "https://i.ytimg.com/vi/J_6Z6F6X970/maxresdefault.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Shoulders"), 4.0);
        addExercise("Floor Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/floor-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Triceps"), 4.0);

        // ==========================================
        // 2. BACK (Lats, Traps, Rhomboids) - Average MET: 3.5 - 8.0
        // ==========================================
        addExercise("Deadlift", "Hard", "https://hips.hearstapps.com/hmg-prod/images/deadlift-workout-for-back-royalty-free-image-1575482387.jpg?crop=0.668xw:1.00xh;0.224xw,0&resize=1200:*", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("General", "Gain Weight", "Back", "Legs"), 8.0);
        addExercise("Pull-Ups", "Hard", "https://www.inspireusafoundation.org/wp-content/uploads/2022/10/pull-up-muscles-768x512.png", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("General", "Back", "Lats"), 8.0);
        addExercise("Chin-Ups", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/chin-up.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Biceps", "General"), 7.0);
        addExercise("Lat Pulldowns", "Medium", "https://hips.hearstapps.com/hmg-prod/images/lat-pulldown-workout-royalty-free-image-1706691763.jpg?crop=0.668xw:1.00xh;0.0867xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Gain Muscle"), 4.5);
        addExercise("Barbell Bent-Over Rows", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/bent-over-barbell-row.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Gain Weight", "General"), 6.0);
        addExercise("Seated Cable Row", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/seated-cable-row.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Gain Muscle"), 4.5);
        addExercise("Single-Arm Dumbbell Row", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/one-arm-dumbbell-row.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps"), 4.5);
        addExercise("T-Bar Row", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/t-bar-row.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Middle Back", "General"), 6.0);
        addExercise("Face Pulls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/face-pull.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Rear Delts", "Upper Back", "General"), 3.0);
        addExercise("Shrugs", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-shrug.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Traps", "General"), 3.0);
        addExercise("Hyperextensions", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/hyperextension.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "General"), 2.8);
        addExercise("Inverted Rows", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/inverted-row.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Bodyweight"), 4.5);
        addExercise("Rack Pulls", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/rack-pull.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Traps", "Strength"), 7.0);
        addExercise("Renegade Rows", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/renegade-row.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Core", "Stability"), 6.5);

        // ==========================================
        // 3. LEGS - Average MET: 4.0 - 8.0
        // ==========================================
        addExercise("Squats", "Medium", "https://hips.hearstapps.com/hmg-prod/images/young-woman-weightraining-at-the-gym-royalty-free-image-1595583398.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Glutes", "General"), 5.0);
        addExercise("Barbell Back Squat", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/squat.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Gain Weight", "General"), 7.0);
        addExercise("Front Squat", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/front-squat.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Legs"), 7.0);
        addExercise("Goblet Squat", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/goblet-squat.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "General"), 5.5);
        addExercise("Bodyweight Squats", "Easy", "https://hips.hearstapps.com/hmg-prod/images/squat-jump-squat-1570724623.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General"), 4.0);
        addExercise("Leg Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/leg-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Gain Weight", "General"), 5.0);
        addExercise("Lunges", "Medium", "https://hips.hearstapps.com/hmg-prod/images/lunge-1554911762.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General"), 4.5);
        addExercise("Walking Lunges", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/walking-lunge.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General"), 5.0);
        addExercise("Reverse Lunges", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/reverse-lunge.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Glutes"), 4.5);
        addExercise("Bulgarian Split Squat", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/bulgarian-split-squat.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Unilateral"), 6.0);
        addExercise("Romanian Deadlift", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/romanian-deadlift.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Glutes", "Gain Weight"), 6.0);
        addExercise("Stiff-Legged Deadlift", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/stiff-leg-deadlift.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Legs"), 5.5);
        addExercise("Leg Extensions", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/leg-extension.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Gain Muscle", "General"), 3.0);
        addExercise("Lying Leg Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/lying-leg-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Legs"), 3.0);
        addExercise("Seated Leg Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/seated-leg-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "General"), 3.0);
        addExercise("Standing Calf Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/standing-calf-raise.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Calves", "Gain Muscle", "General"), 3.0);
        addExercise("Seated Calf Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/seated-calf-raise.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Calves", "Soleus"), 3.0);
        addExercise("Hip Thrusts", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/hip-thrust.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "General"), 6.0);
        addExercise("Glute Bridge", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/glute-bridge.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Legs", "General"), 3.5);
        addExercise("Hack Squat", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/hack-squat.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Legs", "General"), 5.5);
        addExercise("Sumo Squat", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/sumo-squat.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Inner Thighs", "Glutes", "General"), 5.0);
        addExercise("Wall Sit", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/wall-sit.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Endurance", "General"), 3.0);

        // ==========================================
        // 4. SHOULDERS - Average MET: 3.0 - 6.0
        // ==========================================
        addExercise("Overhead Barbell Press", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/overhead-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Gain Weight", "General"), 6.0);
        addExercise("Seated Dumbbell Shoulder Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/seated-dumbbell-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "General"), 4.5);
        addExercise("Arnold Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/arnold-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "General"), 4.5);
        addExercise("Dumbbell Lateral Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-lateral-raise.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Side Delts", "Gain Muscle", "General"), 3.0);
        addExercise("Cable Lateral Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/cable-lateral-raise.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Side Delts", "Shoulders"), 3.0);
        addExercise("Front Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-front-raise.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Front Delts", "Shoulders", "General"), 3.0);
        addExercise("Rear Delt Flys", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/rear-delt-fly.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Shoulders"), 3.0);
        addExercise("Reverse Pec Deck", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/reverse-pec-deck.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Shoulders"), 3.0);
        addExercise("Upright Row", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/upright-row.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Traps", "General"), 4.0);
        addExercise("Push Press", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/push-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Power"), 7.0);
        addExercise("Landmine Shoulder Press", "Medium", "https://i.ytimg.com/vi/Zt6iXNl0o7U/maxresdefault.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Core"), 4.0);
        addExercise("Shoulder Press Machine", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/machine-shoulder-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Machine"), 3.0);
        addExercise("Around the Worlds", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/around-the-world.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Mobility"), 3.5);

        // ==========================================
        // 5. ARMS - Average MET: 2.5 - 4.5
        // ==========================================
        addExercise("Barbell Bicep Curl", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/barbell-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"), 3.5);
        addExercise("Dumbbell Bicep Curl", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Gain Muscle", "General"), 3.0);
        addExercise("Hammer Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/hammer-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Brachialis", "Forearms", "General"), 3.0);
        addExercise("Incline Dumbbell Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/incline-dumbbell-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Stretch"), 3.5);
        addExercise("Preacher Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/preacher-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Isolation", "General"), 3.0);
        addExercise("Concentration Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/concentration-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Peak", "Arms"), 2.5);
        addExercise("Cable Bicep Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/cable-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"), 3.0);
        addExercise("Spider Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/spider-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"), 3.0);
        addExercise("Zottman Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/zottman-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Forearms", "Arms"), 3.5);
        addExercise("Tricep Dips", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/tricep-dip.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "General", "Arms"), 4.0);
        addExercise("Skull Crushers", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/skullcrusher.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "General", "Arms"), 3.5);
        addExercise("Tricep Rope Pushdowns", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/tricep-rope-pushdown.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"), 2.8);
        addExercise("Tricep Bar Pushdowns", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/tricep-pushdown.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"), 2.8);
        addExercise("Overhead Dumbbell Extension", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/overhead-dumbbell-extension.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Long Head", "Arms"), 3.0);
        addExercise("Close-Grip Bench Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/close-grip-bench-press.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Chest", "Arms"), 5.0);
        addExercise("Tricep Kickbacks", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/tricep-kickback.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"), 2.5);
        addExercise("Reverse Grip Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/reverse-grip-curl.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Forearms", "Biceps", "Arms"), 3.0);

        // ==========================================
        // 6. CORE - Average MET: 3.0 - 6.0
        // ==========================================
        addExercise("Plank", "Medium", "https://hips.hearstapps.com/hmg-prod/images/hoka-z-5-1563811904.jpg?crop=0.668xw:1.00xh;0.141xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability", "General"), 3.5);
        addExercise("Crunches", "Easy", "https://hips.hearstapps.com/hmg-prod/images/man-doing-crunches-royalty-free-image-1597163884.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "General"), 3.0);
        addExercise("Sit-Ups", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/sit-up.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "General"), 4.0);
        addExercise("Russian Twists", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/russian-twist.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Core", "General"), 4.0);
        addExercise("Bicycle Crunches", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/bicycle-crunch.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Abs", "General"), 4.5);
        addExercise("Hanging Leg Raises", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/hanging-leg-raise.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core", "General"), 5.0);
        addExercise("Lying Leg Raises", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/lying-leg-raise.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "General"), 3.5);
        addExercise("Side Plank", "Medium", "https://experiencelife.lifetime.life/wp-content/uploads/2021/07/bid-side-plank.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Stability", "General"), 3.5);
        addExercise("Ab Wheel Rollouts", "Hard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTKmvK7Jf0HMEBWGejpAUXouiDuzWJJupcH_w&s", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"), 5.0);
        addExercise("Cable Woodchoppers", "Medium", "https://cdn.shopify.com/s/files/1/0536/1244/5846/files/kneeling_cable_woodchopper_1_480x480.jpg?v=1744898590", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Core"), 4.0);
        addExercise("V-Ups", "Hard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8GXMihxnw7sBRTmkMAYruHGi-Dd3yzkbXqQ&s", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "Core"), 6.0);
        addExercise("Dead Bugs", "Easy", "https://images.squarespace-cdn.com/content/v1/5a620a85d55b41e7233c5243/716fc661-adb7-4edf-914c-4b17c4f8f7e2/105-Dead-Bug-ExerciseGIF.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"), 2.5);
        addExercise("Flutter Kicks", "Medium", "https://i0.wp.com/post.healthline.com/wp-content/uploads/2022/01/400x400_The_5_Best_Exercises_That_Target_the_Lower_Abs_Flutter_Kicks.gif?h=840", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core"), 4.0);
        addExercise("Scissor Kicks", "Medium", "https://i.ytimg.com/vi/WoNCIBVLbgY/maxresdefault.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core"), 4.0);
        addExercise("Decline Sit-Ups", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/decline-sit-up.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "Core"), 4.5);
        addExercise("Toes-to-Bar", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/toes-to-bar.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Abs"), 6.0);
        addExercise("Pallof Press", "Medium", "https://i.ytimg.com/vi/AH_QZLm_0-s/maxresdefault.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Anti-Rotation", "Core"), 3.0);
        addExercise("Hollow Body Hold", "Hard", "https://i.ytimg.com/vi/Wp4BlxcFTkE/maxresdefault.jpg", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"), 4.0);

        // ==========================================
        // 7. CARDIO / HIIT - Average MET: 4.0 - 15.0
        // ==========================================
        addExercise("Running", "Medium", "https://hips.hearstapps.com/hmg-prod/images/running-track-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Cardio", "General"), 9.8);
        addExercise("Brisk Walking", "Easy", "https://www.verywellfit.com/thmb/b-mg6q0k-8g3-0q-1500x1000.jpg", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Low Impact"), 3.5);
        addExercise("Cycling", "Medium", "https://hips.hearstapps.com/hmg-prod/images/cycling-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Cardio", "General"), 7.5);
        addExercise("Spinning", "Hard", "https://hips.hearstapps.com/hmg-prod/images/spinning-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Lose Weight", "Cardio"), 10.0);
        addExercise("Jump Rope", "Medium", "https://hips.hearstapps.com/hmg-prod/images/jump-rope-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "General"), 11.0);
        addExercise("Burpees", "Hard", "https://hips.hearstapps.com/hmg-prod/images/burpee-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "General", "Full Body"), 10.0);
        addExercise("Mountain Climbers", "Medium", "https://hips.hearstapps.com/hmg-prod/images/mountain-climber-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.CORE, Arrays.asList("Lose Weight", "Core", "General"), 8.0);
        addExercise("Jumping Jacks", "Medium", "https://hips.hearstapps.com/hmg-prod/images/jumping-jack-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Boost Energy Levels", "General"), 8.0);
        addExercise("High Knees", "Medium", "https://hips.hearstapps.com/hmg-prod/images/high-knees-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Cardio", "General"), 9.0);
        addExercise("Butt Kicks", "Medium", "https://hips.hearstapps.com/hmg-prod/images/butt-kicks-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Cardio", "General"), 8.0);
        addExercise("Box Jumps", "Hard", "https://hips.hearstapps.com/hmg-prod/images/box-jump-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Explosive", "General"), 9.0);
        addExercise("Kettlebell Swings", "Medium", "https://hips.hearstapps.com/hmg-prod/images/kettlebell-swing-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Hips", "General"), 8.0);
        addExercise("Rowing Machine", "Hard", "https://hips.hearstapps.com/hmg-prod/images/rowing-machine-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "Increase Endurance", "General"), 8.5);
        addExercise("Stair Climber", "Medium", "https://hips.hearstapps.com/hmg-prod/images/stair-climber-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Lose Weight", "Endurance"), 9.0);
        addExercise("Battle Ropes", "Hard", "https://hips.hearstapps.com/hmg-prod/images/battle-ropes-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.ARMS, Arrays.asList("Full Body", "General"), 10.0);
        addExercise("Farmer's Walk", "Medium", "https://hips.hearstapps.com/hmg-prod/images/farmers-walk-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Grip", "Core", "General"), 5.5);
        addExercise("Bear Crawl", "Medium", "https://hips.hearstapps.com/hmg-prod/images/bear-crawl-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Boost Energy Levels", "General"), 8.0);
        addExercise("Thrusters", "Hard", "https://hips.hearstapps.com/hmg-prod/images/thruster-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("General", "Crossfit"), 12.0);
        addExercise("Wall Balls", "Medium", "https://hips.hearstapps.com/hmg-prod/images/wall-ball-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("General", "Cardio"), 9.0);
        addExercise("Boxing", "Hard", "https://hips.hearstapps.com/hmg-prod/images/boxing-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "Cardio"), 12.0);
        addExercise("Kickboxing", "Hard", "https://hips.hearstapps.com/hmg-prod/images/kickboxing-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "Cardio"), 10.0);
        addExercise("Swimming", "Medium", "https://hips.hearstapps.com/hmg-prod/images/swimming-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Endurance"), 7.0);
        addExercise("Hiking", "Medium", "https://hips.hearstapps.com/hmg-prod/images/hiking-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Maintain Weight", "Outdoors"), 6.0);
        addExercise("Zumba", "Medium", "https://hips.hearstapps.com/hmg-prod/images/zumba-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Dance"), 6.5);
        addExercise("Elliptical Machine", "Medium", "https://hips.hearstapps.com/hmg-prod/images/elliptical-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Maintain Weight", "Low Impact"), 5.0);
        addExercise("Water Aerobics", "Easy", "https://hips.hearstapps.com/hmg-prod/images/water-aerobics-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Low Impact"), 4.0);
        addExercise("Sprint Intervals", "Hard", "https://hips.hearstapps.com/hmg-prod/images/sprint-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Lose Weight", "Speed"), 15.0);
        addExercise("Clean and Jerk", "Hard", "https://hips.hearstapps.com/hmg-prod/images/clean-and-jerk-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Gain Weight", "Power", "General"), 8.0);
        addExercise("Snatch", "Hard", "https://hips.hearstapps.com/hmg-prod/images/snatch-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("General", "Power"), 8.0);

        // ==========================================
        // 8. YOGA / FLEXIBILITY - Average MET: 1.5 - 4.0
        // ==========================================
        addExercise("Superman", "Easy", "https://hips.hearstapps.com/hmg-prod/images/superman-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "General"), 2.0);
        addExercise("Bird Dog", "Easy", "https://hips.hearstapps.com/hmg-prod/images/bird-dog-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.CORE, Arrays.asList("Core", "Balance", "General"), 2.0);
        addExercise("Fire Hydrants", "Easy", "https://hips.hearstapps.com/hmg-prod/images/fire-hydrant-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Hips", "General"), 2.5);
        addExercise("Donkey Kicks", "Easy", "https://hips.hearstapps.com/hmg-prod/images/donkey-kick-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "General"), 2.5);
        addExercise("Step-Ups", "Medium", "https://hips.hearstapps.com/hmg-prod/images/step-up-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Glutes", "General"), 4.0);
        addExercise("Tai Chi", "Easy", "https://hips.hearstapps.com/hmg-prod/images/tai-chi-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Balance"), 3.0);
        addExercise("Light Yoga Flow", "Easy", "https://hips.hearstapps.com/hmg-prod/images/yoga-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Flexibility"), 2.5);
        addExercise("Morning Stretching", "Easy", "https://hips.hearstapps.com/hmg-prod/images/stretching-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Flexibility"), 2.0);
        addExercise("Foam Rolling", "Easy", "https://hips.hearstapps.com/hmg-prod/images/foam-rolling-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Recovery"), 1.8);
        addExercise("Vinyasa Yoga", "Medium", "https://hips.hearstapps.com/hmg-prod/images/vinyasa-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Strength"), 4.0);
        addExercise("Yin Yoga", "Easy", "https://hips.hearstapps.com/hmg-prod/images/yin-yoga-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Relaxation"), 2.0);
        addExercise("Pilates", "Medium", "https://hips.hearstapps.com/hmg-prod/images/pilates-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Improve Flexibility", "Core"), 3.5);
        addExercise("Dynamic Warm-ups", "Easy", "https://hips.hearstapps.com/hmg-prod/images/warm-up-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Warmup"), 3.0);
        addExercise("Static Stretching", "Easy", "https://hips.hearstapps.com/hmg-prod/images/static-stretching-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Cooldown"), 1.8);
        addExercise("Pigeon Pose", "Easy", "https://hips.hearstapps.com/hmg-prod/images/pigeon-pose-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Improve Flexibility", "Hips"), 1.8);
        addExercise("Cat-Cow Stretches", "Easy", "https://hips.hearstapps.com/hmg-prod/images/cat-cow-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Improve Flexibility", "Spine"), 2.0);
        addExercise("Sun Salutations", "Medium", "https://hips.hearstapps.com/hmg-prod/images/sun-salutation-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*", Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Boost Energy Levels", "Yoga"), 3.5);
    }

    private static void addExercise(String title, String difficulty, String imageUrl, Exercise.Category category, Exercise.BodyPart bodyPart, List<String> muscleTargets, double met) {
        // Mahalaga: I-copy sa bagong ArrayList para hindi mag-error ang Arrays.asList
        List<String> combinedTagsList = new ArrayList<>(muscleTargets);
        combinedTagsList.add(difficulty);
        String tagsString = String.join(", ", combinedTagsList);

        // Match na dapat ito sa Exercise(String, String, String, Category, BodyPart, List<String>, double)
        allExercises.put(title, new Exercise(title, tagsString, imageUrl, category, bodyPart, muscleTargets, met));
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
                    template.getImageUrl(),
                    template.getCategory(),
                    template.getBodyPart(),
                    new ArrayList<>(template.getMuscleTargets()),
                    template.getMet() // Wag kalimutan ang MET!
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
        // Formula: (MET * 3.5 * weight_kg / 200) * duration_minutes
        return (met * 3.5 * weight / 200) * durationInMinutes;
    }
}
