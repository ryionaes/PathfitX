package com.example.pathfitx;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

    private List<Exercise> list;
    private OnSetChangedListener listener;

    public interface OnSetChangedListener {
        void onSetCompleted(boolean isCompleted);
    }

    public LiveAdapter(List<Exercise> list, OnSetChangedListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = list.get(position);
        holder.tvTitle.setText(exercise.getTitle());
        
        Glide.with(holder.itemView.getContext())
                .load(exercise.getImageUrl())
                .placeholder(R.drawable.ic_workout)
                .error(R.drawable.ic_workout)
                .into(holder.img);

        holder.setsContainer.removeAllViews();

        for (int i = 1; i <= exercise.getSets(); i++) {
            View setView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_set_row, holder.setsContainer, false);

            TextView tvSetNum = setView.findViewById(R.id.tv_set_number);
            TextView tvDetails = setView.findViewById(R.id.tv_set_details);
            ImageView btnCheck = setView.findViewById(R.id.btn_check_set);
            LinearLayout rowContainer = setView.findViewById(R.id.container_set_row);

            tvSetNum.setText("Set " + i);
            tvDetails.setText(exercise.getReps() + " reps • " + exercise.getKg() + " kg");

            // Click Listener
            btnCheck.setOnClickListener(v -> {
                boolean isSelected = rowContainer.getTag() != null && (boolean) rowContainer.getTag();

                if (!isSelected) {
                    // MARK AS DONE
                    rowContainer.setBackgroundResource(R.drawable.bg_workout_done);
                    tvSetNum.setTextColor(Color.parseColor("#2E7D32"));
                    btnCheck.setColorFilter(Color.parseColor("#2E7D32"));
                    rowContainer.setTag(true);

                    // 3. Notify Activity: +1 Completed
                    if (listener != null) listener.onSetCompleted(true);
                } else {
                    // UNDO
                    rowContainer.setBackgroundResource(R.drawable.bg_card_goals);
                    tvSetNum.setTextColor(Color.parseColor("#102040"));
                    btnCheck.clearColorFilter();
                    rowContainer.setTag(false);

                    // 3. Notify Activity: -1 Completed
                    if (listener != null) listener.onSetCompleted(false);
                }
            });

            holder.setsContainer.addView(setView);
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView img;
        LinearLayout setsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_live_exercise_title);
            img = itemView.findViewById(R.id.img_live_exercise);
            setsContainer = itemView.findViewById(R.id.layout_sets_container);
        }
    }
}
