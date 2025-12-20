package com.example.pathfitx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment implements ExerciseAdapter.OnItemClickListener, EditExerciseDialog.DialogListener, CalendarAdapter.OnDateClickListener, OptionsBottomSheetFragment.OnOptionSelectedListener, SwapWorkoutBottomSheetFragment.OnOptionSelectedListener {

    private static final String TAG = "HomeFragment";
    private static final String ARG_SELECTED_DATE = "selectedDate";
    public static final String PREFS_NAME = "WorkoutPrefs";
    public static final String KEY_DEFAULT_TIME = "default_time";
    public static final String KEY_WORKOUT_IN_PROGRESS = "workout_in_progress";
    public static final String KEY_SAVE_TYPE = "save_type";

    private RecyclerView rvCalendar, rvExercises;
    private CalendarAdapter calendarAdapter;
    private ExerciseAdapter exerciseAdapter;
    private TextView tvYear, tvUserName, tvTimeOption, tvEquipmentOption, tvWorkoutTitle, tvRestDayMessage, tvWorkoutSubtitle;
    private ImageView ivProfileIcon, ivNotificationIcon;
    private SwitchMaterial switchRestDay;
    private MaterialButton btnSwap, btnAddExercise, btnStartWorkout;
    private Group workoutDetailsGroup;
    private View restDayCard;
    private NestedScrollView mainScrollView;

    private final List<Exercise> exerciseList = new ArrayList<>();
    private LocalDate selectedDate;
    private LocalDate registrationDate;

    private String globalDefaultTime = "45 min";
    private String globalDefaultEquipment = "With Equipment";

    private String selectedTime = globalDefaultTime;
    private String selectedEquipment = globalDefaultEquipment;
    private String selectedWorkout = "No Workout Planned";

    private boolean isRestDay = false;
    private double userWeight = 70.0;
    private boolean userDefaultsLoaded = false;
    private final ArrayList<WorkoutType> workoutTypes = new ArrayList<>();
    private final ArrayList<String> timeOptions = new ArrayList<>(Arrays.asList("15 min", "30 min", "45 min", "1 hr", "1 hr, 30 min", "Free Time"));
    private final ArrayList<String> equipmentOptions = new ArrayList<>(Arrays.asList("With Equipment", "No Equipment"));

    private OnDateSelectedListener mListener;
    private SharedViewModel sharedViewModel;

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
            throw new RuntimeException(context + " must implement OnDateSelectedListener");
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
        initViews(view);

        if (getArguments() != null && getArguments().getString(ARG_SELECTED_DATE) != null) {
            selectedDate = LocalDate.parse(getArguments().getString(ARG_SELECTED_DATE));
        } else {
            selectedDate = LocalDate.now();
        }
        tvYear.setText(String.valueOf(selectedDate.getYear()));

        setupViewModel();
        setupOptionPickers();
        setupWorkoutTypes();
        setupExercises();
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStartButton();
    }

    private void setupViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        globalDefaultTime = prefs.getString(KEY_DEFAULT_TIME, "45 min");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getMetadata() != null) {
            long creationTimestamp = user.getMetadata().getCreationTimestamp();
            registrationDate = Instant.ofEpochMilli(creationTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            registrationDate = LocalDate.now().minusMonths(1);
        }

        setupCalendar();

        sharedViewModel.getUserSnapshot().observe(getViewLifecycleOwner(), userSnapshot -> {
            if (userSnapshot != null && userSnapshot.exists()) {
                if (userSnapshot.contains("defaultEquipment")) globalDefaultEquipment = userSnapshot.getString("defaultEquipment");
                if (userSnapshot.contains("weight_kg")) {
                    Double weight = userSnapshot.getDouble("weight_kg");
                    if (weight != null) userWeight = weight;
                }
                tvUserName.setText(userSnapshot.getString("username") != null ? userSnapshot.getString("username") : "Fitness User");
                String img = userSnapshot.getString("profileImageUri");
                if (ivProfileIcon != null) {
                    Glide.with(this).load(img != null && !img.isEmpty() ? Uri.parse(img) : R.drawable.pfp).circleCrop().into(ivProfileIcon);
                }
                userDefaultsLoaded = true;
                updateWorkoutUI(sharedViewModel.getWorkoutSnapshot().getValue());
            }
        });

        sharedViewModel.getWorkoutSnapshot().observe(getViewLifecycleOwner(), snapshot -> {
            if (userDefaultsLoaded) updateWorkoutUI(snapshot);
        });
    }

    private void updateWorkoutUI(DocumentSnapshot snapshot) {
        if (!isAdded()) return;
        exerciseList.clear();
        if (snapshot != null && snapshot.exists()) {
            Boolean restDay = snapshot.getBoolean("isRestDay");
            isRestDay = restDay != null && restDay;
            selectedTime = snapshot.getString("time") != null ? snapshot.getString("time") : globalDefaultTime;
            selectedEquipment = snapshot.getString("equipment") != null ? snapshot.getString("equipment") : globalDefaultEquipment;

            if (!isRestDay) {
                selectedWorkout = snapshot.getString("workoutName") != null ? snapshot.getString("workoutName") : "Custom Plan";
                List<HashMap<String, Object>> map = (List<HashMap<String, Object>>) snapshot.get("exercises");
                if (map != null) exerciseList.addAll(parseExercisesFromMap(map));
            } else {
                selectedWorkout = "Rest Day";
            }
        } else {
            isRestDay = false;
            selectedWorkout = "No Workout Planned";
            selectedTime = globalDefaultTime;
            selectedEquipment = globalDefaultEquipment;
        }
        updateGenericUI();
    }

    private void updateGenericUI() {
        if (!isAdded()) return;

        tvTimeOption.setText(selectedTime);
        tvEquipmentOption.setText(selectedEquipment);
        tvWorkoutTitle.setText(selectedWorkout);
        switchRestDay.setChecked(isRestDay);

        boolean hasWorkout = !exerciseList.isEmpty();
        boolean isToday = selectedDate.equals(LocalDate.now());
        btnStartWorkout.setVisibility(isToday && hasWorkout && !isRestDay ? View.VISIBLE : View.GONE);

        if (isRestDay) {
            workoutDetailsGroup.setVisibility(View.GONE);
            restDayCard.setVisibility(View.VISIBLE);
            tvRestDayMessage.setText("Today is a rest day.");
            tvRestDayMessage.setVisibility(View.VISIBLE);
            tvWorkoutSubtitle.setVisibility(View.GONE);
            btnSwap.setVisibility(View.GONE);
            btnAddExercise.setVisibility(View.GONE);
        } else {
            restDayCard.setVisibility(View.VISIBLE);
            tvRestDayMessage.setVisibility(View.GONE);
            tvWorkoutSubtitle.setVisibility(View.VISIBLE);
            btnSwap.setVisibility(View.VISIBLE);
            btnAddExercise.setVisibility(View.VISIBLE);

            if (hasWorkout) {
                workoutDetailsGroup.setVisibility(View.VISIBLE);
                Set<String> muscles = new HashSet<>();
                double totalCalories = 0;
                double totalDurationMinutes = parseDurationInMinutes(selectedTime);

                if (totalDurationMinutes > 0) {
                    double totalMetInList = 0;
                    int validCount = 0;
                    for (Exercise ex : exerciseList) {
                        if (ex != null) {
                            totalMetInList += ex.getMet();
                            if (ex.getMuscleTargets() != null) muscles.addAll(ex.getMuscleTargets());
                            validCount++;
                        }
                    }
                    if (validCount > 0) {
                        double avgMet = totalMetInList / validCount;
                        totalCalories = ExerciseDatabase.calculateCalories(userWeight, avgMet, totalDurationMinutes);
                    }
                } else {
                    for (Exercise ex : exerciseList) {
                        if (ex != null && ex.getMuscleTargets() != null) muscles.addAll(ex.getMuscleTargets());
                    }
                }

                if ("Free Time".equalsIgnoreCase(selectedTime)) {
                    tvWorkoutSubtitle.setText(String.format(Locale.getDefault(), "%d Exercises • %d Muscles", exerciseList.size(), muscles.size()));
                } else {
                    tvWorkoutSubtitle.setText(String.format(Locale.getDefault(), "%d Exercises • %d Muscles • %.0f est. cal burn", exerciseList.size(), muscles.size(), totalCalories));
                }
            } else {
                workoutDetailsGroup.setVisibility(View.GONE);
                tvWorkoutSubtitle.setText("Add a workout or set as rest day.");
            }
        }
        if (exerciseAdapter != null) exerciseAdapter.notifyDataSetChanged();
        updateStartButton();
    }

    private void updateStartButton() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean inProgress = prefs.getBoolean(KEY_WORKOUT_IN_PROGRESS, false);
        btnStartWorkout.setText(inProgress && "deliberate".equals(prefs.getString(KEY_SAVE_TYPE, "")) ? "Resume Workout" : "Start Workout");
    }

    private void setupClickListeners() {
        btnAddExercise.setOnClickListener(v -> {
            if (isRestDay) return;
            if (exerciseList.isEmpty()) selectedWorkout = "Custom Plan";
            saveWorkoutsForDate(selectedDate);
            WorkoutFragment wf = WorkoutFragment.newInstance(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, wf).addToBackStack(null).commit();
            ((BottomNavigationView)requireActivity().findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.nav_workout);
        });

        btnStartWorkout.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            if (prefs.getBoolean(KEY_WORKOUT_IN_PROGRESS, false) && "deliberate".equals(prefs.getString(KEY_SAVE_TYPE, ""))) {
                showResumeWorkoutDialog();
            } else {
                startWorkout();
            }
        });

        switchRestDay.setOnCheckedChangeListener((btn, isChecked) -> {
            if (btn.isPressed()) {
                isRestDay = isChecked;
                if (isRestDay) {
                    exerciseList.clear();
                    selectedWorkout = "Rest Day";
                } else {
                    selectedWorkout = "No Workout Planned";
                    selectedTime = globalDefaultTime;
                    selectedEquipment = globalDefaultEquipment;
                }
                saveWorkoutsForDate(selectedDate);
                updateGenericUI();
            }
        });

        btnSwap.setOnClickListener(v -> showSwapWorkoutBottomSheet());
        ivProfileIcon.setOnClickListener(v -> navigateToProfile(false));
        ivNotificationIcon.setOnClickListener(v -> navigateToProfile(true));
    }

    private void navigateToProfile(boolean isNotif) {
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.newInstance(isNotif)).addToBackStack(null).commit();
        ((BottomNavigationView)requireActivity().findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.nav_profile);
    }

    private void showResumeWorkoutDialog() {
        new AlertDialog.Builder(requireContext()).setTitle("Resume Workout").setMessage("Do you want to continue where you left off?")
                .setPositiveButton("Continue", (dialog, which) -> startActivity(new Intent(getActivity(), LiveSessionActivity.class)))
                .setNegativeButton("Discard", (dialog, which) -> {
                    requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
                    updateStartButton();
                }).show();
    }

    private void startWorkout() {
        if (exerciseList.isEmpty() || isRestDay) {
            Toast.makeText(getContext(), "No workout planned!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), LiveSessionActivity.class);
        intent.putExtra("exerciseList", (Serializable) exerciseList);
        intent.putExtra("userWeight", userWeight);
        intent.putExtra("selectedDate", selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        intent.putExtra("workoutDuration", parseDurationInMinutes(selectedTime));
        startActivity(intent);
    }

    private List<Exercise> parseExercisesFromMap(List<HashMap<String, Object>> map) {
        List<Exercise> list = new ArrayList<>();
        for (HashMap<String, Object> m : map) {
            try {
                Exercise e = new Exercise();
                e.setTitle((String) m.get("title"));
                if (m.get("sets") != null) e.setSets(((Number) m.get("sets")).intValue());
                if (m.get("reps") != null) e.setReps(((Number) m.get("reps")).intValue());
                if (m.get("kg") != null) e.setKg(((Number) m.get("kg")).intValue());
                e.setCategory(Exercise.Category.valueOf((String) m.get("category")));
                e.setBodyPart(Exercise.BodyPart.valueOf((String) m.get("bodyPart")));
                e.setMuscleTargets((List<String>) m.get("muscleTargets"));
                if (m.get("imageResId") != null) e.setImageResId(((Number) m.get("imageResId")).intValue());
                else e.setImageUrl((String) m.get("imageUrl"));
                if (m.get("met") != null) e.setMet(((Number) m.get("met")).doubleValue());
                e.setAddedToWorkout(true);
                if (e.getTitle() != null) list.add(e);
            } catch (Exception err) { Log.e(TAG, "Parse Error", err); }
        }
        return list;
    }

    private void initViews(View view) {
        rvCalendar = view.findViewById(R.id.rv_calendar);
        rvExercises = view.findViewById(R.id.rv_exercises);
        tvYear = view.findViewById(R.id.tv_year);
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivProfileIcon = view.findViewById(R.id.iv_profile_icon);
        ivNotificationIcon = view.findViewById(R.id.iv_notification_icon);
        tvTimeOption = view.findViewById(R.id.tv_time_option);
        tvEquipmentOption = view.findViewById(R.id.tv_equipment_option);
        switchRestDay = view.findViewById(R.id.switch_rest_day);
        tvWorkoutTitle = view.findViewById(R.id.tv_push_day);
        btnSwap = view.findViewById(R.id.btn_swap);
        workoutDetailsGroup = view.findViewById(R.id.workout_details_group);
        tvWorkoutSubtitle = view.findViewById(R.id.tv_workout_subtitle);
        btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        restDayCard = view.findViewById(R.id.rest_day_card);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);
        tvRestDayMessage = view.findViewById(R.id.tv_rest_day_message);
        mainScrollView = view.findViewById(R.id.main_scroll_view);
    }

    private void saveWorkoutsForDate(LocalDate date) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        Map<String, Object> data = new HashMap<>();
        data.put("isRestDay", isRestDay);
        data.put("workoutName", selectedWorkout);
        data.put("exercises", exerciseList.stream().map(this::exerciseToMap).collect(Collectors.toList()));
        data.put("time", selectedTime);
        data.put("equipment", selectedEquipment);
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).collection("workouts").document(date.format(DateTimeFormatter.ISO_LOCAL_DATE)).set(data);
    }

    private Map<String, Object> exerciseToMap(Exercise e) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", e.getTitle());
        map.put("sets", e.getSets());
        map.put("reps", e.getReps());
        map.put("kg", e.getKg());
        map.put("category", e.getCategory() != null ? e.getCategory().name() : null);
        map.put("bodyPart", e.getBodyPart() != null ? e.getBodyPart().name() : null);
        map.put("muscleTargets", e.getMuscleTargets());
        map.put("imageUrl", e.getImageUrl());
        map.put("imageResId", e.getImageResId());
        map.put("met", e.getMet());
        map.put("addedToWorkout", e.isAddedToWorkout());
        return map;
    }

    private void setupCalendar() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        if (registrationDate == null) registrationDate = today.minusMonths(1);
        long diff = ChronoUnit.DAYS.between(registrationDate, today);
        for (int i = 0; i < diff + 30; i++) dates.add(registrationDate.plusDays(i));
        int pos = dates.indexOf(selectedDate);
        calendarAdapter = new CalendarAdapter(dates, pos, this);
        rvCalendar.setAdapter(calendarAdapter);
        if (pos != -1) rvCalendar.post(() -> ((LinearLayoutManager)rvCalendar.getLayoutManager()).scrollToPositionWithOffset(pos, rvCalendar.getWidth()/2 - 40));
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
        workoutTypes.add(new WorkoutType("Push Day", R.drawable.ex_bench_press));
        workoutTypes.add(new WorkoutType("Pull Day", R.drawable.ex_pull_up));
        workoutTypes.add(new WorkoutType("Leg Day", R.drawable.ex_barbell_squat));
        workoutTypes.add(new WorkoutType("Full Body", R.drawable.ex_push_up));
        workoutTypes.add(new WorkoutType("Cardio & Core", R.drawable.ex_plank));
        workoutTypes.add(new WorkoutType("Custom Plan", R.drawable.template_custom));
    }

    private void showOptionsBottomSheet(String type) {
        boolean isTime = "time".equals(type);
        OptionsBottomSheetFragment sheet = OptionsBottomSheetFragment.newInstance(isTime ? "Duration" : "Equipment", isTime ? timeOptions : equipmentOptions, isTime ? selectedTime : selectedEquipment);
        sheet.setOnOptionSelectedListener(this);
        sheet.show(getParentFragmentManager(), "Options");
    }

    private void showSwapWorkoutBottomSheet() {
        SwapWorkoutBottomSheetFragment sheet = SwapWorkoutBottomSheetFragment.newInstance("Swap Workout", workoutTypes, selectedWorkout);
        sheet.setOnOptionSelectedListener(this);
        sheet.show(getParentFragmentManager(), "Swap");
    }

    private double parseDurationInMinutes(String time) {
        if (time == null || "Free Time".equalsIgnoreCase(time)) return 0;
        try {
            if (time.contains("hr")) {
                String[] p = time.toLowerCase().split("hr");
                double m = Double.parseDouble(p[0].trim()) * 60;
                if (p.length > 1 && p[1].contains("min")) m += Double.parseDouble(p[1].replace(",", "").replace("min", "").trim());
                return m;
            }
            return Double.parseDouble(time.replace("min", "").trim());
        } catch (Exception e) { return 0; }
    }

    @Override
    public void onOptionSelected(String option) {
        if (timeOptions.contains(option)) selectedTime = option;
        else if (equipmentOptions.contains(option)) selectedEquipment = option;
        else { swapWorkout(option); return; }
        saveWorkoutsForDate(selectedDate);
        updateGenericUI();
    }

    @Override
    public void onSetAsDefault(String option) {
        if (timeOptions.contains(option)) {
            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String old = prefs.getString(KEY_DEFAULT_TIME, "45 min");
            if (old.equals(option)) return;
            prefs.edit().putString(KEY_DEFAULT_TIME, option).apply();
            this.globalDefaultTime = option;
            this.selectedTime = option;
            updateWorkoutsToNewDefault(old, option, () -> {
                updateGenericUI();
                saveWorkoutsForDate(selectedDate);
                if (getContext() != null) Toast.makeText(getContext(), "Default time updated.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateWorkoutsToNewDefault(String old, String next, Runnable onComplete) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).collection("workouts").whereGreaterThanOrEqualTo(FieldPath.documentId(), today).whereEqualTo("time", old).get().addOnSuccessListener(snaps -> {
            if (snaps.isEmpty()) { onComplete.run(); return; }
            WriteBatch batch = db.batch();
            for (DocumentSnapshot d : snaps) batch.update(d.getReference(), "time", next);
            batch.commit().addOnSuccessListener(aVoid -> onComplete.run()).addOnFailureListener(e -> onComplete.run());
        }).addOnFailureListener(e -> onComplete.run());
    }

    private void swapWorkout(String name) {
        selectedWorkout = name;
        exerciseList.clear();
        WorkoutPlan plan = WorkoutPlanDatabase.getWorkoutPlan(name);
        if (plan != null) exerciseList.addAll(plan.getExercises());
        isRestDay = false;
        saveWorkoutsForDate(selectedDate);
        updateGenericUI();
    }

    @Override
    public void onDateClick(int pos) {
        selectedDate = calendarAdapter.getDateAt(pos);
        calendarAdapter.setSelectedPosition(pos);
        if (mListener != null) mListener.onDateSelected(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Override
    public void onSave(Exercise ex, int pos) {
        exerciseList.set(pos, ex);
        saveWorkoutsForDate(selectedDate);
        updateGenericUI();
    }

    @Override
    public void onRemove(Exercise ex, int pos) {
        exerciseList.remove(pos);
        if (exerciseList.isEmpty()) {
            selectedWorkout = "No Workout Planned";
            selectedTime = globalDefaultTime;
            selectedEquipment = globalDefaultEquipment;
        } else selectedWorkout = "Custom Plan";
        saveWorkoutsForDate(selectedDate);
        updateGenericUI();
    }

    @Override
    public void onMoreClick(Exercise ex, int pos) {
        new EditExerciseDialog(ex, pos, this).show(getChildFragmentManager(), "Edit");
    }
}