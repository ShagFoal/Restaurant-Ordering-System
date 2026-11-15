package org.example.restaurant.factory;

import org.example.restaurant.factory.product.Meal;
import org.example.restaurant.factory.product.Pizza;

public final class PizzaCreator extends MealCreator {
    @Override
    public Meal createMeal() {
        return new Pizza();
    }
}
