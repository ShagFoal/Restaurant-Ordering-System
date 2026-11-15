package org.example.restaurant.observer;

import org.example.restaurant.builder.Order;

public interface OrderObserver {
    void update(Order order);
}
