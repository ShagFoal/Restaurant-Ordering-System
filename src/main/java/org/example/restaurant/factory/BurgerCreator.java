package org.example.restaurant.factory;

import org.example.restaurant.factory.product.Burger;
import org.example.restaurant.factory.product.Meal;

public final class BurgerCreator extends MealCreator {
    @Override
    public Meal createMeal() {
        return new Burger();
    }
}
