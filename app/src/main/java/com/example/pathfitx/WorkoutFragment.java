package com.example.pathfitx;

import androidx.core.content.ContextCompat;
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
import androidx.appcompat.view.ContextThemeWrapper;
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
                        filterExercises(); // Re-filter now that goals are loaded
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
            // Don't filter here, let the calling method do it.
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
    }

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // Split by underscores (common in DB/Enum names) or spaces
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
                        matchesFilter = true; // No sub-filter selected, show all muscles
                    } else if (exercise.getBodyPart() != null) {
                        String muscleAsEnum = currentSubFilter.trim().replace(" ", "_").toUpperCase();
                        matchesFilter = exercise.getBodyPart().name().equals(muscleAsEnum);
                    }
                    break;
                case "By Goals":
                    if (currentSubFilter == null) {
                        matchesFilter = true; // No goal selected, show all
                    } else if (exercise.getMuscleTargets() != null) {
                        // Check if the list of muscle targets contains the selected goal.
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

    private void addExerciseToWorkout(final Exercise exercise, final WorkoutAdapter.AddExerciseCallback callback) {
        if (userId == null || selectedDate == null) {
            callback.onResult(false);
            return;
        }

        final DocumentReference workoutDocRef = db.collection("users").document(userId).collection("workouts").document(selectedDate);
        workoutDocRef.get().addOnCompleteListener(task -> {
            if (!isAdded()) return;

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    if (Boolean.TRUE.equals(document.getBoolean("isRestDay"))) {
                        Toast.makeText(getContext(), "It's a Rest Day.", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                        return;
                    }
                    workoutDocRef.update("exercises", FieldValue.arrayUnion(exercise))
                        .addOnSuccessListener(aVoid -> handleSuccess(exercise.getTitle(), callback))
                        .addOnFailureListener(e -> handleFailure(e, callback));
                } else {
                     // No workout for this day, create one
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

    private void handleSuccess(String title, WorkoutAdapter.AddExerciseCallback callback) {
        if (getContext() != null) Toast.makeText(getContext(), title + " added!", Toast.LENGTH_SHORT).show();
        callback.onResult(true);
    }

    private void handleFailure(Exception e, WorkoutAdapter.AddExerciseCallback callback) {
        Log.e(TAG, "Error: ", e);
        if (getContext() != null) Toast.makeText(getContext(), "Failed to add.", Toast.LENGTH_SHORT).show();
        callback.onResult(false);
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        ExerciseDetailDialog dialog = new ExerciseDetailDialog(exercise);
        dialog.show(getChildFragmentManager(), "ExerciseDetailDialog");
    }
}
