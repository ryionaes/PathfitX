package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    private final List<String> options;
    private String selectedOption;
    private final OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(String option);
    }

    public OptionsAdapter(List<String> options, String selectedOption, OnOptionClickListener listener) {
        this.options = options;
        this.selectedOption = selectedOption;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String option = options.get(position);
        holder.tvOptionName.setText(option);
        holder.rbOption.setChecked(option.equals(selectedOption));

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = options.indexOf(selectedOption);
            selectedOption = option;
            notifyItemChanged(previousPosition);
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) {
                listener.onOptionClick(option);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOptionName;
        RadioButton rbOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOptionName = itemView.findViewById(R.id.tv_option_name);
            rbOption = itemView.findViewById(R.id.rb_option);
        }
    }
}