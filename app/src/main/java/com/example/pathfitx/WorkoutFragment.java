package com.example.pathfitx;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import java.util.stream.Collectors;

public class WorkoutFragment extends Fragment implements WorkoutAdapter.OnExerciseInteractionListener, ExerciseDetailDialog.OnAddExerciseListener {

    private static final String TAG = "WorkoutFragment";
    private static final String ARG_SELECTED_DATE = "selectedDate";

    private RecyclerView rvExplore;
    private WorkoutAdapter adapter;
    private List<Exercise> allExercises;
    private SharedViewModel sharedViewModel;
    private List<String> userGoals = new ArrayList<>();

    // UI for Filtering
    private EditText etSearch;
    private Chip chipAll, chipMuscle, chipGoals;
    private HorizontalScrollView scrollChipsSecondary;
    private ChipGroup chipGroupFilters;
    private LinearLayout chipGroupPrimary;

    private String currentMainFilter = "All";
    private String currentSubFilter = null;

    private FirebaseFirestore db;
    private String userId;
    private String selectedDate;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFirebase();
        initViews(view);
        setupViewModel();
        setupExploreList();
        setupFilterListeners();
        setupSearchListener();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        selectedDate = (getArguments() != null) ? getArguments().getString(ARG_SELECTED_DATE) :
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private void initViews(View view) {
        rvExplore = view.findViewById(R.id.rv_explore);
        etSearch = view.findViewById(R.id.et_search);
        chipAll = view.findViewById(R.id.chip_all);
        chipMuscle = view.findViewById(R.id.chip_muscle);
        chipGoals = view.findViewById(R.id.chip_goals);
        scrollChipsSecondary = view.findViewById(R.id.scroll_chips_secondary);
        chipGroupFilters = view.findViewById(R.id.chipGroupFilters);
        chipGroupPrimary = view.findViewById(R.id.chipGroupCategory);
    }

    private void setupViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUserSnapshot().observe(getViewLifecycleOwner(), snapshot -> {
            if (snapshot != null && snapshot.contains("goals")) {
                List<String> loadedGoals = (List<String>) snapshot.get("goals");
                if (loadedGoals != null) {
                    userGoals = loadedGoals;
                    if ("By Goals".equals(currentMainFilter)) {
                        populateSecondaryFilters(userGoals);
                        filterExercises();
                    }
                }
            }
        });
    }

    private void setupExploreList() {
        allExercises = ExerciseDatabase.getAllExercises();
        adapter = new WorkoutAdapter(new ArrayList<>(allExercises), this);
        rvExplore.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExplore.setAdapter(adapter);
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterExercises();
            }
        });
    }

    private void setupFilterListeners() {
        View.OnClickListener primaryFilterClickListener = v -> {
            chipAll.setChecked(v.getId() == R.id.chip_all);
            chipMuscle.setChecked(v.getId() == R.id.chip_muscle);
            chipGoals.setChecked(v.getId() == R.id.chip_goals);

            if (v.getId() == R.id.chip_all) {
                currentMainFilter = "All";
                scrollChipsSecondary.setVisibility(View.GONE);
                currentSubFilter = null;
                chipGroupFilters.clearCheck();
            } else if (v.getId() == R.id.chip_muscle) {
                currentMainFilter = "By Muscle";
                List<String> muscles = Arrays.stream(Exercise.BodyPart.values()).map(Enum::name).collect(Collectors.toList());
                populateSecondaryFilters(muscles);
                scrollChipsSecondary.setVisibility(View.VISIBLE);
            } else if (v.getId() == R.id.chip_goals) {
                currentMainFilter = "By Goals";
                populateSecondaryFilters(userGoals);
                scrollChipsSecondary.setVisibility(View.VISIBLE);
            }
            filterExercises();
        };

        chipAll.setOnClickListener(primaryFilterClickListener);
        chipMuscle.setOnClickListener(primaryFilterClickListener);
        chipGoals.setOnClickListener(primaryFilterClickListener);

        chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                currentSubFilter = null;
            } else {
                Chip checkedChip = group.findViewById(checkedId);
                if (checkedChip != null) {
                    currentSubFilter = checkedChip.getText().toString();
                }
            }
            filterExercises();
        });
    }

    private void populateSecondaryFilters(List<String> filters) {
        chipGroupFilters.removeAllViews();
        currentSubFilter = null;
        if (filters == null || filters.isEmpty()) {
            updateChipGroupAlignment();
            return;
        }

        ContextThemeWrapper themedContext = new ContextThemeWrapper(getContext(), R.style.SecondaryFilterChip);

        for (String filterName : filters) {
            Chip chip = new Chip(themedContext);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.drawable.selector_chip_bg);
            chip.setTextColor(ContextCompat.getColorStateList(getContext(), R.drawable.selector_chip_text));
            chip.setChipCornerRadius(getResources().getDimension(R.dimen.secondary_chip_corner_radius));
            chip.setChipMinHeight(getResources().getDimension(R.dimen.secondary_chip_min_height));
            chip.setChipStrokeWidth(0f);
            chip.setText(toTitleCase(filterName));
            chipGroupFilters.addView(chip);
        }

        updateChipGroupAlignment();
    }

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String[] words = text.split("[_\\s]+");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return titleCase.toString().trim();
    }

    private void filterExercises() {
        String searchQuery = etSearch.getText().toString().toLowerCase().trim();
        List<Exercise> filteredList = new ArrayList<>();

        for (Exercise exercise : allExercises) {
            boolean matchesSearch = searchQuery.isEmpty() || exercise.getTitle().toLowerCase().contains(searchQuery);
            boolean matchesFilter = false;

            switch (currentMainFilter) {
                case "All":
                    matchesFilter = true;
                    break;
                case "By Muscle":
                    if (currentSubFilter == null) {
                        matchesFilter = true;
                    } else if (exercise.getBodyPart() != null) {
                        String muscleAsEnum = currentSubFilter.trim().replace(" ", "_").toUpperCase();
                        matchesFilter = exercise.getBodyPart().name().equals(muscleAsEnum);
                    }
                    break;
                case "By Goals":
                    if (currentSubFilter == null) {
                        matchesFilter = true;
                    } else if (exercise.getMuscleTargets() != null) {
                        for (String target : exercise.getMuscleTargets()) {
                            if (target.equalsIgnoreCase(currentSubFilter)) {
                                matchesFilter = true;
                                break;
                            }
                        }
                    }
                    break;
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(exercise);
            }
        }
        adapter.updateList(filteredList);
    }

    @Override
    public void onAddExercise(Exercise exercise) {
        addExerciseToWorkout(exercise, success -> {});
    }

    @Override
    public void onExerciseAdd(Exercise exercise, WorkoutAdapter.AddExerciseCallback callback) {
        addExerciseToWorkout(exercise, callback);
    }

    private Map<String, Object> exerciseToMap(Exercise exercise) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", exercise.getTitle());
        map.put("sets", exercise.getSets());
        map.put("reps", exercise.getReps());
        map.put("kg", exercise.getKg());
        map.put("category", exercise.getCategory() != null ? exercise.getCategory().name() : null);
        map.put("bodyPart", exercise.getBodyPart() != null ? exercise.getBodyPart().name() : null);
        map.put("muscleTargets", exercise.getMuscleTargets());
        map.put("imageUrl", exercise.getImageUrl());
        map.put("imageResId", exercise.getImageResId());
        map.put("met", exercise.getMet());
        map.put("addedToWorkout", exercise.isAddedToWorkout());
        return map;
    }

    private void addExerciseToWorkout(final Exercise exercise, final WorkoutAdapter.AddExerciseCallback callback) {
        if (userId == null || selectedDate == null) {
            Toast.makeText(getContext(), "Error: Cannot add exercise.", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
            return;
        }

        if (exercise.getSets() == 0) exercise.setSets(3);
        if (exercise.getReps() == 0) exercise.setReps(10);

        final DocumentReference workoutDocRef = db.collection("users").document(userId).collection("workouts").document(selectedDate);

        workoutDocRef.get().addOnCompleteListener(task -> {
            if (!isAdded() || !task.isSuccessful()) {
                handleFailure(task.getException(), callback);
                return;
            }

            DocumentSnapshot document = task.getResult();
            Map<String, Object> exerciseMap = exerciseToMap(exercise);

            if (document != null && document.exists()) {
                if (Boolean.TRUE.equals(document.getBoolean("isRestDay"))) {
                    Toast.makeText(getContext(), "Cannot add exercises on a rest day.", Toast.LENGTH_SHORT).show();
                    callback.onResult(false);
                    return;
                }

                List<Map<String, Object>> existingExercises = (List<Map<String, Object>>) document.get("exercises");
                if (existingExercises != null) {
                    for (Map<String, Object> existingMap : existingExercises) {
                        if (exercise.getTitle().equals(existingMap.get("title"))) {
                            Toast.makeText(getContext(), exercise.getTitle() + " already exists.", Toast.LENGTH_SHORT).show();
                            callback.onResult(false);
                            return;
                        }
                    }
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("exercises", FieldValue.arrayUnion(exerciseMap));
                updates.put("workoutName", "Custom Plan");
                workoutDocRef.update(updates)
                        .addOnSuccessListener(aVoid -> handleSuccess(exercise.getTitle(), callback))
                        .addOnFailureListener(e -> handleFailure(e, callback));

            } else {
                SharedPreferences prefs = requireActivity().getSharedPreferences(HomeFragment.PREFS_NAME, Context.MODE_PRIVATE);
                String defaultTime = prefs.getString(HomeFragment.KEY_DEFAULT_TIME, "45 min");

                Map<String, Object> newWorkout = new HashMap<>();
                newWorkout.put("workoutName", "Custom Plan");
                newWorkout.put("isRestDay", false);
                newWorkout.put("time", defaultTime);
                newWorkout.put("equipment", "With Equipment");
                newWorkout.put("exercises", Collections.singletonList(exerciseMap));

                workoutDocRef.set(newWorkout)
                        .addOnSuccessListener(aVoid -> handleSuccess(exercise.getTitle(), callback))
                        .addOnFailureListener(e -> handleFailure(e, callback));
            }
        });
    }

    private void handleSuccess(String title, WorkoutAdapter.AddExerciseCallback callback) {
        if (getContext() != null) Toast.makeText(getContext(), title + " added!", Toast.LENGTH_SHORT).show();
        callback.onResult(true);
    }

    private void handleFailure(Exception e, WorkoutAdapter.AddExerciseCallback callback) {
        Log.e(TAG, "Error modifying workout", e);
        if (getContext() != null) Toast.makeText(getContext(), "Failed to modify workout.", Toast.LENGTH_SHORT).show();
        callback.onResult(false);
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        ExerciseDetailDialog dialog = new ExerciseDetailDialog(exercise);
        dialog.show(getChildFragmentManager(), "ExerciseDetailDialog");
    }

    private void updateChipGroupAlignment() {
        HorizontalScrollView scrollView = getView().findViewById(R.id.scroll_chips_secondary);
        ChipGroup chipGroup = getView().findViewById(R.id.chipGroupFilters);

        chipGroup.post(() -> {
            int chipGroupWidth = chipGroup.getWidth();
            int scrollViewWidth = scrollView.getWidth();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) chipGroup.getLayoutParams();
            if (chipGroupWidth < scrollViewWidth) {
                params.gravity = Gravity.CENTER_HORIZONTAL;
            } else {
                params.gravity = Gravity.START;
            }
            chipGroup.setLayoutParams(params);
        });
    }
}