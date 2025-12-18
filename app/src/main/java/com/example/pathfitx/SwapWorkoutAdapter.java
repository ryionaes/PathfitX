package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SwapWorkoutAdapter extends RecyclerView.Adapter<SwapWorkoutAdapter.ViewHolder> {

    private final List<WorkoutType> options;
    private String selectedOption;
    private final OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(String option);
    }

    public SwapWorkoutAdapter(List<WorkoutType> options, String selectedOption, OnOptionClickListener listener) {
        this.options = options;
        this.selectedOption = selectedOption;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swap_workout_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutType option = options.get(position);
        holder.tvWorkoutName.setText(option.getName());

        Glide.with(holder.itemView.getContext())
             .load(option.getImageResId()) // Use the resource ID
             .placeholder(R.drawable.ic_workout)
             .error(R.drawable.ic_workout)
             .into(holder.ivWorkoutImage);

        holder.rbWorkoutOption.setChecked(option.getName().equals(selectedOption));

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = -1;
            for(int i=0; i<options.size(); i++){
                if(options.get(i).getName().equals(selectedOption)){
                    previousPosition = i;
                    break;
                }
            }
            selectedOption = option.getName();
            if(previousPosition != -1) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) {
                listener.onOptionClick(option.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivWorkoutImage;
        TextView tvWorkoutName;
        RadioButton rbWorkoutOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkoutImage = itemView.findViewById(R.id.iv_workout_image);
            tvWorkoutName = itemView.findViewById(R.id.tv_workout_name);
            rbWorkoutOption = itemView.findViewById(R.id.rb_workout_option);
        }
    }
}
