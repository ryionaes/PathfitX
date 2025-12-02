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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCalendar = view.findViewById(R.id.rv_calendar);
        rvExercises = view.findViewById(R.id.rv_exercises);

        setupCalendar();
        setupExercises();
    }

    private void setupCalendar() {
        // Horizontal Layout for Calendar
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCalendar.setAdapter(new CalendarAdapter());
    }

    private void setupExercises() {
        // Create Dummy Data
        List<Exercise> exercises = new ArrayList<>();
        // Use R.drawable.your_image_name if you have imported assets
        exercises.add(new Exercise("Dumbbell Bench Press", "3 Sets • 8 reps • 8 kg", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Dumbbell Shoulder Press", "3 Sets • 8 reps • 8 kg", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Dumbbell Tricep Extension", "3 Sets • 8 reps • 8 kg", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Incline Press", "3 Sets • 10 reps • 12 kg", R.drawable.ic_launcher_background));

        // Vertical Layout for Exercises
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(new ExerciseAdapter(exercises));
    }
}