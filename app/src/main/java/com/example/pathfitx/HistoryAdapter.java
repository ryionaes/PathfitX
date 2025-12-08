package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<WorkoutHistory> historyList;
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onViewClick(WorkoutHistory history);
    }

    public HistoryAdapter(List<WorkoutHistory> historyList) {
        this.historyList = historyList;
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutHistory item = historyList.get(position);
        holder.tvTitle.setText(item.getWorkoutName());

        int minutes = item.getDurationSeconds() / 60;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String dateStr = sdf.format(new Date(item.getTimestamp()));

        holder.tvDate.setText(dateStr + " • " + minutes + " min");

        holder.btnViewHistory.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        MaterialButton btnViewHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            btnViewHistory = itemView.findViewById(R.id.btnViewHistory);
        }
    }
}
