package com.example.pathfitx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri; // Added for Image
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Added Glide
import com.bumptech.glide.signature.ObjectKey; // Added Signature for refresh
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment implements ExerciseAdapter.OnItemClickListener, EditExerciseDialog.DialogListener, CalendarAdapter.OnDateClickListener, OptionsBottomSheetFragment.OnOptionSelectedListener, SwapWorkoutBottomSheetFragment.OnOptionSelectedListener {

    private static final String TAG = "HomeFragment";
    private static final String PREFS_NAME = "WorkoutPrefs"; // For Calendar/Workouts
    private static final String USER_PREFS_NAME = "UserPrefs"; // NEW: For Profile Data
    private static final String KEY_DEFAULT_TIME = "default_time";
    private static final String KEY_DEFAULT_EQUIPMENT = "default_equipment";
    private static final String KEY_SELECTED_DATE = "selected_date";
    private static final boolean UPLOAD_TEMPLATES_ON_START = true;

    private RecyclerView rvCalendar;
    private RecyclerView rvExercises;
    private CalendarAdapter calendarAdapter;

    // UI Variables
    private TextView tvYear, tvUserName; // Added tvUserName
    private TextView tvTimeOption, tvEquipmentOption, tvWorkoutTitle, tvRestDayMessage, tvExerciseCount, tvWorkoutSubtitle;
    private ImageView ivProfileIcon;
    private SwitchMaterial switchRestDay;
    private MaterialButton btnSwap, btnAddExercise, btnStartWorkout;
    private Group workoutDetailsGroup;
    private View restDayContainer;
    private View restDayCard;

    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;
    private FirebaseFirestore db;
    private LocalDate selectedDate;
    private final String userId = "testUser"; // Placeholder

    private ArrayList<String> timeOptions = new ArrayList<>(Arrays.asList("15 min", "30 min", "45 min", "1 hr", "1 hr, 30 min"));
    private ArrayList<String> equipmentOptions = new ArrayList<>(Arrays.asList("With Equipment", "No Equipment"));
    private String selectedTime;
    private String selectedEquipment;
    private String selectedWorkout;
    private ArrayList<WorkoutType> workoutTypes = new ArrayList<>();
    private boolean isRestDay = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        initViews(view);
        loadSavedDate();
        loadDefaultOptions();

        // NEW: Load Profile Data (Name & Picture)
        loadUserProfile();

        tvYear.setText(String.valueOf(selectedDate.getYear()));

        if (UPLOAD_TEMPLATES_ON_START) {
            uploadWorkoutTemplatesToFirestore();
        }

        setupCalendar();
        setupOptionPickers();
        setupWorkoutTypes();
        setupExercises();
        setupClickListeners();
    }

    // --- UPDATED: onResume to refresh Profile Data ---
    @Override
    public void onResume() {
        super.onResume();

        // 1. Load Profile Data (Updates Name & Picture immediately)
        loadUserProfile();

        // 2. Load Calendar/Workout Data
        loadSavedDate();
        int newPosition = calendarAdapter.findPositionForDate(selectedDate);
        if (newPosition != -1) {
            calendarAdapter.setSelectedPosition(newPosition);
        }
        loadWorkoutsForDate(selectedDate);
    }

    // --- NEW: Method to load Name and Picture ---
    private void loadUserProfile() {
        if (getContext() == null) return;
        SharedPreferences userPrefs = getContext().getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);

        // 1. Update Name (Replaces "saturei")
        // Make sure you have a TextView with id 'tv_user_name' in fragment_home.xml
        String username = userPrefs.getString("USERNAME", "Fitness User");
        if (tvUserName != null) {
            tvUserName.setText(username);
        }

        // 2. Update Profile Picture with Glide
        String imageUriString = userPrefs.getString("PROFILE_IMAGE_URI", null);
        if (ivProfileIcon != null) {
            if (imageUriString != null && !imageUriString.isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(imageUriString))
                        .circleCrop()
                        .signature(new ObjectKey(System.currentTimeMillis())) // Force refresh
                        .into(ivProfileIcon);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_launcher_foreground) // Default image
                        .circleCrop()
                        .into(ivProfileIcon);
            }
        }
    }

    private void initViews(View view) {
        rvCalendar = view.findViewById(R.id.rv_calendar);
        rvExercises = view.findViewById(R.id.rv_exercises);
        tvYear = view.findViewById(R.id.tv_year);

        // NEW: Initialize User Name TextView
        // IMPORTANT: Ensure your XML has android:id="@+id/tv_user_name"
        tvUserName = view.findViewById(R.id.tv_user_name);

        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        tvTimeOption = view.findViewById(R.id.tv_time_option);
        tvEquipmentOption = view.findViewById(R.id.tv_equipment_option);
        switchRestDay = view.findViewById(R.id.switch_rest_day);
        tvRestDayMessage = view.findViewById(R.id.tv_rest_day_message);
        tvWorkoutTitle = view.findViewById(R.id.tv_push_day);
        btnSwap = view.findViewById(R.id.btn_swap);
        workoutDetailsGroup = view.findViewById(R.id.workout_details_group);
        tvExerciseCount = view.findViewById(R.id.tv_exercise_count);
        tvWorkoutSubtitle = view.findViewById(R.id.tv_workout_subtitle);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        restDayContainer = view.findViewById(R.id.rest_day_container);
        restDayCard = view.findViewById(R.id.rest_day_card);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);
    }

    // ... (The rest of your code remains exactly the same below) ...

    private void loadSavedDate() {
        if(getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedDate = prefs.getString(KEY_SELECTED_DATE, null);
        if (savedDate != null) {
            selectedDate = LocalDate.parse(savedDate);
        } else {
            selectedDate = LocalDate.now();
        }
    }

    private void saveSelectedDate(LocalDate date) {
        if(getContext() == null) return;
        SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_SELECTED_DATE, date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        editor.apply();
    }

    private void loadDefaultOptions() {
        if(getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedTime = prefs.getString(KEY_DEFAULT_TIME, "45 min");
        selectedEquipment = prefs.getString(KEY_DEFAULT_EQUIPMENT, "With Equipment");
        selectedWorkout = "Push Day"; // Default workout
    }

    private void setupOptionPickers() {
        tvTimeOption.setText(selectedTime);
        tvEquipmentOption.setText(selectedEquipment);
        tvTimeOption.setOnClickListener(v -> showOptionsBottomSheet("time"));
        tvEquipmentOption.setOnClickListener(v -> showOptionsBottomSheet("equipment"));
    }

    private void setupWorkoutTypes() {
        workoutTypes.clear();
        workoutTypes.add(new WorkoutType("Push Day", "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=2070&auto=format&fit=crop"));
        workoutTypes.add(new WorkoutType("Pull Day", "https://images.unsplash.com/photo-1598971639058-211a74a96aea?q=80&w=2070&auto=format&fit=crop"));
        workoutTypes.add(new WorkoutType("Leg Day", "https://images.unsplash.com/photo-1574680096145-d05b474e2155?q=80&w=2069&auto=format&fit=crop"));
        workoutTypes.add(new WorkoutType("Upper Body", "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?q=80&w=2070&auto=format&fit=crop"));
        workoutTypes.add(new WorkoutType("Cardio & Core", "https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=2069&auto=format&fit=crop"));
        workoutTypes.add(new WorkoutType("Custom Plan", "https://plus.unsplash.com/premium_photo-1664109999537-088e7d96448d?q=80&w=1974&auto=format&fit=crop"));
        tvWorkoutTitle.setText(selectedWorkout);
    }

    private void showOptionsBottomSheet(String type) {
        boolean isTime = type.equals("time");
        String title = isTime ? "Workout duration" : "Equipment";
        ArrayList<String> options = isTime ? timeOptions : equipmentOptions;
        String selected = isTime ? selectedTime : selectedEquipment;

        OptionsBottomSheetFragment bottomSheet = OptionsBottomSheetFragment.newInstance(title, options, selected);
        bottomSheet.setOnOptionSelectedListener(this);
        bottomSheet.show(getParentFragmentManager(), "OptionsBottomSheet");
    }

    private void showSwapWorkoutBottomSheet(){
        SwapWorkoutBottomSheetFragment bottomSheet = SwapWorkoutBottomSheetFragment.newInstance("Swap Workout", workoutTypes, selectedWorkout);
        bottomSheet.setOnOptionSelectedListener(this);
        bottomSheet.show(getParentFragmentManager(), "SwapWorkoutBottomSheet");
    }

    @Override
    public void onOptionSelected(String option) {
        if (timeOptions.contains(option)) {
            selectedTime = option;
            tvTimeOption.setText(selectedTime);
            saveWorkoutsForDate(selectedDate);
        } else if (equipmentOptions.contains(option)) {
            selectedEquipment = option;
            tvEquipmentOption.setText(selectedEquipment);
            saveWorkoutsForDate(selectedDate);
        } else {
            selectedWorkout = option;
            tvWorkoutTitle.setText(selectedWorkout);
            swapWorkout(option);
        }
    }

    private void swapWorkout(String workoutName) {
        if ("Custom Plan".equals(workoutName)) {
            return;
        }
        isRestDay = false;
        selectedWorkout = workoutName;

        db.collection("workout_templates").document(workoutName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<HashMap<String, Object>> exercisesMap = (List<HashMap<String, Object>>) documentSnapshot.get("exercises");
                        exerciseList.clear();
                        exerciseList.addAll(parseExercisesFromMap(exercisesMap));
                        saveWorkoutsForDate(selectedDate);
                        updateUI();
                    } else {
                        Log.e(TAG, "Workout template not found: " + workoutName);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting workout template", e));
    }

    @Override
    public void onSetAsDefault(String option) {
        SharedPreferences.Editor editor = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        if (timeOptions.contains(option)) {
            selectedTime = option;
            tvTimeOption.setText(selectedTime);
            editor.putString(KEY_DEFAULT_TIME, selectedTime);
        } else {
            selectedEquipment = option;
            tvEquipmentOption.setText(selectedEquipment);
            editor.putString(KEY_DEFAULT_EQUIPMENT, selectedEquipment);
        }
        editor.apply();
        saveWorkoutsForDate(selectedDate);
    }

    private void setupClickListeners() {
        ivProfileIcon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();

            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_profile);
            }
        });

        btnAddExercise.setOnClickListener(v -> {
            if (!"Custom Plan".equals(selectedWorkout)) {
                selectedWorkout = "Custom Plan";
            }

            WorkoutFragment workoutFragment = new WorkoutFragment();
            Bundle args = new Bundle();
            args.putString("selectedDate", selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            workoutFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, workoutFragment)
                    .addToBackStack(null)
                    .commit();

            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_workout);
            }
        });

        btnStartWorkout.setOnClickListener(v -> {
            if (exerciseList == null || exerciseList.isEmpty() || isRestDay) {
                Toast.makeText(getContext(), "No workout planned for today!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), LiveSessionActivity.class);
            intent.putExtra("exerciseList", (Serializable) exerciseList);
            startActivity(intent);
        });

        switchRestDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                isRestDay = isChecked;
                if(isRestDay){
                    exerciseList.clear();
                }
                saveWorkoutsForDate(selectedDate);
                updateUI();
            }
        });

        btnSwap.setOnClickListener(v -> showSwapWorkoutBottomSheet());
    }

    private void setupCalendar() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startPoint = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        for (int i = 0; i < 21; i++) {
            dates.add(startPoint.plusDays(i));
        }
        int todayPosition = dates.indexOf(today);
        calendarAdapter = new CalendarAdapter(dates, todayPosition, this);
        rvCalendar.setAdapter(calendarAdapter);
        if (todayPosition != -1) {
            ((LinearLayoutManager) rvCalendar.getLayoutManager()).scrollToPositionWithOffset(todayPosition, rvCalendar.getWidth() / 2);
        }
    }

    private void setupExercises() {
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(exerciseAdapter);
        loadWorkoutsForDate(selectedDate);
    }

    @Override
    public void onDateClick(int position) {
        selectedDate = calendarAdapter.getDateAt(position);
        calendarAdapter.setSelectedPosition(position);
        saveSelectedDate(selectedDate);
        loadWorkoutsForDate(selectedDate);
    }

    private List<Exercise> parseExercisesFromMap(List<HashMap<String, Object>> exercisesMap) {
        List<Exercise> exercises = new ArrayList<>();
        if (exercisesMap == null) return exercises;

        for (HashMap<String, Object> map : exercisesMap) {
            try {
                Exercise exercise = new Exercise();
                exercise.setTitle((String) map.get("title"));

                Object setsObj = map.get("sets");
                if(setsObj instanceof Long) exercise.setSets(((Long) setsObj).intValue());

                Object repsObj = map.get("reps");
                if(repsObj instanceof Long) exercise.setReps(((Long) repsObj).intValue());

                Object kgObj = map.get("kg");
                if(kgObj instanceof Long) exercise.setKg(((Long) kgObj).intValue());

                Object categoryObj = map.get("category");
                if (categoryObj instanceof String) {
                    exercise.setCategory(Exercise.Category.valueOf((String) categoryObj));
                }

                Object bodyPartObj = map.get("bodyPart");
                if (bodyPartObj instanceof String) {
                    exercise.setBodyPart(Exercise.BodyPart.valueOf((String) bodyPartObj));
                }

                Object muscleTargetsObj = map.get("muscleTargets");
                if (muscleTargetsObj instanceof List) {
                    exercise.setMuscleTargets((List<String>) muscleTargetsObj);
                }

                if (map.containsKey("imageUrl")) {
                    exercise.setImageUrl((String) map.get("imageUrl"));
                } else if (map.containsKey("imageResId")) {
                    // Fallback or ignore
                }

                exercise.setAddedToWorkout(true);
                exercises.add(exercise);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing exercise from map: " + map.toString(), e);
            }
        }
        return exercises;
    }

    private void loadWorkoutsForDate(LocalDate date) {
        String dateId = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        db.collection("users").document(userId).collection("workouts").document(dateId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        exerciseList.clear();
                        loadDefaultOptions();

                        if (document != null && document.exists()) {
                            if (document.contains("time")) selectedTime = document.getString("time");
                            if (document.contains("equipment")) selectedEquipment = document.getString("equipment");

                            isRestDay = document.getBoolean("isRestDay") != null && document.getBoolean("isRestDay");

                            if (!isRestDay) {
                                String workoutName = document.getString("workoutName");
                                selectedWorkout = workoutName != null ? workoutName : "No Workout Planned";

                                Object exercisesField = document.get("exercises");
                                if (exercisesField instanceof List && !((List) exercisesField).isEmpty()) {
                                    if("No Workout Planned".equals(selectedWorkout)){
                                        selectedWorkout = "Custom Plan";
                                    }
                                    exerciseList.addAll(parseExercisesFromMap((List<HashMap<String, Object>>) exercisesField));
                                } else {
                                    selectedWorkout = "No Workout Planned";
                                }
                            }
                        } else {
                            isRestDay = false;
                            selectedWorkout = "No Workout Planned";
                        }
                        updateUI();
                    } else {
                        Log.e(TAG, "Error getting workouts: ", task.getException());
                    }
                });
    }

    private void saveWorkoutsForDate(LocalDate date) {
        String dateId = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("isRestDay", isRestDay);
        workoutData.put("workoutName", selectedWorkout);
        workoutData.put("exercises", exerciseList);
        workoutData.put("time", selectedTime);
        workoutData.put("equipment", selectedEquipment);

        db.collection("users").document(userId).collection("workouts").document(dateId)
                .set(workoutData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Workout saved for " + dateId))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving workout", e));
    }

    private void updateUI() {
        switchRestDay.setChecked(isRestDay);
        tvWorkoutSubtitle.setVisibility(View.GONE);
        tvExerciseCount.setVisibility(View.GONE);
        tvTimeOption.setText(selectedTime);
        tvEquipmentOption.setText(selectedEquipment);

        boolean hasWorkout = !exerciseList.isEmpty();

        if(isRestDay){
            tvWorkoutTitle.setText("Rest Day");
            tvWorkoutSubtitle.setText("Time to recover!");
            tvWorkoutSubtitle.setVisibility(View.VISIBLE);
            workoutDetailsGroup.setVisibility(View.GONE);
            btnAddExercise.setVisibility(View.GONE);
            btnSwap.setVisibility(View.GONE);
            restDayCard.setVisibility(View.GONE);
        } else if (hasWorkout) {
            tvWorkoutTitle.setText(selectedWorkout);
            int exerciseCount = exerciseList.size();
            Set<String> muscleTargets = new HashSet<>();
            for (Exercise exercise : exerciseList) {
                if (exercise.getMuscleTargets() != null) {
                    muscleTargets.addAll(exercise.getMuscleTargets());
                }
            }
            int muscleCount = muscleTargets.size();
            String muscleTargetString = muscleCount + " Muscle Target" + (muscleCount == 1 ? "" : "s");

            tvExerciseCount.setText(exerciseCount + " " + (exerciseCount == 1 ? "Exercise" : "Exercises") + " • " + muscleTargetString);
            tvExerciseCount.setVisibility(View.VISIBLE);
            btnSwap.setText("Swap");
            btnSwap.setIconResource(R.drawable.ic_swap_horiz);
            workoutDetailsGroup.setVisibility(View.VISIBLE);

            if (exerciseList.size() >= 3) {
                btnAddExercise.setVisibility(View.GONE);
            } else {
                btnAddExercise.setVisibility(View.VISIBLE);
            }

            if (exerciseList.size() >= 1) {
                restDayCard.setVisibility(View.GONE);
            } else {
                restDayCard.setVisibility(View.VISIBLE);
            }

            btnSwap.setVisibility(View.VISIBLE);
            btnStartWorkout.setVisibility(View.VISIBLE);
        } else { // Empty Day
            tvWorkoutTitle.setText("No Workout Planned");
            tvWorkoutSubtitle.setText("Start your path to fitness today!");
            tvWorkoutSubtitle.setVisibility(View.VISIBLE);
            btnSwap.setText("Plans");
            btnSwap.setIconResource(android.R.drawable.ic_dialog_info);
            workoutDetailsGroup.setVisibility(View.GONE);
            btnAddExercise.setVisibility(View.VISIBLE);
            btnSwap.setVisibility(View.VISIBLE);
            restDayCard.setVisibility(View.VISIBLE);
            btnStartWorkout.setVisibility(View.GONE);
        }

        if (exerciseAdapter != null) {
            exerciseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMoreClick(Exercise exercise, int position) {
        EditExerciseDialog dialog = new EditExerciseDialog(exercise, position, this);
        dialog.show(getChildFragmentManager(), "EditExerciseDialog");
    }

    @Override
    public void onSave(Exercise updatedExercise, int position) {
        exerciseList.set(position, updatedExercise);
        saveWorkoutsForDate(selectedDate);
        updateUI();
    }

    @Override
    public void onRemove(Exercise exerciseToRemove, int position) {
        exerciseList.remove(position);
        saveWorkoutsForDate(selectedDate);
        updateUI();
    }

    private void uploadWorkoutTemplatesToFirestore() {
        Log.d(TAG, "Attempting to upload workout templates...");
        Map<String, List<Exercise>> templates = new HashMap<>();
        templates.put("Push Day", WorkoutTemplates.getPushDay());
        templates.put("Pull Day", WorkoutTemplates.getPullDay());
        templates.put("Leg Day", WorkoutTemplates.getLegDay());
        templates.put("Upper Body", WorkoutTemplates.getUpperBody());
        templates.put("Cardio & Core", WorkoutTemplates.getCardioCore());

        for (Map.Entry<String, List<Exercise>> entry : templates.entrySet()) {
            String templateName = entry.getKey();
            List<Exercise> exercises = entry.getValue();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("exercises", exercises);

            db.collection("workout_templates").document(templateName)
                    .set(templateData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Template '" + templateName + "' uploaded successfully."))
                    .addOnFailureListener(e -> Log.e(TAG, "Error uploading template '" + templateName + "'", e));
        }
    }
}