package com.example.pathfitx;

import android.app.DatePickerDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProgressFragment extends Fragment {

    private static final String TAG = "ProgressFragment";

    // UI
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private TextView tvVolumeValue, valWorkouts, valCalories;
    private ImageView calendarIcon;

    // Data
    private List<WorkoutHistory> historyList;
    private SharedViewModel sharedViewModel;
    private Date registrationDate;

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
        sharedViewModel.getHistorySnapshot().observe(getViewLifecycleOwner(), this::updateTodayHistoryUI);
        sharedViewModel.getRegistrationDate().observe(getViewLifecycleOwner(), date -> {
            this.registrationDate = date;
        });

        calendarIcon.setOnClickListener(v -> showDatePickerDialog());
    }

    private void initViews(View view) {
        rvHistory = view.findViewById(R.id.rvHistory);
        tvVolumeValue = view.findViewById(R.id.tvVolumeValue);
        valWorkouts = view.findViewById(R.id.valWorkouts);
        valCalories = view.findViewById(R.id.valCalories);
        calendarIcon = view.findViewById(R.id.calendarIcon);
    }

    private void setupRecyclerView() {
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        adapter.setOnHistoryItemClickListener(this::showWorkoutSummaryDialog);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);
    }

    private void updateTodayHistoryUI(QuerySnapshot snapshots) {
        if (isAdded() && getContext() != null && snapshots != null) {
            historyList.clear();
            long totalVol = 0;
            int totalWorkouts = 0;
            double totalCalories = 0;

            Calendar today = Calendar.getInstance();

            for (QueryDocumentSnapshot document : snapshots) {
                WorkoutHistory item = createWorkoutHistoryFromSnapshot(document);

                if (item.getTimestamp() != null) {
                    Calendar itemDate = Calendar.getInstance();
                    itemDate.setTime(item.getTimestamp());

                    if (isSameDay(today, itemDate)) {
                        historyList.add(item);

                        totalVol += item.getTotalVolume();
                        totalWorkouts++;
                        totalCalories += item.getCaloriesBurned();
                    }
                } else {
                    Log.w(TAG, "WorkoutHistory item has null timestamp: " + document.getId());
                }
            }

            adapter.notifyDataSetChanged();

            tvVolumeValue.setText(String.format("%,d", totalVol));
            valWorkouts.setText(String.valueOf(totalWorkouts));
            valCalories.setText(String.format(Locale.getDefault(), "%.1f", totalCalories));
        }
    }

    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);
            fetchHistoryForDate(selectedDate);
        }, year, month, day);

        if (registrationDate != null) {
            datePickerDialog.getDatePicker().setMinDate(registrationDate.getTime());
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    private void fetchHistoryForDate(Calendar selectedDate) {
        sharedViewModel.getHistorySnapshot().observe(getViewLifecycleOwner(), snapshots -> {
            if (isAdded() && getContext() != null && snapshots != null) {
                List<WorkoutHistory> dailyHistoryList = new ArrayList<>();
                long totalVol = 0;
                int totalWorkouts = 0;
                double totalCalories = 0;

                for (QueryDocumentSnapshot document : snapshots) {
                    WorkoutHistory item = createWorkoutHistoryFromSnapshot(document);

                    if (item.getTimestamp() != null) {
                        Calendar itemDate = Calendar.getInstance();
                        itemDate.setTime(item.getTimestamp());

                        if (isSameDay(selectedDate, itemDate)) {
                            dailyHistoryList.add(item);
                            totalVol += item.getTotalVolume();
                            totalWorkouts++;
                            totalCalories += item.getCaloriesBurned();
                        }
                    } else {
                        Log.w(TAG, "WorkoutHistory item has null timestamp: " + document.getId());
                    }
                }
                showDailySummaryDialog(dailyHistoryList, totalVol, totalWorkouts, totalCalories);
            }
        });
    }

    private WorkoutHistory createWorkoutHistoryFromSnapshot(QueryDocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        String workoutName = (String) data.get("workoutName");

        Number durationSecondsNum = (Number) data.get("durationSeconds");
        int durationSeconds = (durationSecondsNum != null) ? durationSecondsNum.intValue() : 0;

        Number totalVolumeNum = (Number) data.get("totalVolume");
        int totalVolume = (totalVolumeNum != null) ? totalVolumeNum.intValue() : 0;

        Number completionRateNum = (Number) data.get("completionRate");
        int completionRate = (completionRateNum != null) ? completionRateNum.intValue() : 0;

        Number exercisesCountNum = (Number) data.get("exercisesCount");
        int exercisesCount = (exercisesCountNum != null) ? exercisesCountNum.intValue() : 0;

        Number caloriesBurnedNum = (Number) data.get("caloriesBurned");
        double caloriesBurned = (caloriesBurnedNum != null) ? caloriesBurnedNum.doubleValue() : 0.0;

        Object timestampObj = data.get("timestamp");
        Date timestamp = null;
        if (timestampObj instanceof com.google.firebase.Timestamp) {
            timestamp = ((com.google.firebase.Timestamp) timestampObj).toDate();
        } else if (timestampObj instanceof Long) {
            timestamp = new Date((Long) timestampObj);
        }

        WorkoutHistory history = new WorkoutHistory(workoutName, durationSeconds, totalVolume, completionRate, timestamp, exercisesCount, caloriesBurned);
        history.setDocumentId(document.getId());
        return history;
    }

    private void showDailySummaryDialog(List<WorkoutHistory> dailyHistory, long totalVol, int totalWorkouts, double totalCalories) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_daily_summary, null);

        ((TextView) view.findViewById(R.id.tvVolumeValue)).setText(String.format("%,d", totalVol));
        ((TextView) view.findViewById(R.id.valWorkouts)).setText(String.valueOf(totalWorkouts));
        ((TextView) view.findViewById(R.id.valCalories)).setText(String.format(Locale.getDefault(), "%.1f", totalCalories));

        RecyclerView rvDailyHistory = view.findViewById(R.id.rvDailyHistory);
        HistoryAdapter dailyAdapter = new HistoryAdapter(dailyHistory);
        dailyAdapter.setOnHistoryItemClickListener(this::showWorkoutSummaryDialog);
        rvDailyHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDailyHistory.setAdapter(dailyAdapter);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
    }


    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return false;
        }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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

        // --- BAGONG LOGIC DITO ---
        Calendar workoutDate = Calendar.getInstance();
        workoutDate.setTime(history.getTimestamp());
        Calendar today = Calendar.getInstance();

        boolean isToday = isSameDay(today, workoutDate);

        if (isToday) {
            ivEditIcon.setVisibility(View.VISIBLE); // Pwedeng i-edit kung ngayong araw lang
        } else {
            ivEditIcon.setVisibility(View.GONE);    // Itago ang edit icon kung nakalipas na ang araw
        }
        // -------------------------

        tvWorkoutTitle.setVisibility(View.VISIBLE);
        etWorkoutTitle.setVisibility(View.GONE);

        ivEditIcon.setOnClickListener(v -> {
            tvWorkoutTitle.setVisibility(View.GONE);
            etWorkoutTitle.setVisibility(View.VISIBLE);
            etWorkoutTitle.requestFocus();
        });

        // Ang rest ng code para sa TextViews (Duration, Volume, etc.) ay mananatiling pareho...
        ((TextView) view.findViewById(R.id.tvDurationValue)).setText((history.getDurationSeconds() / 60) + " min");
        ((TextView) view.findViewById(R.id.tvVolumeValue)).setText(history.getTotalVolume() + " kg");
        ((TextView) view.findViewById(R.id.tvExercisesCompletedValue)).setText(String.valueOf(history.getExercisesCount()));
        ((TextView) view.findViewById(R.id.tvCompletionRateValue)).setText(history.getCompletionRate() + "%");
        ((TextView) view.findViewById(R.id.tvCaloriesBurnedValue)).setText(String.format(Locale.getDefault(), "%.1f", history.getCaloriesBurned()));

        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        View.OnClickListener closeAction = v -> {
            String newName = etWorkoutTitle.getText().toString().trim();
            // Siguraduhin din na hindi mag-uupdate kung hindi naman "isToday"
            if (isToday && !newName.isEmpty() && !newName.equals(history.getWorkoutName())) {
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
