# Plateable Eats

A Spring Boot + vanilla JS restaurant ordering system: live menu, table management,
order lifecycle (kitchen queue), and reservations, localized for the South African
market (ZAR pricing).

## Status

Early-stage portfolio project. Currently **in-memory persistence** (data resets on
restart) with an **H2 file-backed option** available — see Configuration below.
Write endpoints require basic auth (see Security). Not deployed yet.

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use the bundled `./mvnw`)

## Running locally

`git clone https://github.com/Banelenelson02/platable_eats.git`
`cd platable_eats`
`./mvnw spring-boot:run`

App starts on `http://localhost:8080`. Menu UI is served at `/`.

Run tests:

`./mvnw test`

## Configuration

`src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:mem:plateable` | In-memory H2. Swap to a file path or Postgres URL for persistence across restarts |
| `spring.h2.console.enabled` | `true` | H2 web console at `/h2-console` (dev only — disable in prod) |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema strategy; use `validate` in production with real migrations |

## API Reference

Interactive docs (Swagger UI): `http://localhost:8080/swagger-ui.html`

### Menu — `/api/menu`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/menu` | — | `200` `MenuItemResponse[]` |
| GET | `/api/menu/{id}` | — | `200` `MenuItemResponse` / `404` |
| PATCH | `/api/menu/{id}` | `{ "price": 130.00, "available": false }` | `200` `MenuItemResponse` / `404` — **auth required** |

### Tables — `/api/tables`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/tables` | — | `200` `TableResponse[]` |
| GET | `/api/tables/available?minCapacity=4` | — | `200` `TableResponse[]` |
| POST | `/api/tables` | `{ "tableId": "T5", "capacity": 4 }` | `201` `TableResponse` — **auth required** |

### Orders — `/api/orders`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/orders` | — | `200` `OrderResponse[]` |
| GET | `/api/orders?status=IN_KITCHEN` | — | `200` `OrderResponse[]` |
| GET | `/api/orders/{id}` | — | `200` `OrderResponse` / `404` |
| POST | `/api/orders` | `{ "tableId": "T2", "waiterId": "W001" }` | `201` `OrderResponse` — **auth required** |
| POST | `/api/orders/{id}/items` | `{ "menuItemId": "M001", "quantity": 2, "instructions": "extra cheese" }` | `200` `OrderResponse` / `404` — **auth required** |
| DELETE | `/api/orders/{id}/items/{menuItemId}` | — | `200` `OrderResponse` / `404` — **auth required** |
| PATCH | `/api/orders/{id}/status?status=READY` | — | `200` `OrderResponse` / `404` — **auth required** |

### Reservations — `/api/reservations`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/reservations` | — | `200` `ReservationResponse[]` |
| POST | `/api/reservations` | `{ "customerName": "T. Mokoena", "tableId": "T3", "partySize": 4, "time": "2026-08-01T19:00:00" }` | `201` `ReservationResponse` — **auth required** |
| DELETE | `/api/reservations/{id}` | — | `200` / `404` — **auth required** |

### Error format

All errors return a consistent shape:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Order ORD999 not found",
  "path": "/api/orders/ORD999",
  "timestamp": "2026-07-13T10:22:00"
}

# Plateable Eats — Full-Stack Restaurant System

Plateable Eats is a high-performance, full-stack casual dining web application. This project demonstrates the architectural evolution of an in-memory vanilla Java Object-Oriented Design (OOD) into a decoupled, enterprise-ready N-Tier RESTful system powered by Spring Boot.

The system is fully localized for the South African casual dining market, featuring local menu formatting in South African Rands (ZAR).

---

## 🛠 Tech Stack & Architecture

The application is built using a decoupled, highly scannable structural tier system designed to ensure separate concerns across layers:

### 1. Frontend (Presentation Layer)
* **Languages:** Semantic HTML5, CSS3 utilizing **BEM (Block Element Modifier)** layout methodologies.
* **Logic:** Vanilla ES6+ JavaScript designed via the Module Pattern to run completely dependency-free.
* **Integrations:** Swiper.js for hardware-accelerated animated hero displays.

### 2. Backend (Application Layer)
* **Framework:** Spring Boot 3.x (Spring Web).
* **Build Automation:** Maven.
* **Language:** Java 17+.

---

## 🏗 System Architecture Diagram

```text
  [ Web Browser UI ] 
          │
      (Fetch API / JSON payload)
          │
          ▼
  [ Controllers (Web Layer) ]      <-- com.plateable.controller
          │
          ▼
  [ RestaurantService (Logic) ]    <-- com.plateable.service (Spring @Service Bean)
          │
          ▼
  [ Domain Models (Data Core) ]    <-- com.plateable.model (Order, Table, MenuItem)


platable_eats/
├── src/
│   └── main/
│       ├── java/com/plateable/
│       │   ├── PlateableEatsApplication.java   # Spring Boot Application Root
│       │   ├── controller/                     # REST Web Endpoints
│       │   ├── model/                          # Domain Entities & Enums
│       │   └── service/                        # System Business Engine
│       └── resources/
│           ├── application.properties          # Framework Server Properties
│           └── static/                         # UI Web Assets Location
│               ├── index.html                  # Main Restaurant View
│               ├── css/style.css               # Production Stylesheet
│               └── script/script.js            # Front-to-Back Network Transport Script
└── pom.xml                                     # Dependency Descriptor Configuration
# Plateable Eats

A Spring Boot + vanilla JS restaurant ordering system: live menu, table management,
order lifecycle (kitchen queue), and reservations, localized for the South African
market (ZAR pricing).

## Status

Early-stage portfolio project. Currently **in-memory persistence** (data resets on
restart) with an **H2 file-backed option** available — see Configuration below.
Write endpoints require basic auth (see Security). Not deployed yet.

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use the bundled `./mvnw`)

## Running locally

`git clone [https://github.com/Banelenelson02/platable_eats.git](https://github.com/Banelenelson02/platable_eats.git)`
`cd platable_eats`
`./mvnw spring-boot:run`

App starts on `http://localhost:8080`. Menu UI is served at `/`.

Run tests:

`./mvnw test`

## Configuration

`src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:mem:plateable` | In-memory H2. Swap to a file path or Postgres URL for persistence across restarts |
| `spring.h2.console.enabled` | `true` | H2 web console at `/h2-console` (dev only — disable in prod) |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema strategy; use `validate` in production with real migrations |

## API Reference

Interactive docs (Swagger UI): `http://localhost:8080/swagger-ui.html`

### Menu — `/api/menu`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/menu` | — | `200` `MenuItemResponse[]` |
| GET | `/api/menu/{id}` | — | `200` `MenuItemResponse` / `404` |
| PATCH | `/api/menu/{id}` | `{ "price": 130.00, "available": false }` | `200` `MenuItemResponse` / `404` — **auth required** |

### Tables — `/api/tables`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/tables` | — | `200` `TableResponse[]` |
| GET | `/api/tables/available?minCapacity=4` | — | `200` `TableResponse[]` |
| POST | `/api/tables` | `{ "tableId": "T5", "capacity": 4 }` | `201` `TableResponse` — **auth required** |

### Orders — `/api/orders`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/orders` | — | `200` `OrderResponse[]` |
| GET | `/api/orders?status=IN_KITCHEN` | — | `200` `OrderResponse[]` |
| GET | `/api/orders/{id}` | — | `200` `OrderResponse` / `404` |
| POST | `/api/orders` | `{ "tableId": "T2", "waiterId": "W001" }` | `201` `OrderResponse` — **auth required** |
| POST | `/api/orders/{id}/items` | `{ "menuItemId": "M001", "quantity": 2, "instructions": "extra cheese" }` | `200` `OrderResponse` / `404` — **auth required** |
| DELETE | `/api/orders/{id}/items/{menuItemId}` | — | `200` `OrderResponse` / `404` — **auth required** |
| PATCH | `/api/orders/{id}/status?status=READY` | — | `200` `OrderResponse` / `404` — **auth required** |

### Reservations — `/api/reservations`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/reservations` | — | `200` `ReservationResponse[]` |
| POST | `/api/reservations` | `{ "customerName": "T. Mokoena", "tableId": "T3", "partySize": 4, "time": "2026-08-01T19:00:00" }` | `201` `ReservationResponse` — **auth required** |
| DELETE | `/api/reservations/{id}` | — | `200` / `404` — **auth required** |

### Error format

All errors return a consistent shape:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Order ORD999 not found",
  "path": "/api/orders/ORD999",
  "timestamp": "2026-07-13T10:22:00"
}
x






















# Plateable Eats

A Spring Boot + vanilla JS restaurant ordering system: live menu, table management,
order lifecycle (kitchen queue), and reservations, localized for the South African
market (ZAR pricing).

## Status

Early-stage portfolio project. Currently **in-memory persistence** (data resets on
restart) with an **H2 file-backed option** available — see Configuration below.
Write endpoints require basic auth (see Security). Not deployed yet.

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use the bundled `./mvnw`)

## Running locally

`git clone https://github.com/Banelenelson02/platable_eats.git`
`cd platable_eats`
`./mvnw spring-boot:run`

App starts on `http://localhost:8080`. Menu UI is served at `/`.

Run tests:

`./mvnw test`

## Configuration

`src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:mem:plateable` | In-memory H2. Swap to a file path or Postgres URL for persistence across restarts |
| `spring.h2.console.enabled` | `true` | H2 web console at `/h2-console` (dev only — disable in prod) |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema strategy; use `validate` in production with real migrations |

## API Reference

Interactive docs (Swagger UI): `http://localhost:8080/swagger-ui.html`

### Menu — `/api/menu`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/menu` | — | `200` `MenuItemResponse[]` |
| GET | `/api/menu/{id}` | — | `200` `MenuItemResponse` / `404` |
| PATCH | `/api/menu/{id}` | `{ "price": 130.00, "available": false }` | `200` `MenuItemResponse` / `404` — **auth required** |

### Tables — `/api/tables`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/tables` | — | `200` `TableResponse[]` |
| GET | `/api/tables/available?minCapacity=4` | — | `200` `TableResponse[]` |
| POST | `/api/tables` | `{ "tableId": "T5", "capacity": 4 }` | `201` `TableResponse` — **auth required** |

### Orders — `/api/orders`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/orders` | — | `200` `OrderResponse[]` |
| GET | `/api/orders?status=IN_KITCHEN` | — | `200` `OrderResponse[]` |
| GET | `/api/orders/{id}` | — | `200` `OrderResponse` / `404` |
| POST | `/api/orders` | `{ "tableId": "T2", "waiterId": "W001" }` | `201` `OrderResponse` — **auth required** |
| POST | `/api/orders/{id}/items` | `{ "menuItemId": "M001", "quantity": 2, "instructions": "extra cheese" }` | `200` `OrderResponse` / `404` — **auth required** |
| DELETE | `/api/orders/{id}/items/{menuItemId}` | — | `200` `OrderResponse` / `404` — **auth required** |
| PATCH | `/api/orders/{id}/status?status=READY` | — | `200` `OrderResponse` / `404` — **auth required** |

### Reservations — `/api/reservations`

| Method | Path | Body | Response |
|---|---|---|---|
| GET | `/api/reservations` | — | `200` `ReservationResponse[]` |
| POST | `/api/reservations` | `{ "customerName": "T. Mokoena", "tableId": "T3", "partySize": 4, "time": "2026-08-01T19:00:00" }` | `201` `ReservationResponse` — **auth required** |
| DELETE | `/api/reservations/{id}` | — | `200` / `404` — **auth required** |

### Error format

All errors return a consistent shape:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Order ORD999 not found",
  "path": "/api/orders/ORD999",
  "timestamp": "2026-07-13T10:22:00"
}
