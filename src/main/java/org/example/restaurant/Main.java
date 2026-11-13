package org.example.restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.example.restaurant.adapter.LegacyPaymentAdapter;
import org.example.restaurant.adapter.LegacyPaymentSystem;
import org.example.restaurant.adapter.PaymentProcessor;
import org.example.restaurant.discount.DiscountStrategy;
import org.example.restaurant.discount.NoDiscountStrategy;
import org.example.restaurant.discount.StudentDiscountStrategy;
import org.example.restaurant.facade.OrderFacade;
import org.example.restaurant.factory.BurgerCreator;
import org.example.restaurant.factory.MealCreator;
import org.example.restaurant.factory.PizzaCreator;
import org.example.restaurant.meal.Meal;
import org.example.restaurant.notification.CustomerNotifier;
import org.example.restaurant.notification.KitchenNotifier;
import org.example.restaurant.order.Order;

public final class Main
{
    private Main()
    {
    }

    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            System.out.println("=== Simple Restaurant Ordering System ===");

            String customerName = readCustomerName(scanner);
            if (customerName == null)
            {
                return;
            }

            List<Meal> meals = readMeals(scanner);
            if (meals == null)
            {
                return;
            }

            String comment = readLine(scanner, "Comment (Enter to skip): ");
            if (comment == null)
            {
                return;
            }

            DiscountStrategy discount = readDiscount(scanner);
            if (discount == null)
            {
                return;
            }

            PaymentProcessor payment = new LegacyPaymentAdapter(new LegacyPaymentSystem());
            OrderFacade facade = new OrderFacade(
                payment, discount, List.of(new CustomerNotifier(), new KitchenNotifier()));
            Order order = facade.createOrder(customerName, meals, comment);
            int total = facade.calculateTotal(order);
            printOrder(order, discount, total);

            if (!confirmOrder(scanner))
            {
                return;
            }

            System.out.println("Simulated payment: " + total + " KZT");

            if (facade.checkout(order))
            {
                System.out.println("Order is ready. Payment successful.");
            }
            else
            {
                System.out.println("Payment declined. Order remains unpaid.");
            }
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            System.out.println("Cannot complete order: " + exception.getMessage());
        }
    }

    private static String readCustomerName(Scanner scanner)
    {
        while (true)
        {
            String customerName = readLine(scanner, "Customer name: ");
            if (customerName == null || !customerName.isEmpty())
            {
                return customerName;
            }

            System.out.println("Customer name is required");
        }
    }

    private static List<Meal> readMeals(Scanner scanner)
    {
        List<MealCreator> creators = List.of(new PizzaCreator(), new BurgerCreator());
        List<Meal> meals = new ArrayList<>();

        System.out.println();
        for (int i = 0; i < creators.size(); i++)
        {
            Meal meal = creators.get(i).createMeal();
            System.out.println((i + 1) + ". " + meal.getName() + " - " + meal.getPrice() + " KZT");
        }
        System.out.println("0. Finish selection");

        while (true)
        {
            Integer choice = readChoice(scanner, "Choose a meal: ", 0, creators.size());
            if (choice == null)
            {
                return null;
            }

            if (choice == 0)
            {
                if (!meals.isEmpty())
                {
                    return meals;
                }

                System.out.println("Add at least one meal before continuing");
                continue;
            }

            MealCreator creator = creators.get(choice - 1);
            Meal meal = creator.createMeal();
            meals.add(meal);
            System.out.println("Added: " + meal.getName() + " - " + meal.getPrice() + " KZT");
        }
    }

    private static DiscountStrategy readDiscount(Scanner scanner)
    {
        List<DiscountStrategy> discounts = List.of(
            new NoDiscountStrategy(), new StudentDiscountStrategy());

        System.out.println();
        for (int i = 0; i < discounts.size(); i++)
        {
            System.out.println((i + 1) + ". " + discounts.get(i).getName());
        }

        Integer choice = readChoice(scanner, "Choose a discount: ", 1, discounts.size());
        return choice == null ? null : discounts.get(choice - 1);
    }

    private static Integer readChoice(Scanner scanner, String prompt, int minimum, int maximum)
    {
        while (true)
        {
            String input = readLine(scanner, prompt);
            if (input == null)
            {
                return null;
            }

            try
            {
                int choice = Integer.parseInt(input);
                if (choice >= minimum && choice <= maximum)
                {
                    return choice;
                }
            }
            catch (NumberFormatException exception)
            {
                System.out.println("Enter a whole number");
                continue;
            }

            System.out.println("Choose a number from " + minimum + " to " + maximum);
        }
    }

    private static void printOrder(Order order, DiscountStrategy discount, int total)
    {
        System.out.println();
        System.out.println("Customer: " + order.getCustomerName());
        System.out.println("Meals:");
        for (Meal meal : order.getMeals())
        {
            System.out.println("  " + meal.getName() + " - " + meal.getPrice() + " KZT");
        }

        String comment = order.getComment().isEmpty() ? "(none)" : order.getComment();
        System.out.println("Comment: " + comment);
        System.out.println("Status: " + order.getStatus());
        System.out.println("Subtotal: " + order.getSubtotal() + " KZT");
        System.out.println("Discount: " + discount.getName());
        System.out.println("Total: " + total + " KZT");
    }

    private static boolean confirmOrder(Scanner scanner)
    {
        while (true)
        {
            String answer = readLine(scanner, "Confirm order? (y/n): ");
            if (answer == null)
            {
                return false;
            }

            if (answer.equalsIgnoreCase("y"))
            {
                return true;
            }

            if (answer.equalsIgnoreCase("n"))
            {
                System.out.println("Order cancelled. No payment was made.");
                return false;
            }

            System.out.println("Enter y or n");
        }
    }

    private static String readLine(Scanner scanner, String prompt)
    {
        System.out.print(prompt);
        if (!scanner.hasNextLine())
        {
            System.out.println();
            System.out.println("Input ended. No payment was made.");
            return null;
        }

        return scanner.nextLine().trim();
    }
}
