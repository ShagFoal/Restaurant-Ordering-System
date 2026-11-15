package org.example.restaurant.factory.product;

public final class Pizza implements Meal {
    @Override
    public String getName() {
        return "Pizza";
    }

    @Override
    public int getPrice() {
        return 3000;
    }
}
