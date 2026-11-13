package org.example.restaurant.adapter;

public final class LegacyPaymentSystem
{
    public String makePayment(String amount)
    {
        String result;

        try
        {
            int paymentAmount = Integer.parseInt(amount);
            result = paymentAmount > 0 ? "SUCCESS" : "FAILED";
        }
        catch (NumberFormatException exception)
        {
            result = "FAILED";
        }

        System.out.println(
            "[LegacyPaymentSystem] makePayment(\"" + amount + "\") -> " + result);
        return result;
    }
}
