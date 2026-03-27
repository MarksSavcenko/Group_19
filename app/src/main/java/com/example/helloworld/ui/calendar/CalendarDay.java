package com.example.helloworld.ui.calendar;

// Represents a single day in the calendar grid
public class CalendarDay {
    public final int day;
    public boolean isNightOut;

    public CalendarDay(int day, boolean isNightOut) {
        this.day = day;
        this.isNightOut = isNightOut;
    }
}
