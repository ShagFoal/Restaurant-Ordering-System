package org.example.restaurant.order;

import java.util.List;
import org.example.restaurant.meal.Meal;

public final class Order {
    private final String customerName;
    private final List<Meal> meals;
    private final String comment;
    private OrderStatus status = OrderStatus.CREATED;

    Order(String customerName, List<Meal> meals, String comment) {
        this.customerName = customerName;
        this.meals = List.copyOf(meals);
        this.comment = comment;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public String getComment() {
        return comment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getSubtotal() {
        int subtotal = 0;
        for (Meal meal : meals) {
            subtotal += meal.getPrice();
        }
        return subtotal;
    }

    public void setStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Order status must not be null.");
        }
        if (newStatus == status) {
            return;
        }

        boolean validTransition = switch (status) {
            case CREATED -> newStatus == OrderStatus.PAID;
            case PAID -> newStatus == OrderStatus.PREPARING;
            case PREPARING -> newStatus == OrderStatus.READY;
            case READY -> false;
        };

        if (!validTransition) {
            throw new IllegalStateException(
                    "Cannot change order status from " + status + " to " + newStatus + ".");
        }
        status = newStatus;
    }
}
