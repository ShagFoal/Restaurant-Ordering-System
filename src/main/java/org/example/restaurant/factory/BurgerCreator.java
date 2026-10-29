package org.example.restaurant.factory;

import org.example.restaurant.meal.Burger;
import org.example.restaurant.meal.Meal;

public final class BurgerCreator extends MealCreator {
    @Override
    public Meal createMeal() {
        return new Burger();
    }
}
