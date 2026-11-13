# Egor — Adapter и Facade

Диаграммы соответствуют классам `adapter.PaymentProcessor`, `adapter.LegacyPaymentSystem`, `adapter.LegacyPaymentAdapter`, `facade.OrderFacade` и `Main`. Классы заказа, скидок и уведомлений показаны как зависимости. Их реализации принадлежат Askhat и Danila.

Готовые изображения для отчёта и презентации:

| Диаграмма | PNG | SVG |
|---|---|---|
| Adapter | [Открыть PNG](egor-adapter-class.png) | [Открыть SVG](egor-adapter-class.svg) |
| Facade | [Открыть PNG](egor-facade-class.png) | [Открыть SVG](egor-facade-class.svg) |
| Checkout | [Открыть PNG](egor-checkout-sequence.png) | [Открыть SVG](egor-checkout-sequence.svg) |

Исходники Mermaid приведены ниже. SVG можно масштабировать без потери качества.

## Adapter: Class Diagram

```mermaid
classDiagram
    direction TB
    class PaymentProcessor {
        <<interface>>
        +pay(int amount) boolean
    }
    class LegacyPaymentAdapter {
        -LegacyPaymentSystem legacyPaymentSystem
        +LegacyPaymentAdapter(LegacyPaymentSystem)
        +pay(int amount) boolean
    }
    class LegacyPaymentSystem {
        +makePayment(String amount) String
    }
    class OrderFacade {
        -PaymentProcessor paymentProcessor
        +checkout(Order order) boolean
    }
    PaymentProcessor <|.. LegacyPaymentAdapter
    LegacyPaymentAdapter --> LegacyPaymentSystem : delegates
    OrderFacade --> PaymentProcessor : uses
```

| Роль Adapter | Класс | Назначение |
|---|---|---|
| Target | `PaymentProcessor` | Удобный для приложения контракт `pay(int): boolean` |
| Adaptee | `LegacyPaymentSystem` | Несовместимый строковый контракт `makePayment(String): String` |
| Adapter | `LegacyPaymentAdapter` | Преобразует аргумент и результат, вызывает Adaptee |
| Client | `OrderFacade` | Использует Target и не разбирает ответы Legacy |

Пример преобразования: `4500` → `"4500"` → `"SUCCESS"` → `true`. Adapter передает в Legacy и неположительные суммы; Legacy возвращает `"FAILED"`, а Adapter — `false`. Это локальная имитация оплаты без сети и настоящих списаний.

Ссылка на Legacy передается через конструктор и хранится в `final`-поле. Стрелка показывает связь объектов, а не наследование от старой системы. Реализация интерфейса обозначена пунктирной стрелкой с пустым треугольником.

## Facade: Class Diagram

```mermaid
classDiagram
    direction TB
    class Main {
        +main(String[] args) void
    }
    class OrderFacade {
        -PaymentProcessor paymentProcessor
        -DiscountStrategy discountStrategy
        -List~OrderObserver~ observers
        +OrderFacade(PaymentProcessor, DiscountStrategy, List~OrderObserver~)
        +createOrder(String, List~Meal~, String) Order
        +calculateTotal(Order order) int
        +checkout(Order order) boolean
    }
    class PaymentProcessor {
        <<interface>>
        +pay(int amount) boolean
    }
    class DiscountStrategy {
        <<interface>>
        +calculateTotal(int subtotal) int
        +getName() String
    }
    class OrderObserver {
        <<interface>>
        +update(Order order) void
    }
    class OrderBuilder {
        +customerName(String) OrderBuilder
        +addMeal(Meal) OrderBuilder
        +comment(String) OrderBuilder
        +build() Order
    }
    class Order {
        +getSubtotal() int
        +getStatus() OrderStatus
        +addObserver(OrderObserver) void
        +setStatus(OrderStatus) void
    }
    Main ..> OrderFacade : calls
    OrderFacade --> PaymentProcessor : payment
    OrderFacade --> DiscountStrategy : calculation
    OrderFacade --> "0..*" OrderObserver : subscribers
    OrderFacade ..> OrderBuilder : builds order
    OrderFacade ..> Order : reads and changes status
    OrderBuilder ..> Order : creates
    Order --> "0..*" OrderObserver : notifies
```

На диаграмме показаны методы зависимостей, которые важны для Facade; это фрагмент, а не полный список членов всех классов проекта.

- `createOrder()` использует Builder и подписывает переданные observers. Этот метод не вызывает оплату.
- `calculateTotal()` передает subtotal в Strategy и не меняет заказ.
- `checkout()` проверяет начальный статус, рассчитывает total и вызывает PaymentProcessor. После успеха переводит заказ в `PAID`, `PREPARING`, `READY`.
- `Main` создает конкретные зависимости, ведет диалог и вызывает `checkout()` только после подтверждения `y`.

Facade не хранит формулу скидки и не вызывает `OrderObserver.update()` напрямую. Уведомления отправляет сам `Order` после изменения статуса.

## Checkout: Sequence Diagram

```mermaid
sequenceDiagram
    participant Main
    participant Facade as OrderFacade
    participant Discount as DiscountStrategy
    participant Payment as LegacyPaymentAdapter
    participant Legacy as LegacyPaymentSystem
    participant Order
    participant Observers as OrderObserver subscribers
    Note over Main: Order preview shown, user confirms y
    Main->>Facade: checkout(order)
    Facade->>Order: getStatus()
    Order-->>Facade: current status
    alt status is not CREATED
        Facade-->>Main: throw IllegalStateException
        Note over Facade,Legacy: No payment call
    else status is CREATED
        Facade->>Facade: calculateTotal(order)
        Facade->>Order: getSubtotal()
        Order-->>Facade: subtotal
        Facade->>Discount: calculateTotal(subtotal)
        Discount-->>Facade: total
        Facade->>Payment: pay(total) through PaymentProcessor
        Payment->>Legacy: makePayment(String.valueOf(total))
        Legacy-->>Payment: SUCCESS or FAILED
        Payment-->>Facade: boolean result
        alt payment failed
            Facade-->>Main: false
            Note over Order,Observers: CREATED remains, no notifications
        else payment succeeded
            loop PAID, PREPARING, READY
                Facade->>Order: setStatus(nextStatus)
                Order->>Order: validate and save status
                Order->>Observers: update(this)
            end
            Facade-->>Main: true
        end
    end
```

Отказ при корректном заказе проверяется через подстановку `PaymentProcessor`, возвращающего `false`. Штатная Legacy-имитация принимает положительные суммы, поэтому такой отказ проверяется через API Facade, а не отдельным пунктом меню.

После отказа заказ остается `CREATED`, и вызов можно повторить. Для уже оплаченного заказа повторный `checkout()` запрещен до нового обращения к платежной системе.
