package com.example.pathfitx;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final String[] days = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"};
    private final String[] dates = {"1", "2", "3", "4", "5", "6", "7"};
    private int selectedPosition = 3; // Setting "Tue 4" as selected by default

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDayName.setText(days[position]);
        holder.tvDayNumber.setText(dates[position]);

        if (position == selectedPosition) {
            // Selected Style
            holder.container.setBackgroundResource(R.drawable.bg_selected_date); // Note: reference the drawable XML
            holder.tvDayName.setTextColor(Color.WHITE);
            holder.tvDayNumber.setTextColor(Color.WHITE);
            holder.tvMonthName.setVisibility(View.VISIBLE); // Show "Nov"
        } else {
            // Unselected Style
            holder.container.setBackground(null);
            holder.tvDayName.setTextColor(Color.parseColor("#757575"));
            holder.tvDayNumber.setTextColor(Color.BLACK);
            holder.tvMonthName.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPos);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return days.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber, tvMonthName;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tv_day_name);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            tvMonthName = itemView.findViewById(R.id.tv_month_name);
            container = itemView.findViewById(R.id.container_calendar);
        }
    }
}