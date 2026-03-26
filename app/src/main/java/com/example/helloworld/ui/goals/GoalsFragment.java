package com.example.helloworld.ui.goals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GoalsFragment extends Fragment implements DialogSetGoal.Listener {
    private GoalsViewModel viewModel;
    private GoalsAdapter   adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        TextView    tvEmpty     = view.findViewById(R.id.tvEmptyGoals);
        RecyclerView recycler   = view.findViewById(R.id.rvGoals);
        FloatingActionButton fab = view.findViewById(R.id.fabAddGoal);

        adapter = new GoalsAdapter(
            goal -> DialogSetGoal.newInstance(goal).show(getChildFragmentManager(), "edit_goal"),
            goal -> viewModel.removeGoal(goal.getGoalType(), goal.getPeriodType())
        );

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        viewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            adapter.submitList(goals);
            tvEmpty.setVisibility(goals.isEmpty() ? View.VISIBLE : View.GONE);
        });

        fab.setOnClickListener(v ->
                DialogSetGoal.newInstance(null).show(getChildFragmentManager(), "add_goal"));
    }

    @Override
    public void onGoalConfirmed(Goal.GoalType type, Goal.PeriodType period, double limit) {
        viewModel.setGoal(type, period, limit);
    }
}
