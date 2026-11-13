package org.example.restaurant.facade;

import java.util.List;
import org.example.restaurant.adapter.PaymentProcessor;
import org.example.restaurant.discount.DiscountStrategy;
import org.example.restaurant.meal.Meal;
import org.example.restaurant.order.Order;
import org.example.restaurant.order.OrderBuilder;
import org.example.restaurant.order.OrderObserver;
import org.example.restaurant.order.OrderStatus;

public final class OrderFacade
{
    private final PaymentProcessor paymentProcessor;
    private final DiscountStrategy discountStrategy;
    private final List<OrderObserver> observers;

    public OrderFacade(
        PaymentProcessor paymentProcessor,
        DiscountStrategy discountStrategy,
        List<OrderObserver> observers)
    {
        if (paymentProcessor == null)
        {
            throw new IllegalArgumentException("Payment processor is required");
        }

        if (discountStrategy == null)
        {
            throw new IllegalArgumentException("Discount strategy is required");
        }

        if (observers == null)
        {
            throw new IllegalArgumentException("Observers are required");
        }

        for (OrderObserver observer : observers)
        {
            if (observer == null)
            {
                throw new IllegalArgumentException("Observer is required");
            }
        }

        this.paymentProcessor = paymentProcessor;
        this.discountStrategy = discountStrategy;
        this.observers = List.copyOf(observers);
    }

    public Order createOrder(String customerName, List<Meal> meals, String comment)
    {
        if (meals == null)
        {
            throw new IllegalArgumentException("Meals are required");
        }

        OrderBuilder builder = new OrderBuilder()
            .customerName(customerName)
            .comment(comment);

        for (Meal meal : meals)
        {
            builder.addMeal(meal);
        }

        Order order = builder.build();

        for (OrderObserver observer : observers)
        {
            order.addObserver(observer);
        }

        return order;
    }

    public int calculateTotal(Order order)
    {
        if (order == null)
        {
            throw new IllegalArgumentException("Order is required");
        }

        return discountStrategy.calculateTotal(order.getSubtotal());
    }

    public boolean checkout(Order order)
    {
        if (order == null)
        {
            throw new IllegalArgumentException("Order is required");
        }

        if (order.getStatus() != OrderStatus.CREATED)
        {
            throw new IllegalStateException("Order must be in CREATED status before checkout");
        }

        int total = calculateTotal(order);

        if (!paymentProcessor.pay(total))
        {
            return false;
        }

        order.setStatus(OrderStatus.PAID);
        order.setStatus(OrderStatus.PREPARING);
        order.setStatus(OrderStatus.READY);
        return true;
    }
}
