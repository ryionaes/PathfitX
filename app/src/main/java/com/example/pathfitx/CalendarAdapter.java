package com.example.pathfitx;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final List<LocalDate> dates;
    private int selectedPosition;
    private final OnDateClickListener onDateClickListener;

    public interface OnDateClickListener {
        void onDateClick(int position);
    }

    public CalendarAdapter(List<LocalDate> dates, int selectedPosition, OnDateClickListener listener) {
        this.dates = dates;
        this.selectedPosition = selectedPosition;
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        int parentWidth = parent.getMeasuredWidth();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = parentWidth / 7;
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDate date = dates.get(position);

        holder.tvDayName.setText(date.format(DateTimeFormatter.ofPattern("EEE")));
        holder.tvDayNumber.setText(date.format(DateTimeFormatter.ofPattern("d")));

        if (position == selectedPosition) {
            holder.container.setBackgroundResource(R.drawable.bg_selected_date);
            holder.tvDayName.setTextColor(Color.WHITE);
            holder.tvDayNumber.setTextColor(Color.WHITE);
            holder.tvMonthName.setText(date.format(DateTimeFormatter.ofPattern("MMM")));
            holder.tvMonthName.setVisibility(View.VISIBLE);
        } else {
            holder.container.setBackground(null);
            holder.tvMonthName.setVisibility(View.GONE);
            holder.tvDayName.setTextColor(Color.parseColor("#757575"));
            holder.tvDayNumber.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public void setSelectedPosition(int position) {
        int previousPos = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPos);
        notifyItemChanged(selectedPosition);
    }

    public LocalDate getDateAt(int position) {
        return dates.get(position);
    }

    public int findPositionForDate(LocalDate date) {
        if (date == null) return -1;
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).isEqual(date)) {
                return i;
            }
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber, tvMonthName;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tv_day_name);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            tvMonthName = itemView.findViewById(R.id.tv_month_name);
            container = itemView.findViewById(R.id.container_calendar);

            itemView.setOnClickListener(v -> {
                if (onDateClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onDateClickListener.onDateClick(position);
                    }
                }
            });
        }
    }
}
