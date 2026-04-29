package com.example.helloworld.domain;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class DrinkPriceStore {

    private static final String PREFS_NAME = "DrinkPrices";

    // Irish pub price estimates (€) used as first-time defaults
    private static final Map<String, Double> IRISH_ESTIMATES = new HashMap<String, Double>() {{
        put("Vodka (35.5ml)",          5.50);
        put("Gin (35.5ml)",            6.00);
        put("Whiskey (35.5ml)",        5.50);
        put("Tequila (35.5ml)",        6.00);
        put("Liqueur (35.5ml)",        5.00);
        put("Fortified Wine (75ml)",   5.50);
        put("Wine (150ml)",            7.00);
        put("Beer (Pint)",             6.50);
        put("Malt Beverage",           4.50);
        put("Custom",                  0.00); // user always sets this manually
    }};

    private final SharedPreferences prefs;

    public DrinkPriceStore(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the last saved price for this drink name,
     * or the Irish market estimate if none has been saved yet.
     */
    public double getPrice(String drinkName) {
        float stored = prefs.getFloat(drinkName, -1f);
        if (stored >= 0) return stored;
        Double estimate = IRISH_ESTIMATES.get(drinkName);
        return (estimate != null) ? estimate : 0.0;
    }

    /** Persists the price the user confirmed for a drink type. */
    public void savePrice(String drinkName, double price) {
        prefs.edit().putFloat(drinkName, (float) price).apply();
    }

    /**
     * Returns true if this drink name has an estimate (or saved price) we can
     * pre-fill — used by the dialog to know whether to show "Estimated" label.
     */
    public boolean hasSavedPrice(String drinkName) {
        return prefs.contains(drinkName);
    }
}