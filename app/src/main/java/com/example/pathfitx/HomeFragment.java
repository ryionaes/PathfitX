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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    private List<Exercise> exerciseList = new ArrayList<>();
    private LocalDate selectedDate;

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
            throw new RuntimeException(context.toString() + " must implement OnDateSelectedListener");
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
        setupCalendar();
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

        sharedViewModel.getUserSnapshot().observe(getViewLifecycleOwner(), userSnapshot -> {
            if (userSnapshot != null && userSnapshot.exists()) {
                if (userSnapshot.contains("defaultEquipment")) globalDefaultEquipment = userSnapshot.getString("defaultEquipment");
                if (userSnapshot.contains("weight_kg")) userWeight = userSnapshot.getDouble("weight_kg");

                tvUserName.setText(userSnapshot.getString("username") != null ? userSnapshot.getString("username") : "Fitness User");
                String img = userSnapshot.getString("profileImageUri");
                if (ivProfileIcon != null) {
                    Glide.with(this).load(img != null && !img.isEmpty() ? Uri.parse(img) : R.drawable.ic_profile_default).circleCrop().into(ivProfileIcon);
                }

                userDefaultsLoaded = true;
                updateWorkoutUI(sharedViewModel.getWorkoutSnapshot().getValue());
            }
        });

        sharedViewModel.getWorkoutSnapshot().observe(getViewLifecycleOwner(), snapshot -> {
            if (userDefaultsLoaded) {
                updateWorkoutUI(snapshot);
            }
        });
    }

    private void updateWorkoutUI(DocumentSnapshot snapshot) {
        if (!isAdded()) return;

        exerciseList.clear();
        if (snapshot != null && snapshot.exists()) {
            isRestDay = snapshot.getBoolean("isRestDay") != null && snapshot.getBoolean("isRestDay");

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

        boolean hasWorkout = exerciseList != null && !exerciseList.isEmpty();
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

                String timeDisplay = formatMinutesToReadableTime((int) totalDurationMinutes);
                if ("Free Time".equalsIgnoreCase(selectedTime)) {
                    tvWorkoutSubtitle.setText(String.format(Locale.getDefault(), "%d Exercises • %d Muscles", exerciseList.size(), muscles.size()));
                } else {
                    tvWorkoutSubtitle.setText(String.format(Locale.getDefault(), "%d Exercises • %s • %.1f Cal", exerciseList.size(), timeDisplay, totalCalories));
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
        boolean workoutInProgress = prefs.getBoolean(KEY_WORKOUT_IN_PROGRESS, false);
        String saveType = prefs.getString(KEY_SAVE_TYPE, "");

        if (workoutInProgress && "deliberate".equals(saveType)) {
            btnStartWorkout.setText("Resume Workout");
        } else {
            btnStartWorkout.setText("Start Workout");
        }
    }


    private String formatMinutesToReadableTime(int totalMinutes) {
        if (totalMinutes < 60) {
            return totalMinutes + " min";
        } else {
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            return (minutes == 0) ? hours + " hr" : hours + " hr, " + minutes + " min";
        }
    }

    private void setupClickListeners() {
        btnAddExercise.setOnClickListener(v -> {
            if (isRestDay) return;
            if (exerciseList.isEmpty()) selectedWorkout = "Custom Plan";
            saveWorkoutsForDate(selectedDate);

            WorkoutFragment wf = WorkoutFragment.newInstance(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, wf).addToBackStack(null).commit();
            BottomNavigationView nav = getActivity().findViewById(R.id.bottom_navigation);
            if (nav != null) nav.setSelectedItemId(R.id.nav_workout);
        });

        btnStartWorkout.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean workoutInProgress = prefs.getBoolean(KEY_WORKOUT_IN_PROGRESS, false);
            String saveType = prefs.getString(KEY_SAVE_TYPE, "");

            if (workoutInProgress && "deliberate".equals(saveType)) {
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
                    selectedWorkout = exerciseList.isEmpty() ? "No Workout Planned" : "Custom Plan";
                }
                saveWorkoutsForDate(selectedDate);
                updateGenericUI();
            }
        });

        btnSwap.setOnClickListener(v -> showSwapWorkoutBottomSheet());
        ivProfileIcon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.newInstance(false)).addToBackStack(null).commit();
            BottomNavigationView nav = getActivity().findViewById(R.id.bottom_navigation);
            if (nav != null) nav.setSelectedItemId(R.id.nav_profile);
        });
    }

    private void showResumeWorkoutDialog() {
        new AlertDialog.Builder(getContext())
            .setTitle("Resume Workout")
            .setMessage("Do you want to continue where you left off?")
            .setPositiveButton("Continue", (dialog, which) -> {
                startActivity(new Intent(getActivity(), LiveSessionActivity.class));
            })
            .setNegativeButton("Discard", (dialog, which) -> {
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                updateStartButton();
            })
            .show();
    }

    private void startWorkout() {
        if (exerciseList == null || exerciseList.isEmpty() || isRestDay) {
            Toast.makeText(getContext(), "No workout planned for today!", Toast.LENGTH_SHORT).show();
            return;
        }

        double durationInMinutes = parseDurationInMinutes(selectedTime);
        Intent intent = new Intent(getActivity(), LiveSessionActivity.class);
        intent.putExtra("exerciseList", (Serializable) exerciseList);
        intent.putExtra("userWeight", userWeight);
        intent.putExtra("selectedDate", selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        intent.putExtra("workoutDuration", durationInMinutes);
        startActivity(intent);
    }

    private List<Exercise> parseExercisesFromMap(List<HashMap<String, Object>> map) {
        List<Exercise> list = new ArrayList<>();
        for (HashMap<String, Object> m : map) {
            try {
                Exercise e = new Exercise();
                e.setTitle((String) m.get("title"));
                if (m.get("sets") != null) e.setSets(((Long) m.get("sets")).intValue());
                if (m.get("reps") != null) e.setReps(((Long) m.get("reps")).intValue());
                if (m.get("kg") != null) e.setKg(((Long) m.get("kg")).intValue());
                if (m.get("category") != null) e.setCategory(Exercise.Category.valueOf((String) m.get("category")));
                if (m.get("bodyPart") != null) e.setBodyPart(Exercise.BodyPart.valueOf((String) m.get("bodyPart")));
                if (m.get("muscleTargets") != null) e.setMuscleTargets((List<String>) m.get("muscleTargets"));

                if (m.get("imageResId") != null) {
                    e.setImageResId(((Long) m.get("imageResId")).intValue());
                } else if (m.get("imageUrl") != null) {
                    e.setImageUrl((String) m.get("imageUrl"));
                }

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
    }

    private void saveWorkoutsForDate(LocalDate date) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String dateId = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Map<String, Object> data = new HashMap<>();
        data.put("isRestDay", isRestDay);
        data.put("workoutName", selectedWorkout);
        data.put("exercises", exerciseList);
        data.put("time", selectedTime);
        data.put("equipment", selectedEquipment);
        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .collection("workouts").document(dateId).set(data);
    }

    private void setupCalendar() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<LocalDate> dates = new ArrayList<>();
        LocalDate start = LocalDate.now().minusMonths(1);
        for (int i = 0; i < 90; i++) dates.add(start.plusDays(i));
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
        boolean isTime = type.equals("time");
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
        if (timeOptions.contains(option)) {
            selectedTime = option;
        } else if (equipmentOptions.contains(option)) {
            selectedEquipment = option;
        } else {
            swapWorkout(option);
            return;
        }
        saveWorkoutsForDate(selectedDate);
        updateGenericUI();
    }

    @Override
    public void onSetAsDefault(String option) {
        if (timeOptions.contains(option)) {
            SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String oldDefaultTime = prefs.getString(KEY_DEFAULT_TIME, "45 min"); // Capture the old default
            String newDefaultTime = option;

            if (oldDefaultTime.equals(newDefaultTime)) { // No change needed
                selectedTime = newDefaultTime;
                updateGenericUI();
                saveWorkoutsForDate(selectedDate);
                return;
            }

            // 1. Save new default to SharedPreferences
            prefs.edit().putString(KEY_DEFAULT_TIME, newDefaultTime).apply();

            // 2. Update local variables
            this.globalDefaultTime = newDefaultTime;
            this.selectedTime = newDefaultTime;

            // 3. Batch update Firestore for future workouts
            updateWorkoutsToNewDefault(oldDefaultTime, newDefaultTime);

            // 4. Update UI and save current day
            updateGenericUI();
            saveWorkoutsForDate(selectedDate);

            Toast.makeText(getContext(), "Default time updated for all workouts.", Toast.LENGTH_SHORT).show();

        } 
    }

    private void updateWorkoutsToNewDefault(String oldDefault, String newDefault) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).collection("workouts")
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), today) // Correctly query by document ID
                .whereEqualTo("time", oldDefault)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "No future workouts found with the old default time. Nothing to update.");
                        return;
                    }

                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.update(doc.getReference(), "time", newDefault);
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Successfully updated " + queryDocumentSnapshots.size() + " workouts to the new default time.");
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error committing batch update for default time", e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error finding workouts with old default time", e);
                });
    }

    private void swapWorkout(String workoutName) {
        selectedWorkout = workoutName;
        exerciseList.clear();
        WorkoutPlan plan = WorkoutPlanDatabase.getWorkoutPlan(workoutName);
        if (plan != null) {
            exerciseList.addAll(plan.getExercises());
        }
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
    public void onSave(Exercise ex, int pos) { exerciseList.set(pos, ex); saveWorkoutsForDate(selectedDate); updateGenericUI(); }

    @Override
    public void onRemove(Exercise ex, int pos) {
        exerciseList.remove(pos);
        selectedWorkout = exerciseList.isEmpty() ? "No Workout Planned" : "Custom Plan";
        saveWorkoutsForDate(selectedDate);
        updateGenericUI();
    }

    @Override
    public void onMoreClick(Exercise ex, int pos) { new EditExerciseDialog(ex, pos, this).show(getChildFragmentManager(), "Edit"); }
}
