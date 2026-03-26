package com.example.helloworld.ui.goals;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class GoalsViewModel extends ViewModel {
    private final MutableLiveData<List<Goal>> goalsLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Goal>> getGoals() {
        return goalsLiveData;
    }

    public void setGoal(Goal.GoalType type, Goal.PeriodType period, double limit) {
        List<Goal> list = currentList();
        for (Goal g : list) {
            if (g.getGoalType() == type && g.getPeriodType() == period) {
                g.setLimit(limit);
                save(list);
                return;
            }
        }
        list.add(new Goal(type, period, limit));
        save(list);
    }

    public void removeGoal(Goal.GoalType type, Goal.PeriodType period) {
        List<Goal> list = currentList();
        list.removeIf(g -> g.getGoalType() == type && g.getPeriodType() == period);
        save(list);
    }

    public void addProgress(Goal.GoalType type, double delta) {
        List<Goal> list = currentList();
        for (Goal g : list) {
            if (g.getGoalType() == type) g.setCurrent(g.getCurrent() + delta);
        }
        save(list);
    }

    public void resetPeriod(Goal.PeriodType period) {
        List<Goal> list = currentList();
        for (Goal g : list) {
            if (g.getPeriodType() == period) g.setCurrent(0);
        }
        save(list);
    }
    private List<Goal> currentList() {
        List<Goal> val = goalsLiveData.getValue();
        return val != null ? new ArrayList<>(val) : new ArrayList<>();
    }

    private void save(List<Goal> list) {
        goalsLiveData.setValue(list);
    }


}
