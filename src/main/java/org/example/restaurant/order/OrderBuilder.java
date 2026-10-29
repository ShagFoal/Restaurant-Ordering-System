package org.example.restaurant.order;

import java.util.ArrayList;
import java.util.List;
import org.example.restaurant.meal.Meal;

public final class OrderBuilder {
    private String customerName;
    private final List<Meal> meals = new ArrayList<>();
    private String comment = "";

    public OrderBuilder customerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name must not be blank.");
        }
        this.customerName = customerName.trim();
        return this;
    }

    public OrderBuilder addMeal(Meal meal) {
        if (meal == null) {
            throw new IllegalArgumentException("Meal must not be null.");
        }
        meals.add(meal);
        return this;
    }

    public OrderBuilder comment(String comment) {
        this.comment = comment == null ? "" : comment.trim();
        return this;
    }

    public Order build() {
        if (customerName == null) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (meals.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one meal.");
        }

        long subtotal = 0;
        for (Meal meal : meals) {
            subtotal += meal.getPrice();
        }
        if (subtotal > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Order subtotal exceeds the supported amount.");
        }

        return new Order(customerName, meals, comment);
    }
}
