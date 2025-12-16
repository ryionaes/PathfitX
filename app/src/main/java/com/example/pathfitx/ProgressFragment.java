package com.example.pathfitx;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private static final String TAG = "ProgressFragment";

    // UI
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private TextView tvVolumeValue, valWorkouts, valCalories;

    // Data
    private List<WorkoutHistory> historyList;

    // Firebase
    private FirebaseFirestore db;
    private ListenerRegistration historyListener;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        attachHistoryListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (historyListener != null) {
            historyListener.remove();
        }
    }

    private void initViews(View view) {
        rvHistory = view.findViewById(R.id.rvHistory);
        tvVolumeValue = view.findViewById(R.id.tvVolumeValue);
        valWorkouts = view.findViewById(R.id.valWorkouts);
        valCalories = view.findViewById(R.id.valCalories);
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        adapter.setOnHistoryItemClickListener(this::showWorkoutSummaryDialog);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);
    }

    private void attachHistoryListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            handleSignedOutState();
            return;
        }
        String userId = currentUser.getUid();

        Query historyQuery = db.collection("users").document(userId).collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        historyListener = historyQuery.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error loading history data for user: " + userId, e);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load progress.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (isAdded() && getContext() != null && snapshots != null) {
                historyList.clear();
                long totalVol = 0;
                int totalWorkouts = 0;
                int totalCalories = 0;

                for (QueryDocumentSnapshot document : snapshots) {
                    WorkoutHistory item = document.toObject(WorkoutHistory.class);
                    historyList.add(item);

                    totalVol += item.getTotalVolume();
                    totalWorkouts++;
                    totalCalories += (item.getDurationSeconds() / 60) * 5; // Simple calorie estimation
                }

                adapter.notifyDataSetChanged();

                tvVolumeValue.setText(String.format("%,d", totalVol));
                valWorkouts.setText(String.valueOf(totalWorkouts));
                valCalories.setText(String.format("%,d", totalCalories));
            }
        });
    }

    private void handleSignedOutState() {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), "Please log in to see your progress.", Toast.LENGTH_LONG).show();
            historyList.clear();
            adapter.notifyDataSetChanged();
            tvVolumeValue.setText("0");
            valWorkouts.setText("0");
            valCalories.setText("0");
        }
    }

    private void showWorkoutSummaryDialog(WorkoutHistory history) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_workout_summary, null);

        view.findViewById(R.id.etWorkoutTitle).setVisibility(View.GONE);
        view.findViewById(R.id.ivEditIcon).setVisibility(View.GONE);
        view.findViewById(R.id.tvWorkoutTitle).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.tvWorkoutTitle)).setText(history.getWorkoutName());
        ((TextView) view.findViewById(R.id.tvDurationValue)).setText((history.getDurationSeconds() / 60) + " min");
        ((TextView) view.findViewById(R.id.tvVolumeValue)).setText(history.getTotalVolume() + " kg");
        ((TextView) view.findViewById(R.id.tvExercisesCompletedValue)).setText(String.valueOf(history.getExercisesCount()));
        ((TextView) view.findViewById(R.id.tvCompletionRateValue)).setText(history.getCompletionRate() + "%");

        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnCloseSummary).setOnClickListener(v -> dialog.dismiss());
    }
}
