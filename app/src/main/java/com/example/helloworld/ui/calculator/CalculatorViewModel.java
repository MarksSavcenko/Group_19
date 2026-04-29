package com.example.helloworld.ui.calculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.helloworld.domain.DrinkCalculator;

public class CalculatorViewModel extends ViewModel {

    private final MutableLiveData<Double>  _totalDrinks  = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> _drinkCount   = new MutableLiveData<>(0);

    private final MutableLiveData<Double>  _totalSpending = new MutableLiveData<>(0.0);

    public LiveData<Double>  getTotalDrinks()   { return _totalDrinks;   }
    public LiveData<Integer> getDrinkCount()    { return _drinkCount;    }
    public LiveData<Double>  getTotalSpending() { return _totalSpending; }

    /**
     * Adds a drink to both the standard-drink total and the spending total.
     *
     * @param volume  volume in ml
     * @param abv     alcohol-by-volume percentage
     * @param price   price paid in euros (0 = free / unknown, still tracked)
     */
    public void addDrink(double volume, double abv, double price) {
        double newAmount = DrinkCalculator.calculate(volume, abv);
        Double currentTotal = _totalDrinks.getValue();
        if (currentTotal == null) currentTotal = 0.0;
        _totalDrinks.setValue(Math.round((currentTotal + newAmount) * 100.0) / 100.0);

        Integer currentCount = _drinkCount.getValue();
        if (currentCount == null) currentCount = 0;
        _drinkCount.setValue(currentCount + 1);

        Double currentSpending = _totalSpending.getValue();
        if (currentSpending == null) currentSpending = 0.0;
        _totalSpending.setValue(Math.round((currentSpending + price) * 100.0) / 100.0);
    }

    /** Resets all session state — drinks, count, and spending. */
    public void resetSession() {
        _totalDrinks.setValue(0.0);
        _drinkCount.setValue(0);
        _totalSpending.setValue(0.0);
    }
}