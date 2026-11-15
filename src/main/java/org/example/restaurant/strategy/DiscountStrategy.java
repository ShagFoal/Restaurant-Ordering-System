package org.example.restaurant.strategy;

public interface DiscountStrategy {
    int calculateTotal(int subtotal);

    String getName();
}
