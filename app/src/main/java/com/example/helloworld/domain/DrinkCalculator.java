package com.example.helloworld.domain;

/**
 * Handles standard drink calculation logic.

 */
public class DrinkCalculator {

    // Easy to change later — different countries use different values (e.g. 14g in the US)
    public static final double GRAMS_PER_STANDARD_DRINK = 10.0;
    private static final double ETHANOL_DENSITY = 0.789;

    /**
     * Calculates how many standard drinks a given volume and ABV equates to.
     *

     */
    public static double calculate(double volumeMl, double abv) {
        if (volumeMl <= 0) {
            throw new IllegalArgumentException("Volume must be greater than zero");
        }
        if (abv <= 0) {
            throw new IllegalArgumentException("ABV must be greater than zero");
        }

        double standardDrinks = (volumeMl * (abv / 100.0) * ETHANOL_DENSITY) / GRAMS_PER_STANDARD_DRINK;
        // Round to 2 decimal places for a clean result
        return Math.round(standardDrinks * 100.0) / 100.0;
    }
}
