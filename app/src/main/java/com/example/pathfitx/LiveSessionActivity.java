package com.example.pathfitx;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LiveSessionActivity extends AppCompatActivity {

    // UI
    private TextView tvTimer, tvProgressText;
    private MaterialButton btnPause;
    private ProgressBar progressBar;
    private RecyclerView rvLiveWorkout;

    // Timer Logic
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private boolean isRunning = true;

    // Progress Logic
    private int totalSets = 0;
    private int completedSets = 0;
    private List<Exercise> workoutList;
    private String workoutName = "My Workout"; // You might want to pass this in intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session);

        tvTimer = findViewById(R.id.tv_timer);
        tvProgressText = findViewById(R.id.tv_progress_text);
        progressBar = findViewById(R.id.progress_bar);
        btnPause = findViewById(R.id.btn_pause);
        rvLiveWorkout = findViewById(R.id.rv_live_workout);

        if (getIntent().hasExtra("workoutName")) {
            workoutName = getIntent().getStringExtra("workoutName");
        }

        setupTimer();
        setupListAndProgress();

        findViewById(R.id.btn_finish).setOnClickListener(v -> finishWorkout());
    }

    private void setupTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);

        btnPause.setOnClickListener(v -> {
            if (isRunning) {
                timeSwapBuff += System.currentTimeMillis() - startTime;
                timerHandler.removeCallbacks(updateTimerThread);
                btnPause.setIconResource(android.R.drawable.ic_media_play);
                isRunning = false;
            } else {
                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(updateTimerThread, 0);
                btnPause.setIconResource(android.R.drawable.ic_media_pause);
                isRunning = true;
            }
        });
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            long timeInMilliseconds = System.currentTimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;

            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void setupListAndProgress() {
        workoutList = (List<Exercise>) getIntent().getSerializableExtra("exerciseList");
        if (workoutList == null) {
            workoutList = new ArrayList<>(); 
        }

        // 1. Calculate Total and Completed Sets
        totalSets = 0;
        completedSets = 0;
        for (Exercise ex : workoutList) {
            totalSets += ex.getSets();
        }
        progressBar.setMax(totalSets);

        // 2. Setup Adapter with Listener
        LiveAdapter adapter = new LiveAdapter(workoutList, isCompleted -> {
            if (isCompleted) {
                completedSets++;
            } else {
                completedSets--;
            }
            updateProgress();
        });

        rvLiveWorkout.setLayoutManager(new LinearLayoutManager(this));
        rvLiveWorkout.setAdapter(adapter);
        updateProgress(); // Initial progress update
    }

    private void updateProgress() {
        progressBar.setProgress(completedSets);
        int percent = (totalSets > 0) ? (completedSets * 100 / totalSets) : 0;
        tvProgressText.setText("Progress  " + percent + "%");
    }

    private void finishWorkout() {
        // Stop timer
        timeSwapBuff += System.currentTimeMillis() - startTime;
        timerHandler.removeCallbacks(updateTimerThread);
        isRunning = false;

        // Calculate stats
        int durationSeconds = (int) (updateTime / 1000);
        int totalVolume = 0;
        
        for (Exercise ex : workoutList) {
             totalVolume += (ex.getKg() * ex.getReps() * ex.getSets()); 
        }

        int completionRate = (totalSets > 0) ? (completedSets * 100 / totalSets) : 0;
        int exercisesCount = workoutList.size();

        showSummaryDialog(durationSeconds, totalVolume, exercisesCount, completionRate);
        // We will save inside the dialog's Close button or when name is finalized if needed.
        // But for simplicity, we save here with current name, but we might want to update it if user edits it.
        // Better: Pass the data to saveWorkoutToHistory but call it AFTER dialog closes or when user confirms.
        // However, standard UX is save immediately or save on close.
        // Let's modify showSummaryDialog to handle saving after editing.
    }

    private void showSummaryDialog(int durationSeconds, int totalVolume, int exercisesCount, int completionRate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_workout_summary, null);

        // EDITABLE MODE
        EditText etTitle = view.findViewById(R.id.etWorkoutTitle);
        TextView tvTitle = view.findViewById(R.id.tvWorkoutTitle);
        ImageView ivEdit = view.findViewById(R.id.ivEditIcon);

        etTitle.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        ivEdit.setVisibility(View.VISIBLE);
        
        etTitle.setText(workoutName);

        ((TextView) view.findViewById(R.id.tvDurationValue)).setText((durationSeconds / 60) + " min");
        ((TextView) view.findViewById(R.id.tvVolumeValue)).setText(totalVolume + " kg");
        ((TextView) view.findViewById(R.id.tvExercisesCompletedValue)).setText(String.valueOf(exercisesCount));
        ((TextView) view.findViewById(R.id.tvCompletionRateValue)).setText(completionRate + "%");

        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        View.OnClickListener closeAction = v -> {
            // Update workout name from EditText before saving
            String finalName = etTitle.getText().toString().trim();
            if (!finalName.isEmpty()) {
                workoutName = finalName;
            }
            saveWorkoutToHistory(durationSeconds, totalVolume, completionRate, exercisesCount);
            dialog.dismiss();
            finish(); 
        };

        view.findViewById(R.id.btnClose).setOnClickListener(closeAction);
        view.findViewById(R.id.btnCloseSummary).setOnClickListener(closeAction);
    }

    private void saveWorkoutToHistory(int durationSeconds, int totalVolume, int completionRate, int exercisesCount) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = "testUser"; // Replace with actual user ID management
        String dateId = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateId = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
             dateId = String.valueOf(System.currentTimeMillis());
        }

        Map<String, Object> historyData = new HashMap<>();
        historyData.put("workoutName", workoutName);
        historyData.put("durationSeconds", durationSeconds);
        historyData.put("totalVolume", totalVolume);
        historyData.put("completionRate", completionRate);
        historyData.put("exercisesCount", exercisesCount);
        historyData.put("timestamp", System.currentTimeMillis());
        historyData.put("date", dateId);

        db.collection("users").document(userId).collection("history")
                .add(historyData) // Use add() to generate unique ID for each history entry
                .addOnSuccessListener(documentReference -> {
                    // Success
                })
                .addOnFailureListener(e -> {
                    // Failure
                });
    }
}
