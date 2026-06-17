# Architecture and Design Specification - CineReserve

This document details the software architecture, entity relationships, and execution sequences of the CineReserve platform.

---

## 1. System Architecture

The following diagram outlines the routing of client requests through the API Gateway to downstream microservices, along with shared communications.

```mermaid
graph TD
    Client[React Frontend] -->|HTTP Port 8080| Gateway[API Gateway]
    
    Gateway -->|/auth/**| AuthService[Auth Service :8081]
    Gateway -->|/movies/**| CatalogService[Catalog Service :8082]
    Gateway -->|/booking/**| BookingService[Booking Service :8083]
    Gateway -->|/notifications/**| NotificationService[Notification Service :8084]

    AuthService -->|ACID DB| PostgresAuth[(Postgres: cinereserve_auth)]
    BookingService -->|ACID DB| PostgresBooking[(Postgres: cinereserve_booking)]
    NotificationService -->|ACID DB| PostgresNotif[(Postgres: cinereserve_notification)]
    
    CatalogService -->|Document DB| MongoCatalog[(MongoDB: cinereserve_catalog)]

    CatalogService <-->|Cache Read/Write :6380| Redis[(Redis Caching)]
    BookingService <-->|Cache Read/Write :6380| Redis
    
    BookingService -->|BookingCreated/Cancelled| RabbitMQ[RabbitMQ Broker :5672]
    RabbitMQ -->|Consume Events| NotificationService
```

---

## 2. Entity Relationship Diagram (ERD)

The diagram below maps relational data schemas across databases.

```mermaid
erDiagram
    %% Auth Service
    USER {
        bigint id PK
        string name
        string email UK
        string password
        string role
        string phone
        timestamp created_at
    }
    REFRESH_TOKEN {
        bigint id PK
        bigint user_id FK
        string token UK
        timestamp expiry_date
    }
    USER ||--|| REFRESH_TOKEN : "owns"

    %% Booking Service
    SHOW {
        bigint id PK
        string movie_id
        string screen_id
        string theatre_id
        date show_date
        time start_time
        time end_time
    }
    SHOW_PRICE {
        bigint show_id PK,FK
        string seat_type PK
        double price
    }
    SHOW_SEAT {
        bigint id PK
        bigint show_id FK
        string seat_number
        string seat_type
        string status
        bigint locked_by
        timestamp lock_time
        int version
    }
    BOOKING {
        bigint id PK
        bigint user_id
        bigint show_id FK
        double total_amount
        string status
        timestamp created_at
    }
    BOOKING_SEAT {
        bigint booking_id PK,FK
        string seat_number PK
    }
    SHOW ||--o{ SHOW_PRICE : "defines"
    SHOW ||--o{ SHOW_SEAT : "holds"
    SHOW ||--o{ BOOKING : "scheduled-for"
    BOOKING ||--o{ BOOKING_SEAT : "contains"

    %% Notification Service
    NOTIFICATION {
        bigint id PK
        bigint user_id
        bigint booking_id
        string message
        string notification_type
        timestamp sent_at
    }
```

---

## 3. Booking Sequence Diagram

This diagram maps step-by-step communication during seat selection, locking, and transaction confirmation.

```mermaid
sequenceDiagram
    autonumber
    actor User as React Client
    participant Gateway as API Gateway
    participant Booking as Booking Service
    participant Rabbit as RabbitMQ
    participant Notif as Notification Service

    User->>Gateway: POST /booking/initiate (seats, showId)
    Note over Gateway: JwtAuthenticationFilter validates token
    Gateway->>Booking: Forward with X-User-Id header
    Note over Booking: Acquire PESSIMISTIC_WRITE lock on seats
    Note over Booking: Check availability and update to LOCKED
    Booking-->>User: Return 200 OK (Booking INITIATED, seats locked for 5m)

    User->>Gateway: POST /booking/confirm/{bookingId}
    Gateway->>Booking: Forward with X-User-Id header
    Note over Booking: Verify lock duration (within 5m)
    Note over Booking: Update seat statuses to BOOKED
    Note over Booking: Save booking status to CONFIRMED
    Booking->>Rabbit: Publish BookingConfirmed event
    Booking-->>User: Return 200 OK (Booking CONFIRMED)

    Note over Rabbit: Routing to booking.confirmed queue
    Rabbit->>Notif: Consume BookingConfirmed event
    Note over Notif: Format mock Email/SMS alerts
    Note over Notif: Save notification log to Postgres
```
