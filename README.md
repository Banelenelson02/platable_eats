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
