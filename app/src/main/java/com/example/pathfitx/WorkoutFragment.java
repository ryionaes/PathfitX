package com.example.pathfitx;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class WorkoutFragment extends Fragment implements WorkoutAdapter.OnExerciseAddListener {

    private static final String TAG = "WorkoutFragment";
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String KEY_DEFAULT_TIME = "default_time";
    private static final String KEY_DEFAULT_EQUIPMENT = "default_equipment";

    private RecyclerView rvExplore;
    private WorkoutAdapter adapter;
    private EditText etSearch;
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

        if (getArguments() != null) {
            selectedDate = getArguments().getString("selectedDate");
        } else {
            // Fallback to today if no date is passed
            selectedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        rvExplore = view.findViewById(R.id.rv_explore);
        etSearch = view.findViewById(R.id.et_search);

        setupList();
        setupSearch();
    }

    private void setupList() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Barbell Squat", "Legs • Hard", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Deadlift", "Back • Hard", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Pull Ups", "Back • Medium", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Bicep Curls", "Arms • Easy", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Leg Press", "Legs • Medium", R.drawable.ic_launcher_background));

        adapter = new WorkoutAdapter(exercises, this);
        rvExplore.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExplore.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onExerciseAdd(Exercise exercise) {
        exercise.setAddedToWorkout(true);
        String dateId = selectedDate;
        DocumentReference workoutDocRef = db.collection("users").document(userId).collection("workouts").document(dateId);

        workoutDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists: check for duplicates and update
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
                    } else {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("exercises", FieldValue.arrayUnion(exercise));
                        updates.put("workoutName", "Custom Plan");
                        workoutDocRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), exercise.getTitle() + " added!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Exercise added and workout updated for " + dateId);
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error updating workout", e));
                    }
                } else {
                    // Document does not exist: create it
                    SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
                                Toast.makeText(getContext(), exercise.getTitle() + " added!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "New custom workout created for " + dateId);
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error creating new workout", e));
                }
            } else {
                Log.e(TAG, "Error getting workout document", task.getException());
            }
        });
    }
}
