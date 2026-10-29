# Askhat — Factory Method и Builder

Диаграммы соответствуют реализованной основе проекта: девять Java-файлов в packages `meal`, `factory` и `order`. Observer-часть `Order` добавляет Danila; Facade и консольный клиент — Egor.

## Class Diagram

```mermaid
classDiagram
    class Meal {
        <<interface>>
        +getName() String
        +getPrice() int
    }
    class Pizza {
        +getName() String
        +getPrice() int
    }
    class Burger {
        +getName() String
        +getPrice() int
    }
    class MealCreator {
        <<abstract>>
        +createMeal() Meal
    }
    class PizzaCreator {
        +createMeal() Meal
    }
    class BurgerCreator {
        +createMeal() Meal
    }
    class OrderBuilder {
        -String customerName
        -List~Meal~ meals
        -String comment
        +customerName(String) OrderBuilder
        +addMeal(Meal) OrderBuilder
        +comment(String) OrderBuilder
        +build() Order
    }
    class Order {
        -String customerName
        -List~Meal~ meals
        -String comment
        -OrderStatus status
        ~Order(String, List~Meal~, String)
        +getCustomerName() String
        +getMeals() List~Meal~
        +getComment() String
        +getStatus() OrderStatus
        +getSubtotal() int
        +setStatus(OrderStatus) void
    }
    class OrderStatus {
        <<enumeration>>
        CREATED
        PAID
        PREPARING
        READY
    }
    Meal <|.. Pizza
    Meal <|.. Burger
    MealCreator <|-- PizzaCreator
    MealCreator <|-- BurgerCreator
    MealCreator ..> Meal : returns
    PizzaCreator ..> Pizza : creates
    BurgerCreator ..> Burger : creates
    OrderBuilder --> "0..*" Meal : collects
    OrderBuilder ..> Order : builds
    Order --> "1..*" Meal : immutable list
    Order --> OrderStatus : current status
```

`~` обозначает package-private конструктор `Order`. Builder и Product находятся в одном package. `getMeals()` возвращает неизменяемую копию списка, созданную при построении заказа. Каждый Product блюда имеет постоянное имя и цену.

## Sequence Diagram: создание заказа

`Client` — роль вызывающего кода. На текущем этапе это ручной вызов в JShell; после интеграции выбор creators выполняет Main, а последовательность Builder — OrderFacade. Отдельный класс `Client` не требуется.

```mermaid
sequenceDiagram
    participant Client
    participant PizzaCreator
    participant BurgerCreator
    participant Builder as OrderBuilder
    participant Order
    Client->>PizzaCreator: createMeal() через MealCreator
    PizzaCreator-->>Client: новый Pizza как Meal
    Client->>BurgerCreator: createMeal() через MealCreator
    BurgerCreator-->>Client: новый Burger как Meal
    Client->>Builder: customerName("Askhat")
    Builder-->>Client: тот же Builder
    Client->>Builder: addMeal(pizza)
    Builder-->>Client: тот же Builder
    Client->>Builder: addMeal(burger)
    Builder-->>Client: тот же Builder
    Client->>Builder: comment("Без лука")
    Builder-->>Client: тот же Builder
    Client->>Builder: build()
    Note over Builder: проверка имени, блюд и диапазона subtotal
    Builder->>Order: new Order(name, meals, comment)
    Note over Order: защитная копия List, начальный CREATED
    Order-->>Builder: новый Order
    Builder-->>Client: Order
    Client->>Order: getSubtotal()
    Order-->>Client: 5000 KZT
```

## Передача другим участникам

| Участник | Следующий шаг |
|---|---|
| Danila | Добавить `OrderObserver`, коллекцию подписчиков, регистрацию / отписку и закрытый `notifyObservers()`; вызвать уведомления после `status = newStatus`, сохранив валидацию и no-op |
| Egor | Создавать блюда через `MealCreator`; передавать их в Facade; собирать заказ через API Builder и менять статус только после успешного платежа |

Factory Method здесь использует наследование Creator и полиморфное создание Product. Builder отделяет накопление параметров и валидацию от готового заказа. Проверка допустимых переходов находится в `Order`; факт успешной оплаты впоследствии проверяет Facade.
