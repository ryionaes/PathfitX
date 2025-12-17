package com.example.pathfitx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LiveSessionActivity extends AppCompatActivity {

    private static final String TAG = "LiveSessionActivity";

    // UI
    private TextView tvTimer, tvProgressText;
    private ProgressBar progressBar;
    private MaterialButton btnPause, btnFinish;
    private RecyclerView rvLiveWorkout;

    // Timer
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private boolean isRunning = true;
    private boolean isCountdown = false;
    private long countdownTime = 0L;

    // Progress
    private int totalSets = 0;
    private int completedSets = 0;
    private List<Exercise> workoutList;
    private String workoutName = "My Workout";
    private double userWeight = 70.0; // Default
    private String selectedDate;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;

    // For saving workout state
    private static final String PREFS_NAME = "LiveSessionPrefs";
    private static final String KEY_WORKOUT_IN_PROGRESS = "workout_in_progress";
    private static final String KEY_WORKOUT_LIST = "workout_list";
    private static final String KEY_TIME_SWAP_BUFF = "time_swap_buff";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_WORKOUT_NAME = "workout_name";
    private static final String KEY_USER_WEIGHT = "user_weight";
    private static final String KEY_SELECTED_DATE = "selected_date";
    private static final String KEY_IS_COUNTDOWN = "is_countdown";
    private static final String KEY_COUNTDOWN_TIME = "countdown_time";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_WORKOUT_IN_PROGRESS, false)) {
            showResumeWorkoutDialog();
        } else {
            initializeNewWorkout();
        }
    }

    private void initializeNewWorkout() {
        initFirebase();
        initViews();

        if (userId == null) {
            Toast.makeText(this, "Error: User not logged in. Cannot save progress.", Toast.LENGTH_LONG).show();
            finish(); // Close activity
            return;
        }

        workoutList = (List<Exercise>) getIntent().getSerializableExtra("exerciseList");
        userWeight = getIntent().getDoubleExtra("userWeight", 70.0);
        selectedDate = getIntent().getStringExtra("selectedDate");
        double workoutDuration = getIntent().getDoubleExtra("workoutDuration", 0.0);

        if (workoutDuration > 0) {
            isCountdown = true;
            countdownTime = (long) (workoutDuration * 60 * 1000);
        }

        if (getIntent().hasExtra("workoutName")) {
            workoutName = getIntent().getStringExtra("workoutName");
        }

        setupTimer();
        setupListAndProgress();

        btnFinish.setOnClickListener(v -> finishWorkout());
    }

    private void showResumeWorkoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Resume Workout")
                .setMessage("You have an unfinished workout. Do you want to continue?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    resumeSavedWorkout();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    clearSavedWorkoutState();
                    initializeNewWorkout(); // Start a fresh one
                })
                .setCancelable(false)
                .show();
    }

    private void resumeSavedWorkout() {
        initFirebase();
        initViews();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String serializedList = prefs.getString(KEY_WORKOUT_LIST, null);
        if (serializedList != null) {
            workoutList = deserializeWorkoutList(serializedList);
        } else {
            clearSavedWorkoutState();
            initializeNewWorkout();
            return;
        }

        // Recalculate completed sets from the ground truth (the workout list)
        completedSets = 0;
        if (workoutList != null) {
            for (Exercise ex : workoutList) {
                completedSets += ex.getCompletedSets();
            }
        }

        timeSwapBuff = prefs.getLong(KEY_TIME_SWAP_BUFF, 0L);
        startTime = prefs.getLong(KEY_START_TIME, System.currentTimeMillis());
        workoutName = prefs.getString(KEY_WORKOUT_NAME, "My Workout");
        userWeight = Double.longBitsToDouble(prefs.getLong(KEY_USER_WEIGHT, Double.doubleToRawLongBits(70.0)));
        selectedDate = prefs.getString(KEY_SELECTED_DATE, null);
        isCountdown = prefs.getBoolean(KEY_IS_COUNTDOWN, false);
        countdownTime = prefs.getLong(KEY_COUNTDOWN_TIME, 0L);

        setupTimer();
        setupListAndProgress();
        btnFinish.setOnClickListener(v -> finishWorkout());

        resumeWorkout();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isRunning) { // Only save if the workout is running
            saveWorkoutState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_WORKOUT_IN_PROGRESS, false)) {
            resumeWorkout();
        }
    }

    private void saveWorkoutState() {
        if (workoutList == null || workoutList.isEmpty()) {
            return;
        }

        pauseWorkout(); // Pause timer before saving

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(KEY_WORKOUT_IN_PROGRESS, true);
        editor.putString(KEY_WORKOUT_LIST, serializeWorkoutList(workoutList));
        editor.putLong(KEY_TIME_SWAP_BUFF, timeSwapBuff);
        editor.putLong(KEY_START_TIME, startTime);
        editor.putString(KEY_WORKOUT_NAME, workoutName);
        editor.putLong(KEY_USER_WEIGHT, Double.doubleToRawLongBits(userWeight));
        editor.putString(KEY_SELECTED_DATE, selectedDate);
        editor.putBoolean(KEY_IS_COUNTDOWN, isCountdown);
        editor.putLong(KEY_COUNTDOWN_TIME, countdownTime);

        editor.commit(); // Use commit for synchronous save
        Log.d(TAG, "Workout state saved.");
    }

    private void clearSavedWorkoutState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit(); // Use commit for synchronous save
        Log.d(TAG, "Saved workout state cleared.");
    }

    private String serializeWorkoutList(List<Exercise> list) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            oos.close();
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(TAG, "Error serializing workout list", e);
            return null;
        }
    }

    private List<Exercise> deserializeWorkoutList(String serializedList) {
        try {
            byte[] data = Base64.decode(serializedList, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            @SuppressWarnings("unchecked")
            List<Exercise> list = (List<Exercise>) ois.readObject();
            ois.close();
            return list;
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "Error deserializing workout list", e);
            return null;
        }
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Log.e(TAG, "User is not authenticated.");
        }
    }

    private void initViews(){
        tvTimer = findViewById(R.id.tv_timer);
        tvProgressText = findViewById(R.id.tv_progress_text);
        progressBar = findViewById(R.id.progress_bar);
        btnPause = findViewById(R.id.btn_pause);
        rvLiveWorkout = findViewById(R.id.rv_live_workout);
        btnFinish = findViewById(R.id.btn_finish);
    }


    private void setupTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        // Don't start the timer immediately, wait for resumeWorkout to be called
        btnPause.setOnClickListener(v -> {
            if (isRunning) {
                timeSwapBuff += System.currentTimeMillis() - startTime;
                timerHandler.removeCallbacks(updateTimerThread);
                btnPause.setIconResource(android.R.drawable.ic_media_play);
                isRunning = false;
                pauseWorkout();
            } else {
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(updateTimerThread, 0);
                btnPause.setIconResource(android.R.drawable.ic_media_pause);
                isRunning = true;
                resumeWorkout();
            }
        });
    }

    private void pauseWorkout() {
        if (isRunning) {
            timeSwapBuff += System.currentTimeMillis() - startTime;
            timerHandler.removeCallbacks(updateTimerThread);
            btnPause.setIconResource(android.R.drawable.ic_media_play);
            isRunning = false;
        }
    }

    private void resumeWorkout() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(updateTimerThread, 0);
            btnPause.setIconResource(android.R.drawable.ic_media_pause);
            isRunning = true;
        }
    }

    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            if (isCountdown) {
                long timeInMilliseconds = System.currentTimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMilliseconds;
                long remainingTime = countdownTime - updateTime;

                if (remainingTime <= 0) {
                    finishWorkout();
                    return;
                }
                int secs = (int) (remainingTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
            } else {
                long timeInMilliseconds = System.currentTimeMillis() - startTime;
                updateTime = timeSwapBuff + timeInMilliseconds;

                int secs = (int) (updateTime / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void setupListAndProgress() {
        if (workoutList == null) {
            workoutList = new ArrayList<>();
        }

        totalSets = 0;
        for (Exercise ex : workoutList) {
            totalSets += ex.getSets();
        }
        progressBar.setMax(totalSets);

        LiveAdapter adapter = new LiveAdapter(workoutList, (exercise, isChecked) -> {
            if (isChecked) {
                completedSets++;
                exercise.setCompletedSets(exercise.getCompletedSets() + 1);
            } else {
                completedSets--;
                exercise.setCompletedSets(exercise.getCompletedSets() - 1);
            }
            updateProgress();
        });


        rvLiveWorkout.setLayoutManager(new LinearLayoutManager(this));
        rvLiveWorkout.setAdapter(adapter);
        updateProgress();
    }

    private void updateProgress() {
        progressBar.setProgress(completedSets);
        int percent = (totalSets > 0) ? (completedSets * 100 / totalSets) : 0;
        tvProgressText.setText("Progress  " + percent + "%");

        if (completedSets > 0) {
            btnFinish.setEnabled(true);
            btnFinish.setAlpha(1.0f);
        } else {
            btnFinish.setEnabled(false);
            btnFinish.setAlpha(0.5f);
        }
    }

    @Override
    public void onBackPressed() {
        final boolean wasRunning = isRunning;
        pauseWorkout();

        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    timerHandler.removeCallbacks(updateTimerThread);
                    clearSavedWorkoutState();
                    Toast.makeText(LiveSessionActivity.this, "Workout Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    if (wasRunning) {
                        resumeWorkout();
                    }
                    dialog.dismiss();
                })
                .setOnCancelListener(dialog -> {
                    if (wasRunning) {
                        resumeWorkout();
                    }
                })
                .show();
    }

    private void finishWorkout() {
        timerHandler.removeCallbacks(updateTimerThread);

        long finalDurationMillis;
        if (isRunning) {
            finalDurationMillis = timeSwapBuff + (System.currentTimeMillis() - startTime);
        } else {
            finalDurationMillis = timeSwapBuff;
        }
        isRunning = false;

        clearSavedWorkoutState();

        int durationSeconds = (int) (finalDurationMillis / 1000);
        int totalVolume = 0;
        double totalMet = 0;

        if (workoutList != null) {
            for (Exercise ex : workoutList) {
                totalVolume += (ex.getKg() * ex.getReps() * ex.getSets());
                totalMet += ex.getMet();
            }
        }

        double averageMet = workoutList.isEmpty() ? 0 : totalMet / workoutList.size();
        double caloriesBurned = ExerciseDatabase.calculateCalories(userWeight, averageMet, durationSeconds / 60.0);
        int completionRate = (totalSets > 0) ? (completedSets * 100 / totalSets) : 0;
        int exercisesCount = workoutList.isEmpty() ? 0 : workoutList.size();

        showSummaryDialog(durationSeconds, totalVolume, exercisesCount, completionRate, caloriesBurned);
    }

    private void showSummaryDialog(int durationSeconds, int totalVolume, int exercisesCount, int completionRate, double caloriesBurned) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_workout_summary, null);

        final EditText etWorkoutTitle = view.findViewById(R.id.etWorkoutTitle);
        final TextView tvWorkoutTitle = view.findViewById(R.id.tvWorkoutTitle);
        final ImageView ivEditIcon = view.findViewById(R.id.ivEditIcon);

        tvWorkoutTitle.setText(workoutName);
        etWorkoutTitle.setText(workoutName);

        tvWorkoutTitle.setVisibility(View.VISIBLE);
        etWorkoutTitle.setVisibility(View.GONE);

        ivEditIcon.setOnClickListener(v -> {
            tvWorkoutTitle.setVisibility(View.GONE);
            etWorkoutTitle.setVisibility(View.VISIBLE);
            etWorkoutTitle.requestFocus();
        });

        ((TextView) view.findViewById(R.id.tvDurationValue)).setText((durationSeconds / 60) + " min");
        ((TextView) view.findViewById(R.id.tvVolumeValue)).setText(totalVolume + " kg");
        ((TextView) view.findViewById(R.id.tvExercisesCompletedValue)).setText(String.valueOf(exercisesCount));
        ((TextView) view.findViewById(R.id.tvCompletionRateValue)).setText(completionRate + "%");
        ((TextView) view.findViewById(R.id.tvCaloriesBurnedValue)).setText(String.format(Locale.getDefault(), "%.1f", caloriesBurned));

        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        View.OnClickListener closeAction = v -> {
            String finalName = etWorkoutTitle.getText().toString().trim();
            if (!finalName.isEmpty()) {
                workoutName = finalName;
            }
            saveWorkoutToHistory(durationSeconds, totalVolume, completionRate, exercisesCount, caloriesBurned);
            clearSavedWorkoutState();
            finish();
        };

        view.findViewById(R.id.btnClose).setOnClickListener(closeAction);
        view.findViewById(R.id.btnCloseSummary).setOnClickListener(closeAction);
    }

    private void saveWorkoutToHistory(int durationSeconds, int totalVolume, int completionRate, int exercisesCount, double caloriesBurned) {
        if (userId == null) {
            Log.e(TAG, "Cannot save history, user ID is null.");
            return;
        }

        Map<String, Object> historyData = new HashMap<>();
        historyData.put("workoutName", workoutName);
        historyData.put("durationSeconds", durationSeconds);
        historyData.put("totalVolume", totalVolume);
        historyData.put("completionRate", completionRate);
        historyData.put("exercisesCount", exercisesCount);
        historyData.put("caloriesBurned", caloriesBurned);
        historyData.put("timestamp", System.currentTimeMillis());
        historyData.put("date", selectedDate);

        // Add to the 'history' sub-collection
        db.collection("users").document(userId).collection("history")
                .add(historyData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Workout history saved with ID: " + documentReference.getId());
                    if (selectedDate != null) {
                        db.collection("users").document(userId).collection("workouts").document(selectedDate)
                                .update("caloriesBurned", caloriesBurned)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Calories burned updated for the day."))
                                .addOnFailureListener(e -> Log.e(TAG, "Error updating calories for the day", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error saving workout history", e));
    }
}
