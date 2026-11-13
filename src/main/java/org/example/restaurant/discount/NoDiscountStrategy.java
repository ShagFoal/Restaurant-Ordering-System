package org.example.restaurant.discount;

public final class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public int calculateTotal(int subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal must not be negative.");
        }
        return subtotal;
    }

    @Override
    public String getName() {
        return "No Discount";
    }
}
