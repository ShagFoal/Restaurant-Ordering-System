package org.example.restaurant.factory;

import org.example.restaurant.meal.Meal;
import org.example.restaurant.meal.Pizza;

public final class PizzaCreator extends MealCreator {
    @Override
    public Meal createMeal() {
        return new Pizza();
    }
}
