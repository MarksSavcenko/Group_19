package com.example.helloworld.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


 //Provides data about which dates were "nights out".
 //Currently uses mock data stored in memory.

public class NightOutRepository {

    // Mock data — a set of date strings the user "went out"
    private final Set<String> nightsOut = new HashSet<>(Arrays.asList(
            "2026-03-01",
            "2026-03-07",
            "2026-03-14",
            "2026-03-15",
            "2026-03-20",
            "2026-02-05",
            "2026-02-14",
            "2026-02-22"
    ));

    // Returns all nights out for the given month and year.
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
