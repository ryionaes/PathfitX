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
        // 1. CHEST (Pectorals)
        // ==========================================
        addExercise("Push-Ups", "Medium", "https://www.realsimple.com/thmb/-PW_i-RMLQ4cj3-okjqccGGUock=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/health-benefits-of-pushups-GettyImages-498315681-7008d40842444270868c88b516496884.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Triceps", "General"));
        addExercise("Barbell Bench Press", "Hard", "https://ironbullstrength.com/cdn/shop/articles/how-to-increase-your-bench-press.jpg?v=1715282936",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Weight", "General"));
        addExercise("Dumbbell Bench Press", "Medium", "https://flybirdfitness.com/cdn/shop/articles/Dumbbell_Bench_Press_d1e4b67f-6686-4948-9beb-5921f41b36a3.webp?v=1739964443",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Muscle"));
        addExercise("Incline Barbell Bench Press", "Hard", "https://samsfitness.com.au/wp-content/uploads/2023/11/ATX-MPX-500-incline-bench-press-smith-machine.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "General"));
        addExercise("Incline Dumbbell Press", "Medium", "https://www.meridian-fitness.co.uk/wp-content/uploads/2022/04/incline-dumbbell-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Gain Muscle"));
        addExercise("Decline Bench Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/decline-bench-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Lower Chest", "General"));
        addExercise("Chest Fly (Machine or Dumbbell)", "Medium", "https://www.trainheroic.com/wp-content/uploads/2022/12/Dumbbell-Flys.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Gain Muscle"));
        addExercise("Cable Crossovers", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/cable-crossover-1.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Definition"));
        addExercise("Pec Deck Machine", "Easy", "https://i.ytimg.com/vi/O-OnKP6PrIs/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLB-6XQdZ9_2u4wK5d3rmopZ_9G39w",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Isolation"));
        addExercise("Weighted Dips", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/weighted-dip.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Lower Chest", "Triceps", "Gain Weight"));
        addExercise("Diamond Push-Ups", "Hard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRzh91E9762Z2V73eQZtY0R5E9G763e03195w&s",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Inner Chest", "Triceps"));
        addExercise("Dumbbell Pullovers", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-pullover.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Lats"));
        addExercise("Landmine Press", "Medium", "https://i.ytimg.com/vi/J_6Z6F6X970/maxresdefault.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Upper Chest", "Shoulders"));
        addExercise("Floor Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/floor-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CHEST, Arrays.asList("Chest", "Triceps"));

        // ==========================================
        // 2. BACK (Lats, Traps, Rhomboids)
        // ==========================================
        addExercise("Deadlift", "Hard", "https://hips.hearstapps.com/hmg-prod/images/deadlift-workout-for-back-royalty-free-image-1575482387.jpg?crop=0.668xw:1.00xh;0.224xw,0&resize=1200:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("General", "Gain Weight", "Back", "Legs"));
        addExercise("Pull-Ups", "Hard", "https://www.inspireusafoundation.org/wp-content/uploads/2022/10/pull-up-muscles-768x512.png",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("General", "Back", "Lats"));
        addExercise("Chin-Ups", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/chin-up.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Biceps", "General"));
        addExercise("Lat Pulldowns", "Medium", "https://hips.hearstapps.com/hmg-prod/images/lat-pulldown-workout-royalty-free-image-1706691763.jpg?crop=0.668xw:1.00xh;0.0867xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Gain Muscle"));
        addExercise("Barbell Bent-Over Rows", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/bent-over-barbell-row.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Gain Weight", "General"));
        addExercise("Seated Cable Row", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/seated-cable-row.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Gain Muscle"));
        addExercise("Single-Arm Dumbbell Row", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/one-arm-dumbbell-row.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lats", "Biceps"));
        addExercise("T-Bar Row", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/t-bar-row.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Middle Back", "General"));
        addExercise("Face Pulls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/face-pull.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Rear Delts", "Upper Back", "General"));
        addExercise("Shrugs", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-shrug.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Traps", "General"));
        addExercise("Hyperextensions", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/hyperextension.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "General"));
        addExercise("Inverted Rows", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/inverted-row.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Bodyweight"));
        addExercise("Rack Pulls", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/rack-pull.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Traps", "Strength"));
        addExercise("Renegade Rows", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/renegade-row.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.BACK, Arrays.asList("Back", "Core", "Stability"));

        // ==========================================
        // 3. LEGS (Quads, Hams, Glutes, Calves)
        // ==========================================
        addExercise("Squats", "Medium", "https://hips.hearstapps.com/hmg-prod/images/young-woman-weightraining-at-the-gym-royalty-free-image-1595583398.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Glutes", "General"));
        addExercise("Barbell Back Squat", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/squat.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Gain Weight", "General"));
        addExercise("Front Squat", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/front-squat.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Legs"));
        addExercise("Goblet Squat", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/goblet-squat.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Glutes", "General"));
        addExercise("Bodyweight Squats", "Easy", "https://hips.hearstapps.com/hmg-prod/images/squat-jump-squat-1570724623.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General"));
        addExercise("Leg Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/leg-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Gain Weight", "General"));
        addExercise("Lunges", "Medium", "https://hips.hearstapps.com/hmg-prod/images/lunge-1554911762.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General"));
        addExercise("Walking Lunges", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/walking-lunge.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "General"));
        addExercise("Reverse Lunges", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/reverse-lunge.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Glutes"));
        addExercise("Bulgarian Split Squat", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/bulgarian-split-squat.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Unilateral"));
        addExercise("Romanian Deadlift", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/romanian-deadlift.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Glutes", "Gain Weight"));
        addExercise("Stiff-Legged Deadlift", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/stiff-leg-deadlift.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Legs"));
        addExercise("Leg Extensions", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/leg-extension.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Gain Muscle", "General"));
        addExercise("Lying Leg Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/lying-leg-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "Legs"));
        addExercise("Seated Leg Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/seated-leg-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Hamstrings", "General"));
        addExercise("Standing Calf Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/standing-calf-raise.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Calves", "Gain Muscle", "General"));
        addExercise("Seated Calf Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/seated-calf-raise.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Calves", "Soleus"));
        addExercise("Hip Thrusts", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/hip-thrust.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "General"));
        addExercise("Glute Bridge", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/glute-bridge.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Legs", "General"));
        addExercise("Hack Squat", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/hack-squat.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Legs", "General"));
        addExercise("Sumo Squat", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/sumo-squat.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Inner Thighs", "Glutes", "General"));
        addExercise("Wall Sit", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/wall-sit.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Quads", "Endurance", "General"));

        // ==========================================
        // 4. SHOULDERS (Delts)
        // ==========================================
        addExercise("Overhead Barbell Press", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/overhead-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Gain Weight", "General"));
        addExercise("Seated Dumbbell Shoulder Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/seated-dumbbell-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "General"));
        addExercise("Arnold Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/arnold-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "General"));
        addExercise("Dumbbell Lateral Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-lateral-raise.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Side Delts", "Gain Muscle", "General"));
        addExercise("Cable Lateral Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/cable-lateral-raise.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Side Delts", "Shoulders"));
        addExercise("Front Raises", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-front-raise.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Front Delts", "Shoulders", "General"));
        addExercise("Rear Delt Flys", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/rear-delt-fly.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Shoulders"));
        addExercise("Reverse Pec Deck", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/reverse-pec-deck.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Rear Delts", "Shoulders"));
        addExercise("Upright Row", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/upright-row.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Traps", "General"));
        addExercise("Push Press", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/push-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Power"));
        addExercise("Landmine Shoulder Press", "Medium", "https://i.ytimg.com/vi/Zt6iXNl0o7U/maxresdefault.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Core"));
        addExercise("Shoulder Press Machine", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/machine-shoulder-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Machine"));
        addExercise("Around the Worlds", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/around-the-world.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.SHOULDERS, Arrays.asList("Shoulders", "Mobility"));

        // ==========================================
        // 5. ARMS (Biceps, Triceps, Forearms)
        // ==========================================
        addExercise("Barbell Bicep Curl", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/barbell-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"));
        addExercise("Dumbbell Bicep Curl", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/dumbbell-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Gain Muscle", "General"));
        addExercise("Hammer Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/hammer-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Brachialis", "Forearms", "General"));
        addExercise("Incline Dumbbell Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/incline-dumbbell-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Stretch"));
        addExercise("Preacher Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/preacher-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Isolation", "General"));
        addExercise("Concentration Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/concentration-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Peak", "Arms"));
        addExercise("Cable Bicep Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/cable-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"));
        addExercise("Spider Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/spider-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Arms"));
        addExercise("Zottman Curls", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/zottman-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Biceps", "Forearms", "Arms"));
        addExercise("Tricep Dips", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/tricep-dip.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "General", "Arms"));
        addExercise("Skull Crushers", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/skullcrusher.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "General", "Arms"));
        addExercise("Tricep Rope Pushdowns", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/tricep-rope-pushdown.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"));
        addExercise("Tricep Bar Pushdowns", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/tricep-pushdown.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"));
        addExercise("Overhead Dumbbell Extension", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/overhead-dumbbell-extension.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Long Head", "Arms"));
        addExercise("Close-Grip Bench Press", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/close-grip-bench-press.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Chest", "Arms"));
        addExercise("Tricep Kickbacks", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/tricep-kickback.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Triceps", "Arms"));
        addExercise("Reverse Grip Curls", "Easy", "https://cdn.muscleandstrength.com/sites/default/files/reverse-grip-curl.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.ARMS, Arrays.asList("Forearms", "Biceps", "Arms"));

        // ==========================================
        // 6. CORE (Abs, Obliques)
        // ==========================================
        addExercise("Plank", "Medium", "https://hips.hearstapps.com/hmg-prod/images/hoka-z-5-1563811904.jpg?crop=0.668xw:1.00xh;0.141xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability", "General"));
        addExercise("Crunches", "Easy", "https://hips.hearstapps.com/hmg-prod/images/man-doing-crunches-royalty-free-image-1597163884.jpg?crop=0.668xw:1.00xh;0.332xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "General"));
        addExercise("Sit-Ups", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/sit-up.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "General"));
        addExercise("Russian Twists", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/russian-twist.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Core", "General"));
        addExercise("Bicycle Crunches", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/bicycle-crunch.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Abs", "General"));
        addExercise("Hanging Leg Raises", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/hanging-leg-raise.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core", "General"));
        addExercise("Lying Leg Raises", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/lying-leg-raise.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "General"));
        addExercise("Side Plank", "Medium", "https://experiencelife.lifetime.life/wp-content/uploads/2021/07/bid-side-plank.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Stability", "General"));
        addExercise("Ab Wheel Rollouts", "Hard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTKmvK7Jf0HMEBWGejpAUXouiDuzWJJupcH_w&s",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"));
        addExercise("Cable Woodchoppers", "Medium", "https://cdn.shopify.com/s/files/1/0536/1244/5846/files/kneeling_cable_woodchopper_1_480x480.jpg?v=1744898590",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Obliques", "Core"));
        addExercise("V-Ups", "Hard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8GXMihxnw7sBRTmkMAYruHGi-Dd3yzkbXqQ&s",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "Core"));
        addExercise("Dead Bugs", "Easy", "https://images.squarespace-cdn.com/content/v1/5a620a85d55b41e7233c5243/716fc661-adb7-4edf-914c-4b17c4f8f7e2/105-Dead-Bug-ExerciseGIF.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"));
        addExercise("Flutter Kicks", "Medium", "https://i0.wp.com/post.healthline.com/wp-content/uploads/2022/01/400x400_The_5_Best_Exercises_That_Target_the_Lower_Abs_Flutter_Kicks.gif?h=840",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core"));
        addExercise("Scissor Kicks", "Medium", "https://i.ytimg.com/vi/WoNCIBVLbgY/maxresdefault.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Lower Abs", "Core"));
        addExercise("Decline Sit-Ups", "Medium", "https://cdn.muscleandstrength.com/sites/default/files/decline-sit-up.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Abs", "Core"));
        addExercise("Toes-to-Bar", "Hard", "https://cdn.muscleandstrength.com/sites/default/files/toes-to-bar.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Abs"));
        addExercise("Pallof Press", "Medium", "https://i.ytimg.com/vi/AH_QZLm_0-s/maxresdefault.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Anti-Rotation", "Core"));
        addExercise("Hollow Body Hold", "Hard", "https://i.ytimg.com/vi/Wp4BlxcFTkE/maxresdefault.jpg",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Core", "Stability"));

        // ==========================================
        // 7. CARDIO / HIIT / ENDURANCE
        // ==========================================
        addExercise("Running", "Medium", "https://hips.hearstapps.com/hmg-prod/images/running-track-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Cardio", "General"));
        addExercise("Brisk Walking", "Easy", "https://www.verywellfit.com/thmb/b-mg6q0k-8g3-0q-1500x1000.jpg",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Low Impact"));
        addExercise("Cycling", "Medium", "https://hips.hearstapps.com/hmg-prod/images/cycling-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Cardio", "General"));
        addExercise("Spinning", "Hard", "https://hips.hearstapps.com/hmg-prod/images/spinning-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Lose Weight", "Cardio"));
        addExercise("Jump Rope", "Medium", "https://hips.hearstapps.com/hmg-prod/images/jump-rope-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "General"));
        addExercise("Burpees", "Hard", "https://hips.hearstapps.com/hmg-prod/images/burpee-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "General", "Full Body"));
        addExercise("Mountain Climbers", "Medium", "https://hips.hearstapps.com/hmg-prod/images/mountain-climber-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.CORE, Arrays.asList("Lose Weight", "Core", "General"));
        addExercise("Jumping Jacks", "Medium", "https://hips.hearstapps.com/hmg-prod/images/jumping-jack-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Boost Energy Levels", "General"));
        addExercise("High Knees", "Medium", "https://hips.hearstapps.com/hmg-prod/images/high-knees-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Cardio", "General"));
        addExercise("Butt Kicks", "Medium", "https://hips.hearstapps.com/hmg-prod/images/butt-kicks-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Cardio", "General"));
        addExercise("Box Jumps", "Hard", "https://hips.hearstapps.com/hmg-prod/images/box-jump-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Explosive", "General"));
        addExercise("Kettlebell Swings", "Medium", "https://hips.hearstapps.com/hmg-prod/images/kettlebell-swing-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Hips", "General"));
        addExercise("Rowing Machine", "Hard", "https://hips.hearstapps.com/hmg-prod/images/rowing-machine-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "Increase Endurance", "General"));
        addExercise("Stair Climber", "Medium", "https://hips.hearstapps.com/hmg-prod/images/stair-climber-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Lose Weight", "Endurance"));
        addExercise("Battle Ropes", "Hard", "https://hips.hearstapps.com/hmg-prod/images/battle-ropes-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.ARMS, Arrays.asList("Full Body", "General"));
        addExercise("Farmer's Walk", "Medium", "https://hips.hearstapps.com/hmg-prod/images/farmers-walk-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Grip", "Core", "General"));
        addExercise("Bear Crawl", "Medium", "https://hips.hearstapps.com/hmg-prod/images/bear-crawl-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Boost Energy Levels", "General"));
        addExercise("Thrusters", "Hard", "https://hips.hearstapps.com/hmg-prod/images/thruster-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("General", "Crossfit"));
        addExercise("Wall Balls", "Medium", "https://hips.hearstapps.com/hmg-prod/images/wall-ball-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("General", "Cardio"));
        addExercise("Boxing", "Hard", "https://hips.hearstapps.com/hmg-prod/images/boxing-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "Cardio"));
        addExercise("Kickboxing", "Hard", "https://hips.hearstapps.com/hmg-prod/images/kickboxing-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.FULL_BODY, Arrays.asList("Lose Weight", "Cardio"));
        addExercise("Swimming", "Medium", "https://hips.hearstapps.com/hmg-prod/images/swimming-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Endurance"));
        addExercise("Hiking", "Medium", "https://hips.hearstapps.com/hmg-prod/images/hiking-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Maintain Weight", "Outdoors"));
        addExercise("Zumba", "Medium", "https://hips.hearstapps.com/hmg-prod/images/zumba-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Dance"));
        addExercise("Elliptical Machine", "Medium", "https://hips.hearstapps.com/hmg-prod/images/elliptical-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.LEGS, Arrays.asList("Maintain Weight", "Low Impact"));
        addExercise("Water Aerobics", "Easy", "https://hips.hearstapps.com/hmg-prod/images/water-aerobics-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Maintain Weight", "Low Impact"));
        addExercise("Sprint Intervals", "Hard", "https://hips.hearstapps.com/hmg-prod/images/sprint-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.HIIT, Exercise.BodyPart.LEGS, Arrays.asList("Lose Weight", "Speed"));
        addExercise("Clean and Jerk", "Hard", "https://hips.hearstapps.com/hmg-prod/images/clean-and-jerk-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("Gain Weight", "Power", "General"));
        addExercise("Snatch", "Hard", "https://hips.hearstapps.com/hmg-prod/images/snatch-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.FULL_BODY, Arrays.asList("General", "Power"));

        // ==========================================
        // 8. YOGA / FLEXIBILITY / ACTIVE RECOVERY
        // ==========================================
        addExercise("Superman", "Easy", "https://hips.hearstapps.com/hmg-prod/images/superman-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Lower Back", "General"));
        addExercise("Bird Dog", "Easy", "https://hips.hearstapps.com/hmg-prod/images/bird-dog-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.CORE, Arrays.asList("Core", "Balance", "General"));
        addExercise("Fire Hydrants", "Easy", "https://hips.hearstapps.com/hmg-prod/images/fire-hydrant-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "Hips", "General"));
        addExercise("Donkey Kicks", "Easy", "https://hips.hearstapps.com/hmg-prod/images/donkey-kick-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Glutes", "General"));
        addExercise("Step-Ups", "Medium", "https://hips.hearstapps.com/hmg-prod/images/step-up-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.LEGS, Arrays.asList("Legs", "Glutes", "General"));
        addExercise("Tai Chi", "Easy", "https://hips.hearstapps.com/hmg-prod/images/tai-chi-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Balance"));
        addExercise("Light Yoga Flow", "Easy", "https://hips.hearstapps.com/hmg-prod/images/yoga-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Flexibility"));
        addExercise("Morning Stretching", "Easy", "https://hips.hearstapps.com/hmg-prod/images/stretching-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Flexibility"));
        addExercise("Foam Rolling", "Easy", "https://hips.hearstapps.com/hmg-prod/images/foam-rolling-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Stay Active", "Recovery"));
        addExercise("Vinyasa Yoga", "Medium", "https://hips.hearstapps.com/hmg-prod/images/vinyasa-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Strength"));
        addExercise("Yin Yoga", "Easy", "https://hips.hearstapps.com/hmg-prod/images/yin-yoga-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Relaxation"));
        addExercise("Pilates", "Medium", "https://hips.hearstapps.com/hmg-prod/images/pilates-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.STRENGTH, Exercise.BodyPart.CORE, Arrays.asList("Improve Flexibility", "Core"));
        addExercise("Dynamic Warm-ups", "Easy", "https://hips.hearstapps.com/hmg-prod/images/warm-up-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.CARDIO, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Warmup"));
        addExercise("Static Stretching", "Easy", "https://hips.hearstapps.com/hmg-prod/images/static-stretching-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Improve Flexibility", "Cooldown"));
        addExercise("Pigeon Pose", "Easy", "https://hips.hearstapps.com/hmg-prod/images/pigeon-pose-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.LEGS, Arrays.asList("Improve Flexibility", "Hips"));
        addExercise("Cat-Cow Stretches", "Easy", "https://hips.hearstapps.com/hmg-prod/images/cat-cow-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.BACK, Arrays.asList("Improve Flexibility", "Spine"));
        addExercise("Sun Salutations", "Medium", "https://hips.hearstapps.com/hmg-prod/images/sun-salutation-1579275753.jpg?crop=0.668xw:1.00xh;0.166xw,0&resize=640:*",
                Exercise.Category.YOGA, Exercise.BodyPart.FULL_BODY, Arrays.asList("Boost Energy Levels", "Yoga"));
    }

    private static void addExercise(String title, String difficulty, String imageUrl, Exercise.Category category, Exercise.BodyPart bodyPart, List<String> muscleTargets) {
        // We join the muscleTargets + difficulty to make a tag string so it's searchable
        List<String> combinedTags = new ArrayList<>(muscleTargets);
        combinedTags.add(difficulty);
        String tagsString = String.join(", ", combinedTags);

        allExercises.put(title, new Exercise(title, tagsString, imageUrl, category, bodyPart, muscleTargets));
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
                    new ArrayList<>(template.getMuscleTargets())
            );
            newExercise.setSets(sets);
            newExercise.setReps(reps);
            newExercise.setKg(kg);
            newExercise.setAddedToWorkout(true);
            return newExercise;
        }
        return null;
    }
}