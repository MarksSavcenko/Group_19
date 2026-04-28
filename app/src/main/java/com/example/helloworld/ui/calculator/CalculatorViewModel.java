package com.example.helloworld.ui.calculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helloworld.domain.DrinkCalculator;

public class CalculatorViewModel extends ViewModel {

    // Tracks the total standard drinks and calories for this session
    private final MutableLiveData<Double> totalDrinks  = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> totalCalories = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> drinkCount  = new MutableLiveData<>(0);

    public LiveData<Double>  getTotalDrinks()   { return totalDrinks; }
    public LiveData<Integer> getTotalCalories() { return totalCalories; }
    public LiveData<Integer> getDrinkCount()    { return drinkCount; }

    // Adds a drink and updates both the standard drink total and calorie total
    public void addDrink(double volumeMl, double abv, int carbCalories) {
        double addedDrinks = DrinkCalculator.calculate(volumeMl, abv);
        int addedCalories  = DrinkCalculator.calculateCalories(volumeMl, abv, carbCalories);

        Double currentDrinks = totalDrinks.getValue();
        if (currentDrinks == null) currentDrinks = 0.0;
        totalDrinks.setValue(Math.round((currentDrinks + addedDrinks) * 100.0) / 100.0);

        Integer currentCalories = totalCalories.getValue();
        if (currentCalories == null) currentCalories = 0;
        totalCalories.setValue(currentCalories + addedCalories);

        Integer currentCount = drinkCount.getValue();
        if (currentCount == null) currentCount = 0;
        drinkCount.setValue(currentCount + 1);
    }

    // Resets everything back to zero for a fresh session
    public void resetSession() {
        totalDrinks.setValue(0.0);
        totalCalories.setValue(0);
        drinkCount.setValue(0);
    }
}
