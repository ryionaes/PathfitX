package com.example.pathfitx;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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

    private RecyclerView rvExplore;
    private WorkoutAdapter adapter;
    private EditText etSearch;
    private FirebaseFirestore db;
    private String userId;
    private String selectedDate;

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
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = user.getUid();
        }

        if (getArguments() != null) {
            selectedDate = getArguments().getString(ARG_SELECTED_DATE);
        } else {
            selectedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        rvExplore = view.findViewById(R.id.rv_explore);
        setupExploreList();
    }

    private void setupExploreList() {
        List<Exercise> allExercises = ExerciseDatabase.getAllExercises();
        adapter = new WorkoutAdapter(allExercises, this);
        rvExplore.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExplore.setAdapter(adapter);
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
        final DocumentReference userDocRef = db.collection("users").document(userId);

        // 1. Kunin ang GLOBAL DEFAULTS
        userDocRef.get().addOnCompleteListener(userTask -> {
            String defaultTime = "45 min";
            String defaultEquip = "With Equipment";

            if (userTask.isSuccessful() && userTask.getResult().exists()) {
                DocumentSnapshot userDoc = userTask.getResult();
                if (userDoc.contains("defaultTime")) defaultTime = userDoc.getString("defaultTime");
                if (userDoc.contains("defaultEquipment")) defaultEquip = userDoc.getString("defaultEquipment");
            }

            final String finalTime = defaultTime;
            final String finalEquip = defaultEquip;

            // 2. I-save o I-update ang Workout
            workoutDocRef.get().addOnCompleteListener(task -> {
                if (!isAdded()) return;

                Exercise newEx = new Exercise(exercise);
                newEx.setSets(3);
                newEx.setReps(10);
                newEx.setKg(0);
                newEx.setAddedToWorkout(true);

                if (task.isSuccessful() && task.getResult().exists()) {
                    DocumentSnapshot doc = task.getResult();
                    boolean isRest = doc.getBoolean("isRestDay") != null && doc.getBoolean("isRestDay");

                    if (isRest) {
                        Toast.makeText(getContext(), "Rest Day ngayon.", Toast.LENGTH_SHORT).show();
                        callback.onResult(false);
                        return;
                    }

                    workoutDocRef.update("exercises", FieldValue.arrayUnion(newEx))
                            .addOnSuccessListener(aVoid -> handleSuccess(newEx.getTitle(), callback))
                            .addOnFailureListener(e -> handleFailure(e, callback));
                } else {
                    Map<String, Object> newWorkout = new HashMap<>();
                    newWorkout.put("workoutName", "Custom Plan");
                    newWorkout.put("isRestDay", false);
                    newWorkout.put("time", finalTime);      // Gamit ang Default
                    newWorkout.put("equipment", finalEquip); // Gamit ang Default
                    newWorkout.put("exercises", Collections.singletonList(newEx));

                    workoutDocRef.set(newWorkout)
                            .addOnSuccessListener(aVoid -> handleSuccess(newEx.getTitle(), callback))
                            .addOnFailureListener(e -> handleFailure(e, callback));
                }
            });
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