# Danila — Strategy и Observer

Диаграммы показывают две стратегии расчета суммы и механизм уведомлений в `Order`. В ручных примерах вызывающий код выполняет роль клиента. В общем приложении `Main` будет выбирать стратегию, а `OrderFacade` будет использовать ее как Context, то есть передавать ей сумму заказа для расчета.

## Class Diagram

```mermaid
classDiagram
    class DiscountStrategy {
        <<interface>>
        +calculateTotal(int subtotal) int
        +getName() String
    }
    class NoDiscountStrategy {
        +calculateTotal(int subtotal) int
        +getName() String
    }
    class StudentDiscountStrategy {
        +calculateTotal(int subtotal) int
        +getName() String
    }
    class OrderObserver {
        <<interface>>
        +update(Order order) void
    }
    class Order {
        -OrderStatus status
        -List~OrderObserver~ observers
        +addObserver(OrderObserver observer) void
        +removeObserver(OrderObserver observer) void
        +setStatus(OrderStatus newStatus) void
        +getStatus() OrderStatus
        +getCustomerName() String
        -notifyObservers() void
    }
    class CustomerNotifier {
        +update(Order order) void
    }
    class KitchenNotifier {
        +update(Order order) void
    }
    DiscountStrategy <|.. NoDiscountStrategy
    DiscountStrategy <|.. StudentDiscountStrategy
    OrderObserver <|.. CustomerNotifier
    OrderObserver <|.. KitchenNotifier
    Order --> "0..*" OrderObserver : observers
    OrderObserver ..> Order : update parameter
```

## Sequence Diagram — расчет суммы

```mermaid
sequenceDiagram
    participant Client
    participant NoDiscount as NoDiscountStrategy
    participant Student as StudentDiscountStrategy
    alt Выбрана No Discount
        Client->>NoDiscount: calculateTotal(5000)
        NoDiscount-->>Client: 5000 KZT
    else Выбрана Student Discount
        Client->>Student: calculateTotal(5000)
        Student-->>Client: 4500 KZT
    end
```

Обе реализации предоставляют один метод расчета. Для `0` обе возвращают `0`, для отрицательной суммы бросают `IllegalArgumentException`. Студенческая стратегия оставляет 90% суммы и округляет результат вниз до целого KZT. Промежуточное умножение выполняется в `long`, чтобы поддержать даже `Integer.MAX_VALUE`. Расчет читает только переданную сумму и сохраняет исходные цены блюд.

## Sequence Diagram — подписка, смена статуса и отписка

```mermaid
sequenceDiagram
    participant Client
    participant Order
    participant Customer as CustomerNotifier
    participant Kitchen as KitchenNotifier
    Note over Order: Начальный статус CREATED
    Client->>Order: addObserver(customer)
    Client->>Order: addObserver(customer)
    Note over Order: Тот же экземпляр уже подписан
    Client->>Order: addObserver(kitchen)
    Client->>Order: setStatus(PAID)
    Order->>Order: проверить переход и сохранить PAID
    Order->>Order: notifyObservers()
    Order->>Customer: update(order)
    Order->>Kitchen: update(order)
    Client->>Order: removeObserver(kitchen)
    Client->>Order: setStatus(PAID)
    Order-->>Client: статус уже установлен
    Client->>Order: setStatus(READY)
    Order-->>Client: IllegalStateException
    Client->>Order: setStatus(PREPARING)
    Order->>Order: проверить переход и сохранить PREPARING
    Order->>Order: notifyObservers()
    Order->>Customer: update(order)
```

`Order` хранит подписчиков по интерфейсу `OrderObserver` и вызывает их в порядке подписки после сохранения нового статуса. Каждый подписчик читает имя и статус заказа и выводит свое сообщение. События возникают при переходах `CREATED → PAID → PREPARING → READY`. При подписке, отписке, повторе статуса и недопустимом переходе список полученных сообщений остается прежним.
