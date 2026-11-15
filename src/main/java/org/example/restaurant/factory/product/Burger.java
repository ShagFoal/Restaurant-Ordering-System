package org.example.restaurant.factory.product;

public final class Burger implements Meal {
    @Override
    public String getName() {
        return "Burger";
    }

    @Override
    public int getPrice() {
        return 2000;
    }
}
