package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ExerciseAdapter.OnItemClickListener, EditExerciseDialog.DialogListener{

    private RecyclerView rvCalendar;
    private RecyclerView rvExercises;

    private View headerContainer;
    private View btnAddExercise;
    private View btnStartWorkout;

    // These variables must be up here (Class Level) so we can access them later
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCalendar = view.findViewById(R.id.rv_calendar);
        rvExercises = view.findViewById(R.id.rv_exercises);

        headerContainer = view.findViewById(R.id.header_container);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);

        setupCalendar();
        setupExercises();

        // Set Click Listener to Navigate
        btnAddExercise.setOnClickListener(v -> {
            // Navigate to WorkoutFragment (Explore)
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WorkoutFragment())
                    .addToBackStack(null) // Allows user to press Back button to return
                    .commit();
        });

        // Navigate to LiveSessionActivity
        View btnStart = view.findViewById(R.id.btn_start_workout);
        btnStart.setOnClickListener(v -> {
            // VALIDATION CHECK
            if (exerciseList == null || exerciseList.isEmpty()) {
                // Show Toast if empty
                android.widget.Toast.makeText(getContext(),
                        "Please add an exercise first!",
                        android.widget.Toast.LENGTH_SHORT).show();
                return; // Stop here, do not proceed
            }

            // Start the new Activity
            Intent intent = new Intent(getActivity(), LiveSessionActivity.class);
            startActivity(intent);
        });
    }

    // This runs every time you return to this screen (e.g. back from Explore tab)
    @Override
    public void onResume() {
        super.onResume();
        refreshExerciseList();
    }

    private void setupCalendar() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // Use your existing CalendarAdapter (make sure to fix the constructor if you added a listener earlier)
        rvCalendar.setAdapter(new CalendarAdapter());
    }

    // This method now only initializes the empty list and adapter
    private void setupExercises() {
        exerciseList = new ArrayList<>();
        // Pass 'this' as the listener to the adapter
        exerciseAdapter = new ExerciseAdapter(exerciseList, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(exerciseAdapter);
    }

    // NEW: Helper method to pull data from the Repository
    private void refreshExerciseList() {
        // Get the exercises saved in our Repository (The "Storage Box")
        List<Exercise> savedData = SelectedWorkoutRepository.getInstance().getSelectedExercises();

        // Clear the current list on the screen
        exerciseList.clear();

        // Add the new data from the repository
        exerciseList.addAll(savedData);

        // Tell the adapter to refresh the screen
        if (exerciseAdapter != null) {
            exerciseAdapter.notifyDataSetChanged();
        }

        // LOGIC FOR VISIBILITY
        int count = exerciseList.size();

        // RULE A: If empty (count == 0), Hide Header. Else Show Header.
        if (count == 0) {
            headerContainer.setVisibility(View.GONE);
            rvExercises.setVisibility(View.GONE); // Optional: Hide list if empty
        } else {
            headerContainer.setVisibility(View.VISIBLE);
            rvExercises.setVisibility(View.VISIBLE);
        }

        // RULE B: Show Add Button if items are 3 or less. Hide if more than 3.
        if (count <= 3) {
            btnAddExercise.setVisibility(View.VISIBLE);
        } else {
            btnAddExercise.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMoreClick(Exercise exercise, int position) {
        // Show the edit dialog, passing 'this' as the listener for save/remove events
        EditExerciseDialog dialog = new EditExerciseDialog(exercise, position, this);
        dialog.show(getChildFragmentManager(), "EditExerciseDialog");
    }

    // Handle "Save" from Dialog
    @Override
    public void onSave(Exercise updatedExercise, int position) {
        SelectedWorkoutRepository.getInstance().updateExercise(position, updatedExercise);
        refreshExerciseList(); // Refresh the UI
    }

    // Handle "Remove" from Dialog
    @Override
    public void onRemove(Exercise exerciseToRemove, int position) {
        SelectedWorkoutRepository.getInstance().removeExercise(exerciseToRemove);
        refreshExerciseList(); // Refresh the UI
        // Optional: Show a confirmation toast
        //Toast.makeText(getContext(), "Exercise removed", Toast.LENGTH_SHORT).show();
    }
}