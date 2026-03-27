package com.example.helloworld.domain;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;


 //Provides data about which dates were "nights out".
 //Currently uses mock data stored in memory.

public class NightOutRepository {

    // Mock data — a set of date strings the user "went out"
    private final SharedPreferences prefs;
    private final Set<String> nightsOut;

    public NightOutRepository(Context context) {
        prefs = context.getSharedPreferences("nights_out", Context.MODE_PRIVATE);
        nightsOut = new HashSet<>(prefs.getStringSet("dates", new HashSet<>()));
    }

    public void addNightOut(int year, int month, int day) {
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);
        nightsOut.add(dateStr);
        prefs.edit().putStringSet("dates", nightsOut).apply();
    }

    public void removeNightOut(int year, int month, int day) {
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);
        nightsOut.remove(dateStr);
        prefs.edit().putStringSet("dates", nightsOut).apply();
    }

    public Set<Integer> getNightsOutForMonth(int year, int month) {

        String prefix = String.format("%04d-%02d-", year, month);
        Set<Integer> days = new HashSet<>();
        for (String date : nightsOut) {
            if (date.startsWith(prefix)) {
                // Extract the day number from the date string
                int day = Integer.parseInt(date.substring(8));
                days.add(day);
            }
        }
        return days;
    }


     //Checks if a specific date was a night out.
    public boolean isNightOut(int year, int month, int day) {
        String dateStr = String.format("%04d-%02d-%02d", year, month, day);
        return nightsOut.contains(dateStr);
    }
}
