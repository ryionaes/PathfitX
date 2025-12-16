package com.example.pathfitx;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment implements ExerciseAdapter.OnItemClickListener, EditExerciseDialog.DialogListener, CalendarAdapter.OnDateClickListener, OptionsBottomSheetFragment.OnOptionSelectedListener, SwapWorkoutBottomSheetFragment.OnOptionSelectedListener, UserUpdatable {

    private static final String TAG = "HomeFragment";
    private static final String ARG_SELECTED_DATE = "selectedDate";

    // UI
    private RecyclerView rvCalendar, rvExercises;
    private CalendarAdapter calendarAdapter;
    private ExerciseAdapter exerciseAdapter;
    private TextView tvYear, tvUserName, tvTimeOption, tvEquipmentOption, tvWorkoutTitle, tvRestDayMessage, tvExerciseCount, tvWorkoutSubtitle;
    private ImageView ivProfileIcon;
    private SwitchMaterial switchRestDay;
    private MaterialButton btnSwap, btnAddExercise, btnStartWorkout;
    private Group workoutDetailsGroup;
    private View restDayCard;

    // Firebase
    private FirebaseFirestore db;
    private ListenerRegistration workoutListener;

    // State
    private List<Exercise> exerciseList = new ArrayList<>();
    private LocalDate selectedDate;
    private String selectedTime = "45 min";
    private String selectedEquipment = "With Equipment";
    private String selectedWorkout = "No Workout Planned";
    private boolean isRestDay = false;
    private final ArrayList<WorkoutType> workoutTypes = new ArrayList<>();
    private final ArrayList<String> timeOptions = new ArrayList<>(Arrays.asList("15 min", "30 min", "45 min", "1 hr", "1 hr, 30 min"));
    private final ArrayList<String> equipmentOptions = new ArrayList<>(Arrays.asList("With Equipment", "No Equipment"));

    private OnDateSelectedListener mListener;

    public static HomeFragment newInstance(String selectedDate) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_DATE, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDateSelectedListener) {
            mListener = (OnDateSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDateSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        initViews(view);

        if (getArguments() != null && getArguments().getString(ARG_SELECTED_DATE) != null) {
            selectedDate = LocalDate.parse(getArguments().getString(ARG_SELECTED_DATE));
        } else {
            selectedDate = LocalDate.now();
        }
        tvYear.setText(String.valueOf(selectedDate.getYear()));

        if (getActivity() instanceof HomeScreen) {
            DocumentSnapshot userSnapshot = ((HomeScreen) getActivity()).getUserSnapshot();
            if (userSnapshot != null) {
                onUserUpdate(userSnapshot);
            }
        }

        setupCalendar();
        setupOptionPickers();
        setupWorkoutTypes();
        setupExercises();
        setupClickListeners();
        loadWorkoutsForDate(selectedDate);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (workoutListener != null) {
            workoutListener.remove();
        }
    }

    @Override
    public void onUserUpdate(DocumentSnapshot snapshot) {
        if (isAdded() && getContext() != null) {
            String username = snapshot.getString("username");
            String profileImageUri = snapshot.getString("profileImageUri");

            tvUserName.setText(username != null ? username : "Fitness User");

            if (ivProfileIcon != null) {
                if (profileImageUri != null && !profileImageUri.isEmpty()) {
                    Glide.with(this).load(Uri.parse(profileImageUri)).circleCrop()
                            .signature(new ObjectKey(System.currentTimeMillis()))
                            .into(ivProfileIcon);
                } else {
                    Glide.with(this).load(R.drawable.ic_profile_default).circleCrop().into(ivProfileIcon);
                }
            }
        }
    }

    private void loadWorkoutsForDate(LocalDate date) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();

        String dateId = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (mListener != null) {
            mListener.onDateSelected(dateId);
        }

        if (workoutListener != null) {
            workoutListener.remove();
        }

        DocumentReference workoutDocRef = db.collection("users").document(userId).collection("workouts").document(dateId);
        workoutListener = workoutDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error getting workouts: ", e);
                return;
            }

            exerciseList.clear();

            if (snapshot != null && snapshot.exists()) {
                selectedTime = snapshot.getString("time");
                selectedEquipment = snapshot.getString("equipment");
                isRestDay = snapshot.getBoolean("isRestDay") != null && snapshot.getBoolean("isRestDay");

                if (!isRestDay) {
                    selectedWorkout = snapshot.getString("workoutName");
                    Object exercisesField = snapshot.get("exercises");
                    if (exercisesField instanceof List) {
                        exerciseList.addAll(parseExercisesFromMap((List<HashMap<String, Object>>) exercisesField));
                    }
                } else {
                    selectedWorkout = "Rest Day";
                }
            } else {
                isRestDay = false;
                selectedWorkout = "No Workout Planned";
            }
            updateUI();
        });
    }

    private void saveWorkoutsForDate(LocalDate date) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();
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

    @Override
    public void onDateClick(int position) {
        selectedDate = calendarAdapter.getDateAt(position);
        calendarAdapter.setSelectedPosition(position);
        loadWorkoutsForDate(selectedDate);
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
        if (exerciseList.isEmpty()) {
            selectedWorkout = "No Workout Planned";
        }
        saveWorkoutsForDate(selectedDate);
        updateUI();
    }

    @Override
    public void onOptionSelected(String option) {
        if (timeOptions.contains(option)) {
            selectedTime = option;
            tvTimeOption.setText(selectedTime);
        } else if (equipmentOptions.contains(option)) {
            selectedEquipment = option;
            tvEquipmentOption.setText(selectedEquipment);
        } else {
            selectedWorkout = option;
            tvWorkoutTitle.setText(selectedWorkout);
            swapWorkout(option);
            return;
        }
        saveWorkoutsForDate(selectedDate);
    }

    private void swapWorkout(String workoutName) {
        if ("Custom Plan".equals(workoutName)) {
            exerciseList.clear();
            isRestDay = false;
            saveWorkoutsForDate(selectedDate);
            updateUI();
            return;
        }
        db.collection("workout_templates").document(workoutName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<HashMap<String, Object>> exercisesMap = (List<HashMap<String, Object>>) documentSnapshot.get("exercises");
                        exerciseList.clear();
                        exerciseList.addAll(parseExercisesFromMap(exercisesMap));
                        isRestDay = false;
                        selectedWorkout = workoutName;
                        saveWorkoutsForDate(selectedDate);
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        switchRestDay.setChecked(isRestDay);
        tvTimeOption.setText(selectedTime);
        tvEquipmentOption.setText(selectedEquipment);
        tvWorkoutTitle.setText(selectedWorkout);

        boolean hasWorkout = !exerciseList.isEmpty();

        if (isRestDay) {
            workoutDetailsGroup.setVisibility(View.GONE);
            btnAddExercise.setVisibility(View.GONE);
            btnSwap.setVisibility(View.GONE);
            restDayCard.setVisibility(View.VISIBLE);
            tvWorkoutSubtitle.setVisibility(View.GONE);
            tvRestDayMessage.setVisibility(View.VISIBLE);
        } else if (hasWorkout) {
            workoutDetailsGroup.setVisibility(View.VISIBLE);
            btnAddExercise.setVisibility(View.VISIBLE);
            btnSwap.setVisibility(View.VISIBLE);
            restDayCard.setVisibility(View.GONE);
            tvWorkoutSubtitle.setVisibility(View.VISIBLE);

            int exerciseCount = exerciseList.size();
            Set<String> muscleGroups = new HashSet<>();
            for (Exercise exercise : exerciseList) {
                if (exercise.getMuscleTargets() != null) {
                    muscleGroups.addAll(exercise.getMuscleTargets());
                }
            }
            int muscleCount = muscleGroups.size();

            tvWorkoutSubtitle.setText(String.format("%d Exercises • %d Muscles", exerciseCount, muscleCount));
            tvRestDayMessage.setVisibility(View.GONE);
        } else { // Empty Day
            workoutDetailsGroup.setVisibility(View.GONE);
            btnAddExercise.setVisibility(View.VISIBLE);
            btnSwap.setVisibility(View.VISIBLE);
            restDayCard.setVisibility(View.VISIBLE);
            tvWorkoutSubtitle.setVisibility(View.VISIBLE);
            tvWorkoutSubtitle.setText("Add a workout or set as rest day.");
            tvRestDayMessage.setVisibility(View.GONE);
        }
        if(exerciseAdapter != null) exerciseAdapter.notifyDataSetChanged();
    }
    
    private void initViews(View view) {
        rvCalendar = view.findViewById(R.id.rv_calendar);
        rvExercises = view.findViewById(R.id.rv_exercises);
        tvYear = view.findViewById(R.id.tv_year);
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        tvTimeOption = view.findViewById(R.id.tv_time_option);
        tvEquipmentOption = view.findViewById(R.id.tv_equipment_option);
        switchRestDay = view.findViewById(R.id.switch_rest_day);
        tvWorkoutTitle = view.findViewById(R.id.tv_push_day);
        btnSwap = view.findViewById(R.id.btn_swap);
        workoutDetailsGroup = view.findViewById(R.id.workout_details_group);
        tvExerciseCount = view.findViewById(R.id.tv_exercise_count);
        tvWorkoutSubtitle = view.findViewById(R.id.tv_workout_subtitle);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        restDayCard = view.findViewById(R.id.rest_day_card);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);
        tvRestDayMessage = view.findViewById(R.id.tv_rest_day_message);
    }

    private void setupClickListeners() {
        ivProfileIcon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();

            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_profile);
            }
        });

        btnAddExercise.setOnClickListener(v -> {
            if (!"Custom Plan".equals(selectedWorkout)) {
                selectedWorkout = "Custom Plan";
            }

            WorkoutFragment workoutFragment = WorkoutFragment.newInstance(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, workoutFragment)
                    .addToBackStack(null)
                    .commit();

            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_workout);
            }
        });

        btnStartWorkout.setOnClickListener(v -> {
            if (exerciseList == null || exerciseList.isEmpty() || isRestDay) {
                Toast.makeText(getContext(), "No workout planned for today!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), LiveSessionActivity.class);
            intent.putExtra("exerciseList", (Serializable) exerciseList);
            startActivity(intent);
        });

        switchRestDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                isRestDay = isChecked;
                if (isRestDay) {
                    exerciseList.clear();
                    selectedWorkout = "Rest Day";
                } else {
                    // If there are exercises, it's a custom plan, otherwise no workout.
                    if (!exerciseList.isEmpty()) {
                        selectedWorkout = "Custom Plan";
                    } else {
                        selectedWorkout = "No Workout Planned";
                    }
                }
                saveWorkoutsForDate(selectedDate);
                updateUI();
            }
        });

        btnSwap.setOnClickListener(v -> showSwapWorkoutBottomSheet());
    }

    private void setupCalendar() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<LocalDate> dates = new ArrayList<>();
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        for (int i = 0; i < 90; i++) { // 3 months of dates
            dates.add(monthStart.plusDays(i));
        }
        int selectedPosition = dates.indexOf(selectedDate);
        calendarAdapter = new CalendarAdapter(dates, selectedPosition, this);
        calendarAdapter.setSelectedPosition(selectedPosition);
        rvCalendar.setAdapter(calendarAdapter);
        if (selectedPosition != -1) {
            rvCalendar.post(() -> {
                LinearLayoutManager layoutManager = (LinearLayoutManager) rvCalendar.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(selectedPosition, rvCalendar.getWidth() / 2 - 40);
                }
            });
        }
    }
    
    private void setupExercises() {
        exerciseAdapter = new ExerciseAdapter(exerciseList, this);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(exerciseAdapter);
    }

    private void setupOptionPickers() {
        tvTimeOption.setOnClickListener(v -> showOptionsBottomSheet("time"));
        tvEquipmentOption.setOnClickListener(v -> showOptionsBottomSheet("equipment"));
    }

    private void setupWorkoutTypes() {
        workoutTypes.clear();
        workoutTypes.add(new WorkoutType("Push Day", ""));
        workoutTypes.add(new WorkoutType("Pull Day", ""));
        workoutTypes.add(new WorkoutType("Leg Day", ""));
        workoutTypes.add(new WorkoutType("Upper Body", ""));
        workoutTypes.add(new WorkoutType("Cardio & Core", ""));
        workoutTypes.add(new WorkoutType("Custom Plan", ""));
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
    
    private List<Exercise> parseExercisesFromMap(List<HashMap<String, Object>> exercisesMap) {
        List<Exercise> exercises = new ArrayList<>();
        if (exercisesMap == null) return exercises;

        for (HashMap<String, Object> map : exercisesMap) {
            try {
                Exercise exercise = new Exercise();
                exercise.setTitle((String) map.get("title"));
                if (map.get("sets") != null) exercise.setSets(((Long) map.get("sets")).intValue());
                if (map.get("reps") != null) exercise.setReps(((Long) map.get("reps")).intValue());
                if (map.get("kg") != null) exercise.setKg(((Long) map.get("kg")).intValue());
                if (map.get("category") != null) exercise.setCategory(Exercise.Category.valueOf((String) map.get("category")));
                if (map.get("bodyPart") != null) exercise.setBodyPart(Exercise.BodyPart.valueOf((String) map.get("bodyPart")));
                if (map.get("muscleTargets") != null) exercise.setMuscleTargets((List<String>) map.get("muscleTargets"));
                if (map.get("imageUrl") != null) exercise.setImageUrl((String) map.get("imageUrl"));
                exercise.setAddedToWorkout(true);
                exercises.add(exercise);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing exercise from map: " + map.toString(), e);
            }
        }
        return exercises;
    }

    @Override
    public void onSetAsDefault(String option) {}

    @Override
    public void onMoreClick(Exercise exercise, int position) {
        EditExerciseDialog dialog = new EditExerciseDialog(exercise, position, this);
        dialog.show(getChildFragmentManager(), "EditExerciseDialog");
    }
}
