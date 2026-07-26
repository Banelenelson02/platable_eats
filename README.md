# Plateable Eats

> A full-stack casual dining platform — handcrafted frontend meets enterprise Spring Boot backend.

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-green?style=flat-square)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-BCrypt-green?style=flat-square)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue?style=flat-square)](https://supabase.com)
[![Swagger](https://img.shields.io/badge/API_Docs-Swagger_UI-85EA2D?style=flat-square)](http://localhost:8080/swagger-ui/index.html)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

---

## Live Demo

| | Link |
|---|---|
| 🌐 Frontend | *Deploying soon* |
| 📡 API Docs (Swagger UI) | `http://localhost:8080/swagger-ui/index.html` |
| ❤️ Health Check | `http://localhost:8080/actuator/health` |

---

## The Project

Plateable Eats is a full-stack restaurant ordering and reservation system built in two phases:

**Phase 1 — OOD Design:** The system started as a vanilla Java in-memory application using pure Object-Oriented Design principles — no framework, no database, just clean domain modelling.

**Phase 2 — N-Tier Architecture:** The system was then deliberately refactored into a decoupled, enterprise-ready N-Tier RESTful architecture powered by Spring Boot — separating concerns across controller, service, repository, and domain layers.

This architectural evolution is the core story of the project: recognising when a design has outgrown its foundation, and rebuilding it properly.

The system is fully localized for the South African casual dining market, with menu pricing in ZAR.

---

## Screenshots

> *Screenshots coming once deployed — run locally to see the full UI.*

The frontend features:
- Swiper.js hero slider with floating dish animations
- Live menu search powered by `GET /api/menu`
- Filterable dish grid (All / Mains / Pasta / Wraps)
- Shopping basket drawer with real-time subtotal
- Order form that posts directly to `POST /api/orders`
- Scroll reveal animations with `IntersectionObserver`
- Fully responsive — mobile, tablet, desktop

---

## Architecture

```
┌──────────────────────────────────────────────────────┐
│              FRONTEND (Presentation Layer)            │
│  HTML5 (semantic) + CSS3 (BEM) + Vanilla ES6+        │
│  Swiper.js hero · Cart drawer · Fetch API to backend  │
└──────────────────────────┬───────────────────────────┘
                           │ HTTP / JSON
                           ▼
┌──────────────────────────────────────────────────────┐
│           SPRING BOOT APPLICATION LAYER               │
│                                                       │
│  Controllers (REST endpoints + @Valid)                │
│       ↓                                               │
│  Services (business logic + findOrThrow pattern)      │
│       ↓                                               │
│  Repositories (Spring Data JPA + custom queries)      │
│       ↓                                               │
│  Domain Models (@Entity + JPA relationships)          │
└──────────────────────────┬───────────────────────────┘
                           │ JPA / Hibernate
                           ▼
┌──────────────────────────────────────────────────────┐
│              PostgreSQL (Supabase)                    │
│  menu_items · orders · order_items                   │
│  restaurant_tables · reservations · employees         │
└──────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Language | Java 17 | Backend runtime |
| Framework | Spring Boot 3.3 | Application framework |
| Web | Spring MVC | REST controllers |
| Persistence | Spring Data JPA + Hibernate | ORM + repositories |
| Database | PostgreSQL (Supabase) | Persistent storage |
| Security | Spring Security + BCrypt | Authentication + password hashing |
| Validation | Jakarta Bean Validation | `@Valid` request validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) | Interactive API documentation |
| Monitoring | Spring Actuator | Health checks |
| Build | Maven | Dependency management |
| Frontend | HTML5 + CSS3 (BEM) + Vanilla ES6+ | UI — zero framework dependencies |
| Animation | Swiper.js | Hero slider |

---

## Project Structure

```
src/main/java/com/plateable/
├── controller/               # REST endpoints — HTTP layer only
│   ├── MenuController.java
│   ├── OrderController.java
│   ├── ReservationController.java
│   └── TableController.java
├── service/                  # Business logic — no HTTP concerns
│   ├── MenuService.java
│   ├── OrderService.java
│   ├── ReservationService.java
│   └── TableService.java
├── repository/               # Spring Data JPA — data access only
│   ├── MenuItemRepository.java
│   ├── OrderRepository.java
│   ├── ReservationRepository.java
│   └── TableRepository.java
├── model/                    # JPA entities + enums
│   ├── MenuItem.java
│   ├── Order.java            # @OneToMany with OrderItems
│   ├── OrderItem.java
│   ├── OrderStatus.java      # PENDING, PREPARING, READY, DELIVERED
│   ├── Reservation.java
│   ├── ReservationStatus.java
│   ├── Table.java            # @Table(name="restaurant_tables")
│   ├── TableStatus.java
│   └── Employee.java
├── dto/
│   ├── request/              # Validated inbound request bodies
│   └── response/             # Outbound response shapes
├── exception/
│   └── ResourceNotFoundException.java
├── config/
│   ├── SecurityConfig.java   # BCrypt + role-based access control
│   └── DataSeeder.java       # Idempotent seed on startup
└── PlateableEatsApplication.java

src/main/resources/static/    # Frontend (served by Spring Boot)
├── index.html
├── css/style.css             # 18-section BEM design system
└── script/script.js          # 12-module vanilla ES6+ architecture
```

---

## API Endpoints

All endpoints are documented interactively at `/swagger-ui/index.html` when running locally.

### Menu
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/menu` | Public | Get full menu |
| `GET` | `/api/menu/{id}` | Public | Get single item |
| `PUT` | `/api/menu/{id}` | Staff | Update price or availability |

### Orders
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/orders` | Staff | Get all orders (optional `?status=PENDING`) |
| `GET` | `/api/orders/{id}` | Staff | Get single order |
| `POST` | `/api/orders` | Staff | Create new order |
| `POST` | `/api/orders/{id}/items` | Staff | Add item to order |
| `DELETE` | `/api/orders/{id}/items/{menuItemId}` | Staff | Remove item |
| `PATCH` | `/api/orders/{id}/status` | Staff | Update order status |

### Tables
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/tables` | Public | Get all tables |
| `GET` | `/api/tables/available?minCapacity=2` | Public | Find available tables |
| `POST` | `/api/tables` | Staff | Create table |

### Reservations
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/reservations` | Staff | Get all reservations |
| `POST` | `/api/reservations` | Staff | Create reservation |
| `DELETE` | `/api/reservations/{id}` | Staff | Cancel reservation |

---

## Security Model

```
Public (no auth required):
  GET /api/menu/**
  GET /api/tables/**
  GET /, /*.html, /css/**, /script/**, /images/**
  GET /swagger-ui/**, /v3/api-docs/**
  GET /actuator/health

Staff (Basic Auth required):
  All POST, PUT, PATCH, DELETE endpoints
```

**Credentials (local dev):**
```
Username: staff
Password: changeme
```

Passwords are hashed with BCrypt. In production, replace the in-memory user store with a database-backed `UserDetailsService`.

---

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- A PostgreSQL database (Supabase free tier recommended)

### 1. Clone the repo
```bash
git clone https://github.com/Banelenelson02/platable_eats.git
cd platable_eats
```

### 2. Set up your database

Sign up at [supabase.com](https://supabase.com) → New Project → Settings → Database → Connection string → **JDBC**.

It looks like:
```
jdbc:postgresql://db.[ref].supabase.co:5432/postgres
```

### 3. Configure environment variables
```bash
export DATABASE_URL=jdbc:postgresql://db.[ref].supabase.co:5432/postgres
export DB_USERNAME=postgres
export DB_PASSWORD=your-supabase-password
```

Or create a `.env` file (never commit this):
```env
DATABASE_URL=jdbc:postgresql://db.[ref].supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your-supabase-password
```

### 4. Run
```bash
mvn spring-boot:run
```

On first startup, Hibernate automatically creates all tables and the `DataSeeder` populates:
- 5 menu items (Wood-fired Pizza, Butternut Bisque, Beef Pot Pie, Malva Pudding, Caramel Tart)
- 4 restaurant tables (T1–T4, capacities 2–6)

### 5. Open the app
```
Frontend:    http://localhost:8080
Swagger UI:  http://localhost:8080/swagger-ui/index.html
Health:      http://localhost:8080/actuator/health
```

---

## Running the Tests

```bash
mvn test
```

The integration test (`OrderLifecycleIntegrationTest`) spins up the full Spring Boot context and runs a complete order lifecycle:
1. Authenticates as `staff`
2. Creates an order for Table T2
3. Adds 2 Wood-fired Pizzas (M001)
4. Asserts the total is R240.00 (2 × R120.00)

---

## Key Design Decisions

**Why N-Tier architecture?**
The project started as a vanilla Java in-memory system. As the domain grew (orders → order items → tables → reservations), the lack of layer separation made the codebase hard to test and extend. Refactoring to a proper N-Tier architecture meant controllers never touch repositories, services never know about HTTP, and domain models never know about either.

**Why `findOrThrow` in services?**
Every service method that fetches by ID uses a private `findOrThrow()` helper that throws `ResourceNotFoundException` on miss. This keeps controllers clean — no null checks, no `Optional.get()` — and produces consistent 404 responses via the exception handler.

**Why vanilla JS (no React/Vue)?**
The frontend is intentionally dependency-free. It demonstrates that a production-quality UI — module pattern, live API integration, accessibility, cart state management, form validation — doesn't require a framework. Every feature uses browser-native APIs.

**Why idempotent seeding?**
`DataSeeder` checks `menuRepo.count() == 0` before inserting. This means the seed runs safely on every startup without duplicating data — important when connecting to a persistent PostgreSQL database rather than H2.

---

## Roadmap

- [x] In-memory OOD Java system (Phase 1)
- [x] N-Tier Spring Boot REST architecture (Phase 2)
- [x] Spring Security with BCrypt
- [x] Spring Data JPA with custom queries
- [x] Swagger UI / OpenAPI documentation
- [x] Spring Actuator health endpoint
- [x] Integration tests
- [x] Idempotent data seeder
- [ ] PostgreSQL via Supabase (replacing H2)
- [ ] Deploy to Railway
- [ ] JWT authentication (replacing Basic Auth)
- [ ] More test coverage (@WebMvcTest per controller)
- [ ] Admin dashboard for staff

---

## Background

Built as part of my software development studies at WeThinkCode_. The architectural evolution from OOD to N-Tier is intentional — it mirrors the kind of refactoring decisions real teams make when a system outgrows its initial design.

**Banele Ntuli** — Software development student, WeThinkCode_ · [GitHub](https://github.com/Banelenelson02)