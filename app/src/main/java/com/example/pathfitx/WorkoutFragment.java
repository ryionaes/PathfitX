package com.example.pathfitx;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutFragment extends Fragment implements WorkoutAdapter.OnExerciseInteractionListener, ExerciseDetailDialog.OnAddExerciseListener {

    private static final String TAG = "WorkoutFragment";
    private static final String ARG_SELECTED_DATE = "selectedDate";

    // UI
    private RecyclerView rvExplore;
    private WorkoutAdapter adapter;
    private EditText etSearch;
    private ChipGroup chipGroupCategory; // Primary chips
    private ChipGroup chipGroupFilters; // Secondary chips
    private HorizontalScrollView secondaryFilterScrollView;

    // Primary Category Chips
    private Chip chipAll, chipMuscle, chipGoals;

    // Data
    private List<Exercise> allExercises;

    // Filter State
    private enum FilterType { ALL, MUSCLE, GOALS }
    private FilterType currentFilterType = FilterType.ALL;
    private String currentSecondaryFilter = null; // Stores "Chest", "Lose Weight", etc.

    // Lists for secondary filters
    private final List<String> muscleList = Arrays.asList("Legs", "Chest", "Core", "Arms", "Shoulder", "Back");
    private List<String> userGoalList = new ArrayList<>();

    // Firebase & State
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private String selectedDate; // ISO-8601 formatted date string

    // Callback for generic operations
    public interface GeneralCallback {
        void onComplete(boolean success);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static WorkoutFragment newInstance(String selectedDate) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_DATE, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFirebase();
        initViews(view);

        if (!loadUserAndDateInfo()) {
            return;
        }

        setupList();
        setupSearch();
        setupCategoryFilters();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean loadUserAndDateInfo() {
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e(TAG, "User is not logged in. Cannot save workouts.");
            Toast.makeText(getContext(), "Error: You must be logged in.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (getArguments() != null && getArguments().getString(ARG_SELECTED_DATE) != null) {
            selectedDate = getArguments().getString(ARG_SELECTED_DATE);
        } else {
            selectedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setRestDay(boolean isRest, final GeneralCallback callback) {
        if (userId == null || selectedDate == null) {
            if (callback != null) callback.onComplete(false);
            return;
        }

        final DocumentReference workoutDocRef = db.collection("users").document(userId).collection("workouts").document(selectedDate);
        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("isRestDay", isRest);

        if (isRest) {
            // If it's a rest day, we can clear out the exercises
            workoutData.put("exercises", new ArrayList<>());
            workoutData.put("workoutName", "Rest Day");
        } else {
            // If it's not a rest day, we don't want to remove existing exercises unless specified
            workoutData.put("workoutName", "Custom Plan");
        }

        workoutDocRef.get().addOnCompleteListener(task -> {
            if (getContext() == null || !isAdded()) {
                if (callback != null) callback.onComplete(false);
                return;
            }

            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().exists()) {
                    workoutDocRef.update(workoutData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), isRest ? "Marked as Rest Day" : "Workout day activated", Toast.LENGTH_SHORT).show();
                                if (callback != null) callback.onComplete(true);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update day.", Toast.LENGTH_SHORT).show();
                                if (callback != null) callback.onComplete(false);
                            });
                } else {
                    // if it does not exist, create a new one
                    workoutDocRef.set(workoutData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), isRest ? "Marked as Rest Day" : "Workout day activated", Toast.LENGTH_SHORT).show();
                                if (callback != null) callback.onComplete(true);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to set day.", Toast.LENGTH_SHORT).show();
                                if (callback != null) callback.onComplete(false);
                            });
                }
            } else {
                Toast.makeText(getContext(), "Failed to access day.", Toast.LENGTH_SHORT).show();
                if (callback != null) callback.onComplete(false);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAddExercise(Exercise exercise) {
        addExerciseToWorkout(exercise, success -> {});
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onExerciseAdd(Exercise exercise, WorkoutAdapter.AddExerciseCallback callback) {
        addExerciseToWorkout(exercise, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addExerciseToWorkout(final Exercise exercise, final WorkoutAdapter.AddExerciseCallback callback) {
        if (userId == null || selectedDate == null) {
            callback.onResult(false);
            return;
        }

        final DocumentReference workoutDocRef = db.collection("users").document(userId).collection("workouts").document(selectedDate);

        workoutDocRef.get().addOnCompleteListener(task -> {
            if (getContext() == null || !isAdded()) {
                callback.onResult(false);
                return;
            }

            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    boolean isRestDay = document.getBoolean("isRestDay") != null && document.getBoolean("isRestDay");
                    if (isRestDay) {
                        Toast.makeText(getContext(), "Cannot add workouts to a rest day.", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                        return;
                    }

                    List<HashMap<String, Object>> exercises = (List<HashMap<String, Object>>) document.get("exercises");
                    if (exercises != null) {
                        for (HashMap<String, Object> existingExercise : exercises) {
                            if (existingExercise.get("title").equals(exercise.getTitle())) {
                                Toast.makeText(getContext(), "Exercise already exists in the workout.", Toast.LENGTH_SHORT).show();
                                callback.onResult(false);
                                return;
                            }
                        }
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("exercises", FieldValue.arrayUnion(exercise));
                    updates.put("isRestDay", false);
                    updates.put("workoutName", "Custom Plan");

                    workoutDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> handleSuccess(exercise.getTitle(), callback))
                            .addOnFailureListener(e -> handleFailure(e, callback));
                } else {
                    Map<String, Object> newWorkout = new HashMap<>();
                    newWorkout.put("workoutName", "Custom Plan");
                    newWorkout.put("isRestDay", false);
                    newWorkout.put("time", "45 min");
                    newWorkout.put("equipment", "With Equipment");
                    newWorkout.put("exercises", Collections.singletonList(exercise));

                    workoutDocRef.set(newWorkout)
                            .addOnSuccessListener(aVoid -> handleSuccess(exercise.getTitle(), callback))
                            .addOnFailureListener(e -> handleFailure(e, callback));
                }
            } else {
                handleFailure(task.getException(), callback);
            }
        });
    }

    private void handleSuccess(String exerciseTitle, WorkoutAdapter.AddExerciseCallback callback) {
        if (getContext() == null || !isAdded()) return;
        Toast.makeText(getContext(), exerciseTitle + " added!", Toast.LENGTH_SHORT).show();
        callback.onResult(true);
    }

    private void handleFailure(Exception e, WorkoutAdapter.AddExerciseCallback callback) {
        if (getContext() == null || !isAdded()) return;
        Log.e(TAG, "Error saving workout", e);
        Toast.makeText(getContext(), "Failed to add exercise.", Toast.LENGTH_SHORT).show();
        callback.onResult(false);
    }

    // --- UI and Filter Logic ---

    private void initViews(View view) {
        rvExplore = view.findViewById(R.id.rv_explore);
        etSearch = view.findViewById(R.id.et_search);
        secondaryFilterScrollView = view.findViewById(R.id.scroll_chips_secondary);

        chipGroupCategory = view.findViewById(R.id.chipGroupCategory);
        chipGroupFilters = view.findViewById(R.id.chipGroupFilters);

        chipAll = view.findViewById(R.id.chip_all);
        chipMuscle = view.findViewById(R.id.chip_muscle);
        chipGoals = view.findViewById(R.id.chip_goals);
    }

    private void setupList() {
        allExercises = ExerciseDatabase.getAllExercises();
        adapter = new WorkoutAdapter(new ArrayList<>(), this);
        rvExplore.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExplore.setAdapter(adapter);
        applyFilters();
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void setupCategoryFilters() {
        chipAll.setOnClickListener(v -> setFilterType(FilterType.ALL));
        chipMuscle.setOnClickListener(v -> setFilterType(FilterType.MUSCLE));
        chipGoals.setOnClickListener(v -> setFilterType(FilterType.GOALS));

        // Initial state
        setFilterType(FilterType.ALL);
    }

    private void setFilterType(FilterType type) {
        currentFilterType = type;
        currentSecondaryFilter = null; // Reset secondary selection

        if (type == FilterType.ALL) {
            secondaryFilterScrollView.setVisibility(View.GONE);
            chipGroupFilters.removeAllViews();
            applyFilters();
        } else if (type == FilterType.MUSCLE) {
            secondaryFilterScrollView.setVisibility(View.VISIBLE);
            populateChips(muscleList);
            applyFilters();
        } else if (type == FilterType.GOALS) {
            secondaryFilterScrollView.setVisibility(View.VISIBLE);
            fetchUserGoals(); // Fetch goals from Firestore
        }
    }

    private void fetchUserGoals() {
        if (userId != null) {
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        List<String> goals = (List<String>) snapshot.get("goals");
                        if (goals != null && !goals.isEmpty()) {
                            userGoalList.clear();
                            userGoalList.addAll(goals);
                            populateChips(userGoalList);
                        } else {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "No goals set. Please set your goals in your profile.", Toast.LENGTH_LONG).show();
                            }
                            chipGroupFilters.removeAllViews();
                        }
                    }
                } else {
                     if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to fetch goals.", Toast.LENGTH_SHORT).show();
                     }
                }
                applyFilters(); // Apply filters after fetching or failing
            });
        }
    }


    private void populateChips(List<String> tags) {
        if (getContext() == null) return;

        chipGroupFilters.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String tag : tags) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_filter_chip, chipGroupFilters, false);
            chip.setText(tag);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    currentSecondaryFilter = tag;
                } else {

                    if (tag.equals(currentSecondaryFilter)) {
                        currentSecondaryFilter = null;
                    }
                }
                applyFilters();
            });

            chipGroupFilters.addView(chip);
        }
    }

    private void applyFilters() {
        String searchText = etSearch.getText().toString().toLowerCase().trim();
        List<Exercise> filteredList = new ArrayList<>();

        for (Exercise exercise : allExercises) {
            boolean matchesSearch = searchText.isEmpty() || exercise.getTitle().toLowerCase().contains(searchText);
            boolean matchesFilter = true;

            if (currentFilterType == FilterType.MUSCLE) {
                if (currentSecondaryFilter != null) {
                    matchesFilter = matchMuscle(exercise, currentSecondaryFilter);
                }
            } else if (currentFilterType == FilterType.GOALS) {
                if (currentSecondaryFilter != null) {
                    matchesFilter = matchGoal(exercise, currentSecondaryFilter);
                }
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(exercise);
            }
        }
        adapter.setExercises(filteredList);
    }

    private boolean matchMuscle(Exercise exercise, String muscle) {
        Exercise.BodyPart part = exercise.getBodyPart();
        switch (muscle) {
            case "Legs": return part == Exercise.BodyPart.LEGS;
            case "Chest": return part == Exercise.BodyPart.CHEST;
            case "Core": return part == Exercise.BodyPart.CORE;
            case "Arms": return part == Exercise.BodyPart.ARMS;
            case "Shoulder": return part == Exercise.BodyPart.SHOULDERS;
            case "Back": return part == Exercise.BodyPart.BACK;
            default: return false;
        }
    }

    private boolean matchGoal(Exercise exercise, String goal) {
        switch (goal) {
            case "Gain Muscle": return exercise.getCategory() == Exercise.Category.STRENGTH;
            case "Lose Weight": return exercise.getCategory() == Exercise.Category.HIIT || exercise.getCategory() == Exercise.Category.CARDIO;
            case "Improve Flexibility": return exercise.getCategory() == Exercise.Category.YOGA;
            case "Increase Endurance": return exercise.getCategory() == Exercise.Category.CARDIO;
            case "Boost Energy Levels": return exercise.getCategory() == Exercise.Category.CARDIO || exercise.getCategory() == Exercise.Category.YOGA;
            case "Gain Weight": return exercise.getCategory() == Exercise.Category.STRENGTH;
            case "Stay Active": return true;
            case "Maintain Weight": return true;
            default: return true;
        }
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        ExerciseDetailDialog dialog = new ExerciseDetailDialog(exercise);
        dialog.show(getChildFragmentManager(), "ExerciseDetailDialog");
    }
}
