package com.example.pathfitx;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment implements ExerciseAdapter.OnItemClickListener, EditExerciseDialog.DialogListener, CalendarAdapter.OnDateClickListener {

    private RecyclerView rvCalendar;
    private RecyclerView rvExercises;
    private CalendarAdapter calendarAdapter;
    private TextView tvYear;
    private ImageView ivProfileIcon;

    private View headerContainer;
    private View btnAddExercise;
    private View btnStartWorkout;

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
        tvYear = view.findViewById(R.id.tv_year);
        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);

        headerContainer = view.findViewById(R.id.header_container);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);

        // Set the current year
        tvYear.setText(String.valueOf(LocalDate.now().getYear()));

        setupCalendar();
        setupExercises();

        // Set OnClickListener for the profile icon
        ivProfileIcon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null) // Optional: Allows user to press Back to return
                    .commit();
        });

        btnAddExercise.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WorkoutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        View btnStart = view.findViewById(R.id.btn_start_workout);
        btnStart.setOnClickListener(v -> {
            if (exerciseList == null || exerciseList.isEmpty()) {
                android.widget.Toast.makeText(getContext(), "Please add an exercise first!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), LiveSessionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshExerciseList();
    }

    private void setupCalendar() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Start the list with yesterday.
        dates.add(yesterday);

        // Add today and the next 30 days for future planning.
        for (int i = 0; i < 30; i++) {
            dates.add(today.plusDays(i));
        }

        // "Today" is always at position 1 in our new list.
        int todayPosition = 1;

        calendarAdapter = new CalendarAdapter(dates, todayPosition, this);
        rvCalendar.setAdapter(calendarAdapter);

        // Scroll to the beginning to show "Yesterday" and "Today".
        rvCalendar.scrollToPosition(0);
    }


    private void setupExercises() {
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(exerciseAdapter);
    }

    @Override
    public void onDateClick(int position) {
        calendarAdapter.setSelectedPosition(position);
        // TODO: Add logic to load workout data for the selected date
    }

    private void refreshExerciseList() {
        List<Exercise> savedData = SelectedWorkoutRepository.getInstance().getSelectedExercises();
        exerciseList.clear();
        exerciseList.addAll(savedData);

        if (exerciseAdapter != null) {
            exerciseAdapter.notifyDataSetChanged();
        }

        int count = exerciseList.size();
        if (count == 0) {
            headerContainer.setVisibility(View.GONE);
            rvExercises.setVisibility(View.GONE);
        } else {
            headerContainer.setVisibility(View.VISIBLE);
            rvExercises.setVisibility(View.VISIBLE);
        }

        if (count <= 3) {
            btnAddExercise.setVisibility(View.VISIBLE);
        } else {
            btnAddExercise.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMoreClick(Exercise exercise, int position) {
        EditExerciseDialog dialog = new EditExerciseDialog(exercise, position, this);
        dialog.show(getChildFragmentManager(), "EditExerciseDialog");
    }

    @Override
    public void onSave(Exercise updatedExercise, int position) {
        SelectedWorkoutRepository.getInstance().updateExercise(position, updatedExercise);
        refreshExerciseList();
    }

    @Override
    public void onRemove(Exercise exerciseToRemove, int position) {
        SelectedWorkoutRepository.getInstance().removeExercise(exerciseToRemove);
        refreshExerciseList();
    }
}