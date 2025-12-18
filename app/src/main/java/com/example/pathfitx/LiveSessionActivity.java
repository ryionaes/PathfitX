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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

    // UI Elements
    private TextView tvTimer, tvProgressText;
    private ProgressBar progressBar;
    private MaterialButton btnPause, btnFinish;
    private RecyclerView rvLiveWorkout;
    private LiveAdapter liveAdapter;

    // Timer logic
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private boolean isRunning = true;
    private boolean isCountdown = false;
    private long countdownTime = 0L;

    // Workout Data
    private int totalSets = 0;
    private int completedSets = 0;
    private List<Exercise> workoutList;
    private String workoutName = "My Workout";
    private double userWeight = 70.0;
    private String selectedDate;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    private boolean isFinishingWorkout = false;

    // Constants for SharedPreferences (Persistence)
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String KEY_WORKOUT_IN_PROGRESS = "workout_in_progress";
    private static final String KEY_SAVE_TYPE = "save_type";
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
        initFirebase();
        initViews();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_WORKOUT_IN_PROGRESS, false)) {
            resumeSavedWorkout();
        } else {
            initializeNewWorkout();
        }
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) userId = currentUser.getUid();
    }

    private void initViews() {
        tvTimer = findViewById(R.id.tv_timer);
        tvProgressText = findViewById(R.id.tv_progress_text);
        progressBar = findViewById(R.id.progress_bar);
        btnPause = findViewById(R.id.btn_pause);
        rvLiveWorkout = findViewById(R.id.rv_live_workout);
        btnFinish = findViewById(R.id.btn_finish);
    }

    private void initializeNewWorkout() {
        if (userId == null) { finish(); return; }

        workoutList = (List<Exercise>) getIntent().getSerializableExtra("exerciseList");
        userWeight = getIntent().getDoubleExtra("userWeight", 70.0);
        selectedDate = getIntent().getStringExtra("selectedDate");
        double workoutDuration = getIntent().getDoubleExtra("workoutDuration", 0.0);

        if (workoutDuration > 0) {
            isCountdown = true;
            countdownTime = (long) (workoutDuration * 60 * 1000);
        }
        if (getIntent().hasExtra("workoutName")) workoutName = getIntent().getStringExtra("workoutName");

        setupTimer();
        setupListAndProgress();
        btnFinish.setOnClickListener(v -> finishWorkout());
    }

    private void setupTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);
        btnPause.setOnClickListener(v -> {
            if (isRunning) pauseWorkout(); else resumeWorkout();
        });
    }

    private void pauseWorkout() {
        if (isRunning) {
            timeSwapBuff += System.currentTimeMillis() - startTime;
            timerHandler.removeCallbacks(updateTimerThread);
            btnPause.setIconResource(android.R.drawable.ic_media_play);
            isRunning = false;
            if (liveAdapter != null) liveAdapter.setPaused(true);
        }
    }

    private void resumeWorkout() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(updateTimerThread, 0);
            btnPause.setIconResource(android.R.drawable.ic_media_pause);
            isRunning = true;
            if (liveAdapter != null) liveAdapter.setPaused(false);
        }
    }

    private final Runnable updateTimerThread = new Runnable() {
        public void run() {
            long timeInMilliseconds = System.currentTimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            long displayTime = isCountdown ? (countdownTime - updateTime) : updateTime;

            if (isCountdown && displayTime <= 0) {
                finishWorkout();
                return;
            }

            int totalSecs = (int) (displayTime / 1000);
            int hrs = totalSecs / 3600;
            int mins = (totalSecs % 3600) / 60;
            int secs = totalSecs % 60;

            if (hrs > 0) {
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs));
            } else {
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void setupListAndProgress() {
        if (workoutList == null) workoutList = new ArrayList<>();
        totalSets = 0;
        completedSets = 0;
        for (Exercise ex : workoutList) {
            totalSets += ex.getSets();
            completedSets += ex.getCompletedSets();
        }
        progressBar.setMax(totalSets);

        liveAdapter = new LiveAdapter(workoutList, (exercise, isChecked) -> {
            completedSets += isChecked ? 1 : -1;
            exercise.setCompletedSets(exercise.getCompletedSets() + (isChecked ? 1 : -1));
            updateProgress();
        });

        rvLiveWorkout.setLayoutManager(new LinearLayoutManager(this));
        rvLiveWorkout.setAdapter(liveAdapter);
        updateProgress();
    }

    private void updateProgress() {
        progressBar.setProgress(completedSets);
        int percent = (totalSets > 0) ? (completedSets * 100 / totalSets) : 0;
        tvProgressText.setText("Progress  " + percent + "%");
        btnFinish.setEnabled(completedSets > 0);
        btnFinish.setAlpha(completedSets > 0 ? 1.0f : 0.5f);
    }

    private void finishWorkout() {
        isFinishingWorkout = true;
        timerHandler.removeCallbacks(updateTimerThread);
        long finalMillis = isRunning ? (timeSwapBuff + (System.currentTimeMillis() - startTime)) : timeSwapBuff;
        isRunning = false;

        int durationSeconds = (int) (finalMillis / 1000);
        int totalVolume = 0;
        double totalMet = 0;
        for (Exercise ex : workoutList) {
            totalVolume += (ex.getKg() * ex.getReps() * ex.getCompletedSets());
            totalMet += ex.getMet();
        }
        double avgMet = workoutList.isEmpty() ? 0 : totalMet / workoutList.size();
        double caloriesBurned = ExerciseDatabase.calculateCalories(userWeight, avgMet, durationSeconds / 60.0);
        int completionRate = (totalSets > 0) ? (completedSets * 100 / totalSets) : 0;

        clearSavedWorkoutState();
        showSummaryDialog(durationSeconds, totalVolume, workoutList.size(), completionRate, caloriesBurned);
    }

    private void showSummaryDialog(int durationSeconds, int totalVolume, int exercisesCount, int completionRate, double caloriesBurned) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_workout_summary, null);

        final EditText etWorkoutTitle = view.findViewById(R.id.etWorkoutTitle);
        final TextView tvWorkoutTitle = view.findViewById(R.id.tvWorkoutTitle);
        final ImageView ivEditIcon = view.findViewById(R.id.ivEditIcon);

        tvWorkoutTitle.setText(workoutName);
        etWorkoutTitle.setText(workoutName);
        ivEditIcon.setOnClickListener(v -> {
            tvWorkoutTitle.setVisibility(View.GONE);
            etWorkoutTitle.setVisibility(View.VISIBLE);
            etWorkoutTitle.requestFocus();
        });

        int totalMins = durationSeconds / 60;
        String durationDisplay;
        if (totalMins < 60) {
            durationDisplay = totalMins + " min";
        } else {
            int h = totalMins / 60;
            int m = totalMins % 60;
            durationDisplay = (m == 0) ? h + " hr" : h + " hr, " + m + " min";
        }

        ((TextView) view.findViewById(R.id.tvDurationValue)).setText(durationDisplay);
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
            String finalTitle = etWorkoutTitle.getText().toString().trim();
            if (!finalTitle.isEmpty()) workoutName = finalTitle;
            saveWorkoutToHistory(durationSeconds, totalVolume, completionRate, exercisesCount, caloriesBurned);
            isFinishingWorkout = true;
            finish();
        };
        view.findViewById(R.id.btnClose).setOnClickListener(closeAction);
        view.findViewById(R.id.btnCloseSummary).setOnClickListener(closeAction);
    }

    private void saveWorkoutToHistory(int durationSeconds, int totalVolume, int completionRate, int exercisesCount, double caloriesBurned) {
        if (userId == null) return;
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("workoutName", workoutName);
        historyData.put("durationSeconds", durationSeconds);
        historyData.put("totalVolume", totalVolume);
        historyData.put("completionRate", completionRate);
        historyData.put("exercisesCount", exercisesCount);
        historyData.put("caloriesBurned", caloriesBurned);
        historyData.put("timestamp", System.currentTimeMillis());
        historyData.put("date", selectedDate);

        db.collection("users").document(userId).collection("history").add(historyData);
    }

    private void resumeSavedWorkout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        workoutName = prefs.getString(KEY_WORKOUT_NAME, "My Workout");
        userWeight = Double.longBitsToDouble(prefs.getLong(KEY_USER_WEIGHT, Double.doubleToLongBits(70.0)));
        selectedDate = prefs.getString(KEY_SELECTED_DATE, null);
        timeSwapBuff = prefs.getLong(KEY_TIME_SWAP_BUFF, 0L);
        isCountdown = prefs.getBoolean(KEY_IS_COUNTDOWN, false);
        countdownTime = prefs.getLong(KEY_COUNTDOWN_TIME, 0L);

        String serializedList = prefs.getString(KEY_WORKOUT_LIST, null);
        if (serializedList != null) {
            workoutList = deserializeWorkoutList(serializedList);
        }

        setupTimer();
        setupListAndProgress();
        btnFinish.setOnClickListener(v -> finishWorkout());
    }

    private void saveWorkoutState(String saveType) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(KEY_WORKOUT_IN_PROGRESS, true);
        editor.putString(KEY_SAVE_TYPE, saveType);
        editor.putString(KEY_WORKOUT_LIST, serializeWorkoutList(workoutList));
        editor.putLong(KEY_TIME_SWAP_BUFF, timeSwapBuff + (isRunning ? (System.currentTimeMillis() - startTime) : 0));
        editor.putString(KEY_WORKOUT_NAME, workoutName);
        editor.putLong(KEY_USER_WEIGHT, Double.doubleToLongBits(userWeight));
        editor.putString(KEY_SELECTED_DATE, selectedDate);
        editor.putBoolean(KEY_IS_COUNTDOWN, isCountdown);
        editor.putLong(KEY_COUNTDOWN_TIME, countdownTime);
        editor.apply();
    }

    private void clearSavedWorkoutState() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    private String serializeWorkoutList(List<Exercise> list) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            oos.close();
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(TAG, "Serialization error", e);
            return null;
        }
    }

    private List<Exercise> deserializeWorkoutList(String serializedList) {
        try {
            byte[] data = Base64.decode(serializedList, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            List<Exercise> list = (List<Exercise>) ois.readObject();
            ois.close();
            return list;
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "Deserialization error", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Exit Workout")
            .setMessage("Do you want to save your progress?")
            .setPositiveButton("Save & Exit", (dialogInterface, which) -> {
                isFinishingWorkout = true;
                saveWorkoutState("deliberate");
                finish();
            })
            .setNegativeButton("Discard & Exit", (dialogInterface, which) -> {
                isFinishingWorkout = true;
                clearSavedWorkoutState();
                finish();
            })
            .setNeutralButton("Cancel", null)
            .show();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(this, R.color.prim_red));

            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(Color.GRAY);

            Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutralButton.setTextColor(Color.GRAY);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishingWorkout && (isRunning || completedSets > 0)) {
            pauseWorkout();
            saveWorkoutState("unexpected");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(updateTimerThread);
    }
}
