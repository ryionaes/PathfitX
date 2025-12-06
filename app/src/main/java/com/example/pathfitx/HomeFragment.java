package com.example.pathfitx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment implements ExerciseAdapter.OnItemClickListener, EditExerciseDialog.DialogListener, CalendarAdapter.OnDateClickListener, OptionsBottomSheetFragment.OnOptionSelectedListener, SwapWorkoutBottomSheetFragment.OnOptionSelectedListener {

    private static final String TAG = "HomeFragment";
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String KEY_DEFAULT_TIME = "default_time";
    private static final String KEY_DEFAULT_EQUIPMENT = "default_equipment";

    private RecyclerView rvCalendar;
    private RecyclerView rvExercises;
    private CalendarAdapter calendarAdapter;
    private TextView tvYear, tvTimeOption, tvEquipmentOption, tvWorkoutTitle, tvRestDayMessage, tvExerciseCount, tvWorkoutSubtitle;
    private ImageView ivProfileIcon;
    private SwitchMaterial switchRestDay;
    private MaterialButton btnSwap, btnAddExercise, btnStartWorkout;
    private Group workoutDetailsGroup;
    private View restDayContainer;

    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;
    private FirebaseFirestore db;
    private LocalDate selectedDate;
    private final String userId = "testUser"; // Placeholder

    // Data for Bottom Sheets
    private ArrayList<String> timeOptions = new ArrayList<>(Arrays.asList("15 min", "30 min", "45 min", "1 hr", "1 hr, 30 min"));
    private ArrayList<String> equipmentOptions = new ArrayList<>(Arrays.asList("With Equipment", "No Equipment"));
    private String selectedTime;
    private String selectedEquipment;
    private String selectedWorkout;
    private ArrayList<WorkoutType> workoutTypes = new ArrayList<>();
    private boolean isRestDay = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Initialize Views
        initViews(view);
        loadDefaultOptions();

        selectedDate = LocalDate.now();
        tvYear.setText(String.valueOf(selectedDate.getYear()));

        setupCalendar();
        setupOptionPickers();
        setupWorkoutTypes();
        setupExercises();
        setupClickListeners();
    }

    private void initViews(View view) {
        rvCalendar = view.findViewById(R.id.rv_calendar);
        rvExercises = view.findViewById(R.id.rv_exercises);
        tvYear = view.findViewById(R.id.tv_year);
        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        tvTimeOption = view.findViewById(R.id.tv_time_option);
        tvEquipmentOption = view.findViewById(R.id.tv_equipment_option);
        switchRestDay = view.findViewById(R.id.switch_rest_day);
        tvRestDayMessage = view.findViewById(R.id.tv_rest_day_message);
        tvWorkoutTitle = view.findViewById(R.id.tv_push_day);
        btnSwap = view.findViewById(R.id.btn_swap);
        workoutDetailsGroup = view.findViewById(R.id.workout_details_group);
        tvExerciseCount = view.findViewById(R.id.tv_exercise_count);
        tvWorkoutSubtitle = view.findViewById(R.id.tv_workout_subtitle);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        restDayContainer = view.findViewById(R.id.rest_day_container);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);
    }

    private void loadDefaultOptions() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedTime = prefs.getString(KEY_DEFAULT_TIME, "45 min");
        selectedEquipment = prefs.getString(KEY_DEFAULT_EQUIPMENT, "With Equipment");
        selectedWorkout = "Push Day"; // Default workout for a new day
    }

    private void setupOptionPickers() {
        tvTimeOption.setText(selectedTime);
        tvEquipmentOption.setText(selectedEquipment);

        tvTimeOption.setOnClickListener(v -> showOptionsBottomSheet("time"));
        tvEquipmentOption.setOnClickListener(v -> showOptionsBottomSheet("equipment"));
    }

    private void setupWorkoutTypes() {
        workoutTypes.clear();
        workoutTypes.add(new WorkoutType("Push Day", R.drawable.ic_launcher_background));
        workoutTypes.add(new WorkoutType("Pull Day", R.drawable.ic_launcher_background));
        workoutTypes.add(new WorkoutType("Leg Day", R.drawable.ic_launcher_background));
        workoutTypes.add(new WorkoutType("Upper Body", R.drawable.ic_launcher_background));
        workoutTypes.add(new WorkoutType("Cardio & Core", R.drawable.ic_launcher_background));
        workoutTypes.add(new WorkoutType("Custom Plan", R.drawable.ic_launcher_background));


        tvWorkoutTitle.setText(selectedWorkout);
    }

    private void showOptionsBottomSheet(String type) {
        boolean isTime = type.equals("time");
        String title = isTime ? "Workout duration" : "Equipment";
        ArrayList<String> options = isTime ? timeOptions : equipmentOptions;
        String selected = isTime ? selectedTime : selectedEquipment;

        OptionsBottomSheetFragment bottomSheet = OptionsBottomSheetFragment.newInstance(title, options, selected);
        bottomSheet.setOnOptionSelectedListener(this);
        bottomSheet.show(getParentFragmentManager(), "OptionsBottomSheet");
    }

    private void showSwapWorkoutBottomSheet(){
        SwapWorkoutBottomSheetFragment bottomSheet = SwapWorkoutBottomSheetFragment.newInstance("Swap Workout", workoutTypes, selectedWorkout);
        bottomSheet.setOnOptionSelectedListener(this);
        bottomSheet.show(getParentFragmentManager(), "SwapWorkoutBottomSheet");
    }

    @Override
    public void onOptionSelected(String option) {
        if (timeOptions.contains(option)) {
            selectedTime = option;
            tvTimeOption.setText(selectedTime);
            saveWorkoutsForDate(selectedDate);
        } else if (equipmentOptions.contains(option)) {
            selectedEquipment = option;
            tvEquipmentOption.setText(selectedEquipment);
            saveWorkoutsForDate(selectedDate);
        } else {
            selectedWorkout = option;
            tvWorkoutTitle.setText(selectedWorkout);
            swapWorkout(option);
        }
    }

    private void swapWorkout(String workoutName) {
        isRestDay = false; // Swapping a workout means it's no longer a rest day
        List<Exercise> newExercises;
        switch (workoutName) {
            case "Push Day":
                newExercises = WorkoutTemplates.getPushDay();
                break;
            case "Pull Day":
                newExercises = WorkoutTemplates.getPullDay();
                break;
            case "Leg Day":
                newExercises = WorkoutTemplates.getLegDay();
                break;
            case "Upper Body":
                newExercises = WorkoutTemplates.getUpperBody();
                break;
            case "Cardio & Core":
                newExercises = WorkoutTemplates.getCardioCore();
                break;
            default:
                newExercises = new ArrayList<>();
                break;
        }
        exerciseList.clear();
        exerciseList.addAll(newExercises);
        saveWorkoutsForDate(selectedDate);
        updateUI();
    }

    @Override
    public void onSetAsDefault(String option) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        if (timeOptions.contains(option)) {
            selectedTime = option;
            tvTimeOption.setText(selectedTime);
            editor.putString(KEY_DEFAULT_TIME, selectedTime);
        } else {
            selectedEquipment = option;
            tvEquipmentOption.setText(selectedEquipment);
            editor.putString(KEY_DEFAULT_EQUIPMENT, selectedEquipment);
        }
        editor.apply();
        saveWorkoutsForDate(selectedDate);
    }

    private void setupClickListeners() {
        ivProfileIcon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnAddExercise.setOnClickListener(v -> {
            if (!selectedWorkout.equals("Custom Plan")) {
                selectedWorkout = "Custom Plan";
            }

            WorkoutFragment workoutFragment = new WorkoutFragment();
            Bundle args = new Bundle();
            args.putString("selectedDate", selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            workoutFragment.setArguments(args);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, workoutFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnStartWorkout.setOnClickListener(v -> {
            if (exerciseList == null || exerciseList.isEmpty() || isRestDay) {
                android.widget.Toast.makeText(getContext(), "No workout planned for today!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), LiveSessionActivity.class);
            intent.putExtra("exerciseList", (Serializable) exerciseList);
            startActivity(intent);
        });

        switchRestDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) { // Only trigger if user interacts
                isRestDay = isChecked;
                if(isRestDay){
                    exerciseList.clear();
                }
                saveWorkoutsForDate(selectedDate);
                updateUI();
            }
        });

        btnSwap.setOnClickListener(v -> showSwapWorkoutBottomSheet());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkoutsForDate(selectedDate);
    }

    private void setupCalendar() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        LocalDate startPoint = today.minusWeeks(1).with(DayOfWeek.MONDAY);

        for (int i = 0; i < 21; i++) {
            dates.add(startPoint.plusDays(i));
        }

        int todayPosition = dates.indexOf(today);

        calendarAdapter = new CalendarAdapter(dates, todayPosition, this);
        rvCalendar.setAdapter(calendarAdapter);

        if (todayPosition != -1) {
            ((LinearLayoutManager) rvCalendar.getLayoutManager()).scrollToPositionWithOffset(todayPosition, rvCalendar.getWidth() / 2);
        }
    }

    private void setupExercises() {
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(exerciseAdapter);
        loadWorkoutsForDate(selectedDate);
    }

    @Override
    public void onDateClick(int position) {
        selectedDate = calendarAdapter.getDateAt(position);
        calendarAdapter.setSelectedPosition(position);
        loadWorkoutsForDate(selectedDate);
    }

    private void loadWorkoutsForDate(LocalDate date) {
        String dateId = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        db.collection("users").document(userId).collection("workouts").document(dateId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        exerciseList.clear();
                        loadDefaultOptions(); // Load defaults first

                        if (document != null && document.exists()) {
                            // Overwrite with date-specific data if it exists
                            if (document.contains("time")) {
                                selectedTime = document.getString("time");
                            }
                            if (document.contains("equipment")) {
                                selectedEquipment = document.getString("equipment");
                            }

                            isRestDay = document.getBoolean("isRestDay") != null && document.getBoolean("isRestDay");

                            if(!isRestDay) {
                                String workoutName = document.getString("workoutName");
                                selectedWorkout = workoutName != null ? workoutName : "No Workout Planned";

                                Object exercisesField = document.get("exercises");
                                if (exercisesField instanceof List && !((List) exercisesField).isEmpty()) {
                                    if(selectedWorkout.equals("No Workout Planned")){
                                        selectedWorkout = "Custom Plan";
                                    }
                                    List<HashMap<String, Object>> exercisesMap = (List<HashMap<String, Object>>) exercisesField;
                                    for (HashMap<String, Object> map : exercisesMap) {
                                        Exercise exercise = new Exercise();
                                        exercise.setTitle((String) map.get("title"));
                                        Object setsObj = map.get("sets");
                                        if(setsObj instanceof Long) exercise.setSets(((Long) setsObj).intValue());
                                        if(setsObj instanceof Double) exercise.setSets(((Double) setsObj).intValue());

                                        Object repsObj = map.get("reps");
                                        if(repsObj instanceof Long) exercise.setReps(((Long) repsObj).intValue());
                                        if(repsObj instanceof Double) exercise.setReps(((Double) repsObj).intValue());

                                        Object kgObj = map.get("kg");
                                        if(kgObj instanceof Long) exercise.setKg(((Long) kgObj).intValue());
                                        if(kgObj instanceof Double) exercise.setKg(((Double) kgObj).intValue());

                                        exercise.setAddedToWorkout(true);
                                        exerciseList.add(exercise);
                                    }
                                } else {
                                     selectedWorkout = "No Workout Planned";
                                }
                            }
                        } else {
                            isRestDay = false;
                            selectedWorkout = "No Workout Planned";
                        }
                        updateUI();
                    } else {
                        Log.e(TAG, "Error getting workouts: ", task.getException());
                    }
                });
    }

    private void saveWorkoutsForDate(LocalDate date) {
        String dateId = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("isRestDay", isRestDay);
        workoutData.put("workoutName", selectedWorkout);
        workoutData.put("exercises", exerciseList);
        workoutData.put("time", selectedTime);
        workoutData.put("equipment", selectedEquipment);

        db.collection("users").document(userId).collection("workouts").document(dateId)
                .set(workoutData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Workout saved for " + dateId))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving workout", e));
    }

    private void updateUI() {
        switchRestDay.setChecked(isRestDay);
        tvWorkoutSubtitle.setVisibility(View.GONE);
        tvExerciseCount.setVisibility(View.GONE);
        tvTimeOption.setText(selectedTime);
        tvEquipmentOption.setText(selectedEquipment);

        boolean hasWorkout = !exerciseList.isEmpty();

        if(isRestDay){
            tvWorkoutTitle.setText("Rest Day");
            tvWorkoutSubtitle.setText("Time to recover!");
            tvWorkoutSubtitle.setVisibility(View.VISIBLE);
            workoutDetailsGroup.setVisibility(View.GONE);
            btnAddExercise.setVisibility(View.GONE);
            btnSwap.setVisibility(View.GONE);
        } else if (hasWorkout) {
            tvWorkoutTitle.setText(selectedWorkout);
            int exerciseCount = exerciseList.size();
            tvExerciseCount.setText(exerciseCount + " " + (exerciseCount == 1 ? "Exercise" : "Exercises"));
            tvExerciseCount.setVisibility(View.VISIBLE);
            btnSwap.setText("Swap");
            btnSwap.setIconResource(R.drawable.ic_swap_horiz);
            workoutDetailsGroup.setVisibility(View.VISIBLE);
            btnAddExercise.setVisibility(View.VISIBLE);
            btnSwap.setVisibility(View.VISIBLE);
            tvRestDayMessage.setVisibility(View.GONE);
            restDayContainer.setVisibility(View.VISIBLE);
            btnStartWorkout.setVisibility(View.VISIBLE);
        } else { // Empty Day
            tvWorkoutTitle.setText("No Workout Planned");
            tvWorkoutSubtitle.setText("Start your path to fitness today!");
            tvWorkoutSubtitle.setVisibility(View.VISIBLE);
            btnSwap.setText("Plans");
            btnSwap.setIconResource(android.R.drawable.ic_dialog_info);
            workoutDetailsGroup.setVisibility(View.GONE);
            btnAddExercise.setVisibility(View.VISIBLE);
            btnSwap.setVisibility(View.VISIBLE);
            tvRestDayMessage.setVisibility(View.GONE);
            restDayContainer.setVisibility(View.VISIBLE);
            btnStartWorkout.setVisibility(View.GONE);
        }

        if (exerciseAdapter != null) {
            exerciseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMoreClick(Exercise exercise, int position) {
        EditExerciseDialog dialog = new EditExerciseDialog(exercise, position, this);
        dialog.show(getChildFragmentManager(), "EditExerciseDialog");
    }

    @Override
    public void onSave(Exercise updatedExercise, int position) {
        exerciseList.set(position, updatedExercise);
        saveWorkoutsForDate(selectedDate);
        updateUI();
    }

    @Override
    public void onRemove(Exercise exerciseToRemove, int position) {
        exerciseList.remove(position);
        saveWorkoutsForDate(selectedDate);
        updateUI();
    }
}
