package com.example.pathfitx;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WorkoutFragment extends Fragment {

    private RecyclerView rvExplore;
    private WorkoutAdapter adapter;
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvExplore = view.findViewById(R.id.rv_explore);
        etSearch = view.findViewById(R.id.et_search);

        setupList();
        setupSearch();
    }

    private void setupList() {
        // 1. Create dummy data
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Barbell Squat", "Legs • Hard", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Deadlift", "Back • Hard", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Pull Ups", "Back • Medium", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Bicep Curls", "Arms • Easy", R.drawable.ic_launcher_background));
        exercises.add(new Exercise("Leg Press", "Legs • Medium", R.drawable.ic_launcher_background));

        // 2. Setup Adapter
        adapter = new WorkoutAdapter(exercises);
        rvExplore.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExplore.setAdapter(adapter);
    }

    private void setupSearch() {
        // 3. Listen for text changes
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list as user types
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}