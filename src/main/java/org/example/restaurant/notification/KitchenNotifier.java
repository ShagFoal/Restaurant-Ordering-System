package org.example.restaurant.notification;

import org.example.restaurant.order.Order;
import org.example.restaurant.order.OrderObserver;

public final class KitchenNotifier implements OrderObserver {
    @Override
    public void update(Order order) {
        System.out.println("[Kitchen] " + order.getCustomerName() + ": " + order.getStatus());
    }
}
