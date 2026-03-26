package com.example.helloworld.ui.goals;

public class Goal {

    public enum GoalType {
        DRINKS("Drinks", "drinks", false),
        MONEY("Money Spent", "€", true),
        CALORIES("Calories", "kcal", false);

        final String label;
        final String unit;
        final boolean decimal;

        GoalType(String label, String unit, boolean decimal) {
            this.label = label;
            this.unit = unit;
            this.decimal = decimal;
        }

        public String getLabel() {
            return label;
        }
        public String getUnit()  {
            return unit;
        }
    }

    public enum PeriodType {
        DAY("Day"), WEEK("Week"), MONTH("Month");

        private final String label;
        PeriodType(String label) {
            this.label = label;
        }
        public String getLabel() {
            return label;
        }
    }

    private final GoalType goalType;
    private final PeriodType periodType;
    private double limit;
    private double current;

    public Goal(GoalType goalType, PeriodType periodType, double limit) {
        this.goalType   = goalType;
        this.periodType = periodType;
        this.limit      = limit;
    }

    public float getProgress() {
        if (limit <= 0) return 0f;
        return (float) Math.min(current / limit, 1.0);
    }

    public String getSummary() {
        String fmt = goalType.decimal ? "%.2f / %.2f %s" : "%.0f / %.0f %s";
        return String.format(fmt, current, limit, goalType.unit);
    }

    public GoalType getGoalType() {
        return goalType;
    }
    public PeriodType getPeriodType(){
        return periodType;
    }
    public double getLimit() {
        return limit;
    }
    public void setLimit(double l) {
        limit   = l;
    }
    public double getCurrent() {
        return current;
    }
    public void setCurrent(double c) {
        current = c;
    }
}
