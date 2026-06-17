# CineReserve - Microservices Movie Ticket Booking Platform

CineReserve is a complete, production-ready Movie Ticket Booking microservices application. It features a modern dark-themed user interface inspired by Netflix and BookMyShow, powered by a Java 21, Spring Boot 3.4.0, Spring Cloud Gateway, PostgreSQL, MongoDB, Redis, and RabbitMQ distributed backend.

---

## 1. Project Directory Structure

```
cine-reserve/
├── docker/
│   ├── docker-compose.yml       # DB infrastructure (Postgres, MongoDB, Redis, Rabbit)
│   └── postgres-init.sql        # Postgres database initialization script
├── docs/
│   ├── architecture.md          # Architectural, ERD, and sequence flow diagrams
│   ├── db-scripts.sql           # Database schema tables setup scripts
│   └── postman_collection.json  # Postman API Collection
├── api-gateway/                 # Spring Cloud API Gateway (Port 8080)
├── auth-service/                # Authentication Service (Port 8081 - Postgres)
├── catalog-service/             # Movie Catalog Service (Port 8082 - MongoDB, Redis)
├── booking-service/             # Transactional Booking Service (Port 8083 - Postgres, Redis)
├── notification-service/        # Event-driven Notification Service (Port 8084 - Postgres, RabbitMQ)
├── frontend/                    # React 19 + Vite + TailwindCSS SPA
└── pom.xml                      # Maven Multi-Module Parent POM
```

---

## 2. Microservices Architecture & Port Mappings

*   **API Gateway**: `8080` (Direct entry point for all client requests; handles JWT decoding and downstream header injection)
*   **Authentication Service**: `8081` (Handles user registration, BCrypt password hashing, session tokens)
*   **Movie Catalog Service**: `8082` (Coordinates locations, approved theatres, screen layout grids, movie profiles)
*   **Booking Service**: `8083` (Initiates ticket locks, manages seat availability checkout timers, confirm payments)
*   **Notification Service**: `8084` (Asynchronously consumes booking events to process mock Email/SMS alerts)
*   **React Client**: `5173` (Netflix + BookMyShow inspired dark layout)
*   **PostgreSQL Database**: `5432` (Auth, Booking, and Notification data)
*   **MongoDB Database**: `27017` (Document catalog layouts, movies, and locations)
*   **Redis Cache**: `6380` (Cached seat maps, movies, shows, and location regions)
*   **RabbitMQ Broker**: `5672` (AMQP) & `15672` (Management Console)

---

## 3. Technology Stack

*   **Frontend**: React 19, Vite, Axios, TailwindCSS, Context API, Lucide Icons.
*   **Backend**: Java 21, Spring Boot 3.4+, Spring Cloud Gateway 4.x, Spring Data JPA, Spring Data MongoDB, Validation, Spring AMQP.
*   **Databases**: PostgreSQL (Relational transactions), MongoDB (Document catalog layouts).
*   **Middleware**: Redis (High-speed read caching), RabbitMQ (Asynchronous messaging broker).

---

## 4. Setup and Installation Instructions

### Step 1: Boot Database Infrastructure
Make sure Docker is running on your system, then boot the background databases and brokers:
```bash
cd docker
docker compose up -d
```
*Note: This automatically executes `postgres-init.sql` to initialize databases: `cinereserve_auth`, `cinereserve_booking`, and `cinereserve_notification`.*

### Step 2: Compile and Build Java Backend
At the root of the project (`d:/coding/cinereserve`), execute Maven to clean, compile, and package all microservices:
```bash
mvn clean package -DskipTests
```

### Step 3: Run the Backend Microservices
Run each service using Java or your IDE runner:
1.  **Auth Service**:
    ```bash
    java -jar auth-service/target/auth-service-1.0.0.jar
    ```
2.  **API Gateway**:
    ```bash
    java -jar api-gateway/target/api-gateway-1.0.0.jar
    ```
3.  **Catalog Service**:
    ```bash
    java -jar catalog-service/target/catalog-service-1.0.0.jar
    ```
4.  **Booking Service**:
    ```bash
    java -jar booking-service/target/booking-service-1.0.0.jar
    ```
5.  **Notification Service**:
    ```bash
    java -jar notification-service/target/notification-service-1.0.0.jar
    ```

### Step 4: Run the React Frontend
Navigate to the `frontend/` directory, install packages, and boot the Vite development server:
```bash
cd frontend
npm install
npm run dev
```
Open your browser and navigate to `http://localhost:5173`.

---

## 5. Core Architectural Patterns

### Concurrency & Seat Locking (Double-Booking Prevention)
To ensure no two users can book or lock the same seat concurrently, CineReserve implements **Database Pessimistic Locking**.
*   When a user clicks "Proceed to Checkout", the Booking Service starts a database transaction.
*   It queries the requested seats using `@Lock(LockModeType.PESSIMISTIC_WRITE)`:
    ```sql
    SELECT * FROM show_seats WHERE show_id = ? AND seat_number IN (?) FOR UPDATE;
    ```
*   This locks the rows at the DB engine level, preventing concurrent threads from reading or locking them.
*   Seats are set to `LOCKED` with a `lock_time` timestamp. If payment is not completed within **5 minutes**, the lock automatically expires and is dynamically recycled upon the next availability lookup.

### Decentralized Security Gateway Pattern
*   All client traffic routes through the **API Gateway** on port `8080`.
*   The gateway executes a `JwtAuthenticationFilter` on protected endpoints (everything except `/auth/login`, `/auth/register`).
*   It decodes the bearer token, validates the signature, extracts claims (`userId`, `role`, `email`), and injects them as downstream headers: `X-User-Id`, `X-User-Role`, `X-User-Email`.
*   Downstream services intercept these headers and establish their internal security contexts, removing runtime database checks and maximizing response times.

### Cache-Aside Strategy (Redis)
To accelerate the hot-spots in movie booking, CineReserve caches:
*   Movie details, locations, and approved theatres (in `catalog-service`).
*   Show configurations and active seat availability maps (in `booking-service`).
Cache invalidations trigger automatically when edits occur or when a seat lock/booking transaction completes.
