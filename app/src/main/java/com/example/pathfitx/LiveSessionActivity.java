package com.example.pathfitx;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_session);

        tvTimer = findViewById(R.id.tv_timer);
        tvProgressText = findViewById(R.id.tv_progress_text);
        progressBar = findViewById(R.id.progress_bar);
        btnPause = findViewById(R.id.btn_pause);
        rvLiveWorkout = findViewById(R.id.rv_live_workout);

        setupTimer();
        setupListAndProgress();

        findViewById(R.id.btn_finish).setOnClickListener(v -> finish());
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

    private void setupListAndProgress() {
        List<Exercise> workoutList = (List<Exercise>) getIntent().getSerializableExtra("exerciseList");
        if (workoutList == null) {
            workoutList = new ArrayList<>(); // Avoid null pointer exception
        }

        // 1. Calculate Total Sets
        totalSets = 0;
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
}
