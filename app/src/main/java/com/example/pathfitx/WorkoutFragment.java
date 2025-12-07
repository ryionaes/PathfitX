package com.example.pathfitx;

import android.content.Context;
import android.content.SharedPreferences;
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
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String KEY_SELECTED_DATE = "selected_date";
    private static final String KEY_DEFAULT_TIME = "default_time";
    private static final String KEY_DEFAULT_EQUIPMENT = "default_equipment";


    private RecyclerView rvExplore;
    private WorkoutAdapter adapter;
    private EditText etSearch;
    private Button btnStrength, btnCardio, btnHiit, btnYoga;
    private Button btnBodyAll, btnChest, btnBack, btnLegs, btnShoulders, btnArms, btnCore;
    private HorizontalScrollView secondaryFilterScrollView;

    private List<Exercise> allExercises;
    private Exercise.Category currentCategory = Exercise.Category.STRENGTH;
    private Exercise.BodyPart currentBodyPart = null; // null means all

    private FirebaseFirestore db;
    private String selectedDate;
    private final String userId = "testUser"; // Placeholder

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        initViews(view);
        loadSelectedDate();
        setupList();
        setupSearch();
        setupCategoryFilters();
    }

    private void initViews(View view) {
        rvExplore = view.findViewById(R.id.rv_explore);
        etSearch = view.findViewById(R.id.et_search);
        // Primary Filters
        btnStrength = view.findViewById(R.id.btn_strength);
        btnCardio = view.findViewById(R.id.btn_cardio);
        btnHiit = view.findViewById(R.id.btn_hiit);
        btnYoga = view.findViewById(R.id.btn_yoga);
        // Secondary Filters
        secondaryFilterScrollView = view.findViewById(R.id.scroll_chips_secondary);
        btnBodyAll = view.findViewById(R.id.btn_body_all);
        btnChest = view.findViewById(R.id.btn_chest);
        btnBack = view.findViewById(R.id.btn_back);
        btnLegs = view.findViewById(R.id.btn_legs);
        btnShoulders = view.findViewById(R.id.btn_shoulders);
        btnArms = view.findViewById(R.id.btn_arms);
        btnCore = view.findViewById(R.id.btn_core);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadSelectedDate() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedDate = prefs.getString(KEY_SELECTED_DATE, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategoryFilters() {
        // Primary Listeners
        btnStrength.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.STRENGTH, btnStrength));
        btnCardio.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.CARDIO, btnCardio));
        btnHiit.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.HIIT, btnHiit));
        btnYoga.setOnClickListener(v -> updateCategoryFilter(Exercise.Category.YOGA, btnYoga));

        // Secondary Listeners
        btnBodyAll.setOnClickListener(v -> updateBodyPartFilter(null, btnBodyAll));
        btnChest.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.CHEST, btnChest));
        btnBack.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.BACK, btnBack));
        btnLegs.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.LEGS, btnLegs));
        btnShoulders.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.SHOULDERS, btnShoulders));
        btnArms.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.ARMS, btnArms));
        btnCore.setOnClickListener(v -> updateBodyPartFilter(Exercise.BodyPart.CORE, btnCore));

        // Set Initial State
        updateCategoryFilter(Exercise.Category.STRENGTH, btnStrength);
    }

    private void updateCategoryFilter(Exercise.Category category, Button selectedButton) {
        currentCategory = category;
        currentBodyPart = null; // Reset body part filter
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onExerciseAdd(Exercise exercise, WorkoutAdapter.AddExerciseCallback callback) {
        loadSelectedDate(); // Always get the latest date
        addExerciseToWorkout(exercise, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAddExercise(Exercise exercise) {
        addExerciseToWorkout(exercise, success -> {}); // Empty callback, as it's not needed here
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addExerciseToWorkout(Exercise exercise, WorkoutAdapter.AddExerciseCallback callback) {
        loadSelectedDate(); // Always get the latest date
        exercise.setAddedToWorkout(true);
        String dateId = this.selectedDate;
        DocumentReference workoutDocRef = db.collection("users").document(userId).collection("workouts").document(dateId);

        workoutDocRef.get().addOnCompleteListener(task -> {
            if (getContext() == null || !isAdded()) {
                callback.onResult(false);
                return;
            }

            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<HashMap<String, Object>> exercisesMap = (List<HashMap<String, Object>>) document.get("exercises");
                    boolean alreadyExists = false;
                    if (exercisesMap != null) {
                        for (HashMap<String, Object> map : exercisesMap) {
                            if (map.get("title").equals(exercise.getTitle())) {
                                alreadyExists = true;
                                break;
                            }
                        }
                    }

                    if (alreadyExists) {
                        Toast.makeText(getContext(), "This exercise is already added", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                    } else {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("exercises", FieldValue.arrayUnion(exercise));
                        updates.put("workoutName", "Custom Plan");
                        workoutDocRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    if (getContext() == null || !isAdded()) return;
                                    Toast.makeText(getContext(), exercise.getTitle() + " added!", Toast.LENGTH_SHORT).show();
                                    callback.onResult(true);
                                })
                                .addOnFailureListener(e -> {
                                    if (getContext() == null || !isAdded()) return;
                                    Log.e(TAG, "Error updating workout", e);
                                    callback.onResult(false);
                                });
                    }
                } else {
                    SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String defaultTime = prefs.getString(KEY_DEFAULT_TIME, "45 min");
                    String defaultEquipment = prefs.getString(KEY_DEFAULT_EQUIPMENT, "With Equipment");

                    Map<String, Object> newWorkout = new HashMap<>();
                    newWorkout.put("workoutName", "Custom Plan");
                    newWorkout.put("isRestDay", false);
                    newWorkout.put("exercises", Arrays.asList(exercise));
                    newWorkout.put("time", defaultTime);
                    newWorkout.put("equipment", defaultEquipment);

                    workoutDocRef.set(newWorkout)
                            .addOnSuccessListener(aVoid -> {
                                if (getContext() == null || !isAdded()) return;
                                Toast.makeText(getContext(), exercise.getTitle() + " added!", Toast.LENGTH_SHORT).show();
                                callback.onResult(true);
                            })
                            .addOnFailureListener(e -> {
                                if (getContext() == null || !isAdded()) return;
                                Log.e(TAG, "Error creating new workout", e);
                                callback.onResult(false);
                            });
                }
            } else {
                Log.e(TAG, "Error getting workout document", task.getException());
                callback.onResult(false);
            }
        });
    }
}
