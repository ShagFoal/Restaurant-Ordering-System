package org.example.restaurant.discount;

public interface DiscountStrategy {
    int calculateTotal(int subtotal);

    String getName();
}
