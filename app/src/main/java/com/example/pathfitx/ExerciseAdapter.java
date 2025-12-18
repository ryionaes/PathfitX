package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private final List<Exercise> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onMoreClick(Exercise exercise, int position);
    }

    public ExerciseAdapter(List<Exercise> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = list.get(position);
        holder.tvTitle.setText(exercise.getTitle());
        holder.tvDetails.setText(String.format("%d sets • %d reps • %d kg", exercise.getSets(), exercise.getReps(), exercise.getKg()));

        // FIXED: Marunong mag-load ng local at remote images
        Object imageSource = exercise.getImageResId() != 0 ? exercise.getImageResId() : exercise.getImageUrl();

        Glide.with(holder.itemView.getContext())
                .load(imageSource)
                .placeholder(R.drawable.ic_workout)
                .error(R.drawable.ic_workout)
                .into(holder.imgExercise);

        holder.btnMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(exercise, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetails;
        ImageView imgExercise;
        ImageView btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_exercise_title);
            tvDetails = itemView.findViewById(R.id.tv_exercise_details);
            imgExercise = itemView.findViewById(R.id.img_exercise);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }
}
