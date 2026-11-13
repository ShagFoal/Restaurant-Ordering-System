package org.example.restaurant.adapter;

public final class LegacyPaymentAdapter implements PaymentProcessor
{
    private final LegacyPaymentSystem legacyPaymentSystem;

    public LegacyPaymentAdapter(LegacyPaymentSystem legacyPaymentSystem)
    {
        if (legacyPaymentSystem == null)
        {
            throw new IllegalArgumentException("Legacy payment system is required");
        }

        this.legacyPaymentSystem = legacyPaymentSystem;
    }

    @Override
    public boolean pay(int amount)
    {
        String result = legacyPaymentSystem.makePayment(String.valueOf(amount));
        return "SUCCESS".equals(result);
    }
}
