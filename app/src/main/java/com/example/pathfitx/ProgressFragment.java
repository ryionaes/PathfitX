package com.example.pathfitx;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private static final String TAG = "ProgressFragment";
    private static final String USER_PREFS_NAME = "UserPrefs";

    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private List<WorkoutHistory> historyList;
    private TextView tvVolumeValue, valWorkouts, valCalories;
    private FirebaseFirestore db;
    private String userId;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        tvVolumeValue = view.findViewById(R.id.tvVolumeValue);
        valWorkouts = view.findViewById(R.id.valWorkouts);
        valCalories = view.findViewById(R.id.valCalories);

        db = FirebaseFirestore.getInstance();
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);

        adapter.setOnHistoryItemClickListener(this::showWorkoutSummaryDialog);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);

        loadUserId();
        if (userId != null) {
            loadHistoryData();
        } else {
            // Handle the case where user ID is null (e.g., show a message)
            Toast.makeText(getContext(), "Error: User not logged in.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadUserId() {
        if (getContext() == null) return;
        SharedPreferences userPrefs = getContext().getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);
        userId = userPrefs.getString("USERNAME", null);
        if (userId == null) {
            Log.e(TAG, "User ID is null. Cannot load progress.");
        }
    }

    private void loadHistoryData() {
        db.collection("users").document(userId).collection("history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        historyList.clear();
                        long totalVol = 0;
                        int totalWorkouts = 0;
                        int totalCalories = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            WorkoutHistory item = document.toObject(WorkoutHistory.class);
                            historyList.add(item);

                            totalVol += item.getTotalVolume();
                            totalWorkouts++;
                            totalCalories += (item.getDurationSeconds() / 60) * 5;
                        }

                        adapter.notifyDataSetChanged();

                        tvVolumeValue.setText(String.format("%,d", totalVol));
                        valWorkouts.setText(String.valueOf(totalWorkouts));
                        valCalories.setText(String.format("%,d", totalCalories));
                    } else {
                        Log.e(TAG, "Error loading history data for user: " + userId, task.getException());
                    }
                });
    }

    private void showWorkoutSummaryDialog(WorkoutHistory history) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_workout_summary, null);

        // Explicitly set non-editable state
        EditText etTitle = view.findViewById(R.id.etWorkoutTitle);
        TextView tvTitle = view.findViewById(R.id.tvWorkoutTitle);
        ImageView ivEdit = view.findViewById(R.id.ivEditIcon);

        etTitle.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        ivEdit.setVisibility(View.GONE);

        // Set Data from History Item
        tvTitle.setText(history.getWorkoutName());
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
