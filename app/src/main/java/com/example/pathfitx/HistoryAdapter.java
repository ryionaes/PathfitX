package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<WorkoutHistory> list;
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(WorkoutHistory history);
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    public HistoryAdapter(List<WorkoutHistory> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gumagamit ng item_workout_history.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutHistory item = list.get(position);

        // Iset ang Workout Title
        holder.tvTitle.setText(item.getWorkoutName());

        // I-format ang Date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dateStr = (item.getTimestamp() != null) ? sdf.format(item.getTimestamp()) : "Unknown Date";

        // HOUR FORMAT SUPPORT para sa Duration
        int totalMinutes = item.getDurationSeconds() / 60;
        String durationStr;
        if (totalMinutes < 60) {
            durationStr = totalMinutes + " min";
        } else {
            int h = totalMinutes / 60;
            int m = totalMinutes % 60;
            durationStr = (m == 0) ? h + " hr" : h + " hr, " + m + " min";
        }

        // Pinagsama ang Date, Duration, at Volume sa tvHistoryDate dahil ito lang ang available na TextView
        String fullDetails = String.format(Locale.getDefault(), "%s • %s • %,d kg", dateStr, durationStr, item.getTotalVolume());
        holder.tvDate.setText(fullDetails);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHistoryItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ang mga ID na ito ay tugma sa iyong item_workout_history.xml
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
        }
    }
}