package org.example.restaurant.notification;

import org.example.restaurant.order.Order;
import org.example.restaurant.order.OrderObserver;

public final class CustomerNotifier implements OrderObserver {
    @Override
    public void update(Order order) {
        System.out.println("[Customer: " + order.getCustomerName() + "] Status: " + order.getStatus());
    }
}
