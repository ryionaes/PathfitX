package com.example.pathfitx;

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

public class HomeFragment extends Fragment {

    private RecyclerView rvCalendar;
    private RecyclerView rvExercises;

    private View headerContainer;
    private View btnAddExercise;

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
        // Initialize the list (Empty for now)
        exerciseList = new ArrayList<>();

        // Initialize the adapter with the empty list
        exerciseAdapter = new ExerciseAdapter(exerciseList);

        // Setup RecyclerView
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(exerciseAdapter);

        // Note: We removed the "Dummy Data" block here because
        // we want to load the real data from the Repository instead.
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
}