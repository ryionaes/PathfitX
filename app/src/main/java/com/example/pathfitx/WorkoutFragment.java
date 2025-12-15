package com.example.pathfitx;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutFragment extends Fragment implements WorkoutAdapter.OnExerciseInteractionListener, ExerciseDetailDialog.OnAddExerciseListener {

    private static final String TAG = "WorkoutFragment";

    // UI
    private RecyclerView rvExplore;
    private WorkoutAdapter adapter;
    private EditText etSearch;
    private Button btnStrength, btnCardio, btnHiit, btnYoga;
    private Button btnBodyAll, btnChest, btnBack, btnLegs, btnShoulders, btnArms, btnCore;
    private HorizontalScrollView secondaryFilterScrollView;

    // Data
    private List<Exercise> allExercises;
    private Exercise.Category currentCategory = Exercise.Category.STRENGTH;
    private Exercise.BodyPart currentBodyPart = null;

    // Firebase & State
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private String selectedDate; // ISO-8601 formatted date string

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
            // If loading info fails (e.g., user not logged in), stop further setup.
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
        // 1. Get User ID from Firebase Auth
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e(TAG, "User is not logged in. Cannot save workouts.");
            Toast.makeText(getContext(), "Error: You must be logged in.", Toast.LENGTH_LONG).show();
            // Optional: Navigate to login screen
            // getParentFragmentManager().popBackStack();
            return false;
        }

        // 2. Get Selected Date from Arguments
        if (getArguments() != null && getArguments().getString("selectedDate") != null) {
            selectedDate = getArguments().getString("selectedDate");
        } else {
            // Fallback to today if no date is passed (should not happen in normal flow)
            selectedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            Log.w(TAG, "No selectedDate argument found. Falling back to today.");
        }
        return true;
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
            Log.e(TAG, "User ID or Selected Date is null. Aborting save.");
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
                Map<String, Object> updates = new HashMap<>();
                updates.put("exercises", FieldValue.arrayUnion(exercise));
                updates.put("workoutName", "Custom Plan"); // Adding an exercise always makes it a custom plan

                if (document.exists()) {
                    // Document for this day exists, just update it
                    workoutDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> handleSuccess(exercise.getTitle(), callback))
                            .addOnFailureListener(e -> handleFailure(e, callback));
                } else {
                    // No workout for this day, create a new one
                    updates.put("isRestDay", false);
                    updates.put("time", "45 min"); // Sensible default
                    updates.put("equipment", "With Equipment"); // Sensible default
                    workoutDocRef.set(updates)
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


    // --- UI and Filter Logic (Mostly Unchanged) ---

    private void initViews(View view) {
        rvExplore = view.findViewById(R.id.rv_explore);
        etSearch = view.findViewById(R.id.et_search);
        btnStrength = view.findViewById(R.id.btn_strength);
        btnCardio = view.findViewById(R.id.btn_cardio);
        btnHiit = view.findViewById(R.id.btn_hiit);
        btnYoga = view.findViewById(R.id.btn_yoga);
        secondaryFilterScrollView = view.findViewById(R.id.scroll_chips_secondary);
        btnBodyAll = view.findViewById(R.id.btn_body_all);
        btnChest = view.findViewById(R.id.btn_chest);
        btnBack = view.findViewById(R.id.btn_back);
        btnLegs = view.findViewById(R.id.btn_legs);
        btnShoulders = view.findViewById(R.id.btn_shoulders);
        btnArms = view.findViewById(R.id.btn_arms);
        btnCore = view.findViewById(R.id.btn_core);
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
        btnStrength.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.STRENGTH, btnStrength));
        btnCardio.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.CARDIO, btnCardio));
        btnHiit.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.HIIT, btnHiit));
        btnYoga.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.YOGA, btnYoga));
        btnBodyAll.setOnClickListener(v -> updateBodyPartFilter(null, btnBodyAll));
        btnChest.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.CHEST, btnChest));
        btnBack.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.BACK, btnBack));
        btnLegs.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.LEGS, btnLegs));
        btnShoulders.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.SHOULDERS, btnShoulders));
        btnArms.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.ARMS, btnArms));
        btnCore.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.CORE, btnCore));
        updateCategoryFilter(Exercise.Category.STRENGTH, btnStrength);
    }

    private void updateCategoryFilter(Exercise.Category category, Button selectedButton) {
        currentCategory = category;
        currentBodyPart = null;
        updateButtonStyles(new Button[]{btnStrength, btnCardio, btnHiit, btnYoga}, selectedButton);
        secondaryFilterScrollView.setVisibility(category == Exercise.Category.STRENGTH ? View.VISIBLE : View.GONE);
        if (category == Exercise.Category.STRENGTH) {
            updateBodyPartFilter(null, btnBodyAll);
        }
        applyFilters();
    }

    private void updateBodyPartFilter(Exercise.BodyPart bodyPart, Button selectedButton) {
        currentBodyPart = bodyPart;
        updateButtonStyles(new Button[]{btnBodyAll, btnChest, btnBack, btnLegs, btnShoulders, btnArms, btnCore}, selectedButton);
        applyFilters();
    }

    private void applyFilters() {
        String searchText = etSearch.getText().toString().toLowerCase().trim();
        List<Exercise> filteredList = new ArrayList<>();
        for (Exercise exercise : allExercises) {
            boolean categoryMatch = exercise.getCategory() == currentCategory;
            boolean bodyPartMatch = (currentBodyPart == null) || (exercise.getBodyPart() == currentBodyPart);
            boolean searchMatch = searchText.isEmpty() || exercise.getTitle().toLowerCase().contains(searchText);
            if (categoryMatch && bodyPartMatch && searchMatch) {
                filteredList.add(exercise);
            }
        }
        adapter.setExercises(filteredList);
    }

    private void updateButtonStyles(Button[] buttons, Button selectedButton) {
        for (Button button : buttons) {
            if (button == selectedButton) {
                button.setBackgroundColor(Color.parseColor("#D32F2F"));
                button.setTextColor(Color.WHITE);
            } else {
                button.setBackgroundColor(Color.WHITE);
                button.setTextColor(Color.BLACK);
            }
        }
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        ExerciseDetailDialog dialog = new ExerciseDetailDialog(exercise);
        dialog.show(getChildFragmentManager(), "ExerciseDetailDialog");
    }
}
