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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private SharedViewModel sharedViewModel;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getHistorySnapshot().observe(getViewLifecycleOwner(), this::updateHistoryUI);
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

    private void updateHistoryUI(QuerySnapshot snapshots) {
        if (isAdded() && getContext() != null && snapshots != null) {
            historyList.clear();
            long totalVol = 0;
            int totalWorkouts = 0;
            double totalCalories = 0;

            for (QueryDocumentSnapshot document : snapshots) {
                WorkoutHistory item = document.toObject(WorkoutHistory.class);
                item.setDocumentId(document.getId()); // Store document ID
                historyList.add(item);

                totalVol += item.getTotalVolume();
                totalWorkouts++;
                totalCalories += item.getCaloriesBurned();
            }

            adapter.notifyDataSetChanged();

            tvVolumeValue.setText(String.format("%,d", totalVol));
            valWorkouts.setText(String.valueOf(totalWorkouts));
            valCalories.setText(String.format("%,.0f", totalCalories));
        }
    }

    private void showWorkoutSummaryDialog(WorkoutHistory history) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_workout_summary, null);

        final EditText etWorkoutTitle = view.findViewById(R.id.etWorkoutTitle);
        final TextView tvWorkoutTitle = view.findViewById(R.id.tvWorkoutTitle);
        final ImageView ivEditIcon = view.findViewById(R.id.ivEditIcon);

        tvWorkoutTitle.setText(history.getWorkoutName());
        etWorkoutTitle.setText(history.getWorkoutName());

        tvWorkoutTitle.setVisibility(View.VISIBLE);
        etWorkoutTitle.setVisibility(View.GONE);
        ivEditIcon.setVisibility(View.VISIBLE); // Show edit icon

        ivEditIcon.setOnClickListener(v -> {
            tvWorkoutTitle.setVisibility(View.GONE);
            etWorkoutTitle.setVisibility(View.VISIBLE);
            etWorkoutTitle.requestFocus();
        });

        ((TextView) view.findViewById(R.id.tvDurationValue)).setText((history.getDurationSeconds() / 60) + " min");
        ((TextView) view.findViewById(R.id.tvVolumeValue)).setText(history.getTotalVolume() + " kg");
        ((TextView) view.findViewById(R.id.tvExercisesCompletedValue)).setText(String.valueOf(history.getExercisesCount()));
        ((TextView) view.findViewById(R.id.tvCompletionRateValue)).setText(history.getCompletionRate() + "%");
        ((TextView) view.findViewById(R.id.tvCaloriesBurnedValue)).setText(String.format("%.0f", history.getCaloriesBurned()));

        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        View.OnClickListener closeAction = v -> {
            String newName = etWorkoutTitle.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(history.getWorkoutName())) {
                updateWorkoutName(history, newName);
            }
            dialog.dismiss();
        };

        view.findViewById(R.id.btnClose).setOnClickListener(closeAction);
        view.findViewById(R.id.btnCloseSummary).setOnClickListener(closeAction);
    }

    private void updateWorkoutName(WorkoutHistory history, String newName) {
        if (history.getDocumentId() == null || sharedViewModel.getUserId() == null) {
            Toast.makeText(getContext(), "Error: Cannot update workout name.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .document(sharedViewModel.getUserId())
                .collection("history")
                .document(history.getDocumentId())
                .update("workoutName", newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Workout name updated.", Toast.LENGTH_SHORT).show();
                    history.setWorkoutName(newName);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update name.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating workout name", e);
                });
    }
}
