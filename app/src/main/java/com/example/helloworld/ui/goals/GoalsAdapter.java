package com.example.helloworld.ui.goals;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;

import java.util.ArrayList;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {

    private final List<Goal> goals = new ArrayList<>();
    private final OnGoalClickListener onEdit;
    private final OnGoalClickListener onDelete;

    public interface OnGoalClickListener {
        void onClick(Goal goal);
    }

    public GoalsAdapter(OnGoalClickListener onEdit, OnGoalClickListener onDelete) {
        this.onEdit   = onEdit;
        this.onDelete = onDelete;
    }

    public void submitList(List<Goal> newGoals) {
        goals.clear();
        if (newGoals != null) goals.addAll(newGoals);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal_card, parent, false);
        return new GoalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder h, int position) {
        h.bind(goals.get(position), onEdit, onDelete);
    }

    @Override public int getItemCount() {
        return goals.size();
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        private final TextView    tvTitle, tvPeriod, tvSummary, tvStatus;
        private final ProgressBar progressBar;
        private final ImageButton btnEdit, btnDelete;
        GoalViewHolder(@NonNull View v) {
            super(v);
            tvTitle     = v.findViewById(R.id.tvGoalTitle);
            tvPeriod    = v.findViewById(R.id.tvGoalPeriod);
            tvSummary   = v.findViewById(R.id.tvGoalSummary);
            progressBar = v.findViewById(R.id.goalProgressBar);
            tvStatus    = v.findViewById(R.id.tvGoalStatus);
            btnEdit     = v.findViewById(R.id.btnEditGoal);
            btnDelete   = v.findViewById(R.id.btnDeleteGoal);
        }

        void bind(Goal goal, OnGoalClickListener onEdit, OnGoalClickListener onDelete) {
            tvTitle.setText(goal.getGoalType().getLabel());
            tvPeriod.setText(goal.getPeriodType().getLabel());
            tvSummary.setText(goal.getSummary());

            int pct = (int) (goal.getProgress() * 100);
            progressBar.setProgress(pct);

            if (pct >= 100) {
                setStatus("#D32F2F", "⚠ Limit reached!");
            } else if (pct >= 75) {
                setStatus("#F9A825", "Getting close…");
            } else {
                progressBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#388E3C")));
                tvStatus.setVisibility(View.GONE);
            }

            btnEdit.setOnClickListener(v   -> onEdit.onClick(goal));
            btnDelete.setOnClickListener(v -> onDelete.onClick(goal));
        }

        private void setStatus(String hex, String message) {
            int colour = Color.parseColor(hex);
            progressBar.setProgressTintList(ColorStateList.valueOf(colour));
            tvStatus.setText(message);
            tvStatus.setTextColor(colour);
            tvStatus.setVisibility(View.VISIBLE);
        }
    }
}
