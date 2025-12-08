package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<Exercise> exerciseList;
    private OnItemClickListener listener;

    // Interface for click events
    public interface OnItemClickListener {
        void onMoreClick(Exercise exercise, int position);
    }

    // Update Constructor
    public ExerciseAdapter(List<Exercise> exerciseList, OnItemClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.tvTitle.setText(exercise.getTitle());
        holder.tvDetails.setText(exercise.getDetails());
        
        // Use Glide to load image from URL
        Glide.with(holder.itemView.getContext())
                .load(exercise.getImageUrl())
                .placeholder(R.drawable.ic_workout) // Default icon while loading
                .error(R.drawable.ic_workout)       // Default icon if error
                .into(holder.imgExercise);

        // Set click listener on the "more" button
        holder.btnMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(exercise, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetails;
        ImageView imgExercise, btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_exercise_title);
            tvDetails = itemView.findViewById(R.id.tv_exercise_details);
            imgExercise = itemView.findViewById(R.id.img_exercise);
            btnMore = itemView.findViewById(R.id.btn_more); // Ensure this ID exists in item_exercise.xml
        }
    }
}
