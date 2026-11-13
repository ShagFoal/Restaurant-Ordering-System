package org.example.restaurant.discount;

public final class StudentDiscountStrategy implements DiscountStrategy {
    @Override
    public int calculateTotal(int subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal must not be negative.");
        }
        return (int) ((long) subtotal * 90 / 100);
    }

    @Override
    public String getName() {
        return "Student Discount";
    }
}
