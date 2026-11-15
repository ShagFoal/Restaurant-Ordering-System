package org.example.restaurant.observer.notification;

import org.example.restaurant.builder.Order;
import org.example.restaurant.observer.OrderObserver;

public final class KitchenNotifier implements OrderObserver {
    @Override
    public void update(Order order) {
        System.out.println("[Kitchen] " + order.getCustomerName() + ": " + order.getStatus());
    }
}
