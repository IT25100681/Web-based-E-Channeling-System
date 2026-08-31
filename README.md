# Web-based E-Channeling System
**Group 2026-Y2-S1-B5G2-10 · Spring Boot + Thymeleaf + MySQL**

Welcome to the E-Channeling System project setup. This repository contains the source code for the Web-based E-Channeling system for hospital channelings, patient consultations, doctor scheduling, electronic prescriptions, medical history, complaints, and administrative management.

---

## 🛠 Tech Stack

- **Language & Runtime:** Java 17 / Java 21 (LTS)
- **Framework:** Spring Boot 3.3.2
- **Build Tool:** Apache Maven
- **Template Engine:** Thymeleaf + Thymeleaf Layout Dialect
- **Styling:** Bootstrap 5
- **Persistence:** Spring Data JPA (Hibernate)
- **Database Migrations:** Flyway (`classpath:db/migration`)
- **Database Engine:** MySQL 8 (`echanneling_db`)
- **Security:** Spring Security 6 + BCrypt password hashing

---

## 📁 Layered Architecture & Package Layout

```
com.sliit.echanneling
├── EChannelingApplication.java
├── controller/          ── Presentation layer (Controllers & View handling)
├── dto/                 ── Data Transfer Objects (request/ & response/)
├── service/             ── Service interfaces & business logic (impl/)
├── repository/          ── Spring Data JPA Repositories
├── model/               ── JPA Entities, Embedded types, and Enums
├── mapper/              ── Entity <-> DTO Mappers
├── security/            ── Spring Security & UserDetailsService
├── exception/           ── Global Exception Handlers
└── config/              ── Security & Web MVC Configurations
```

### 🚨 Architectural Guidelines
1. **Controllers NEVER inject Repositories directly.** Controllers interact exclusively with Services.
2. **Entities NEVER reach Thymeleaf views.** Pass DTOs to view models to prevent `LazyInitializationException` and security data leaks.
3. **Services handle `@Transactional` boundaries.**

---

## 🚀 Local Setup Instructions

### 1. Prerequisites
- Java JDK 17 or JDK 21 installed.
- Apache Maven 3.8+ installed.
- MySQL 8 server running locally on port `3306`.

### 2. Database Preparation
Create a MySQL database for local development:
```sql
CREATE DATABASE IF NOT EXISTS echanneling_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Default connection settings in `src/main/resources/application-dev.yml`:
- **URL:** `jdbc:mysql://localhost:3306/echanneling_db`
- **Username:** `root`
- **Password:** `password`

*(Override credentials in environment variables or your local profile without committing secrets to Git).*

### 3. Build & Run
Run the application locally using Maven:
```bash
# Clean and compile
mvn clean compile

# Run Spring Boot Application
mvn spring-boot:run
```
The application will launch on `http://localhost:8080`.

---

## 🌿 Git Branching Strategy

To minimize merge conflicts across team members:
- **`main`**: Production-ready code. Protected branch.
- **`dev`**: Integration branch for current iteration.
- **Feature Branches**: `feature/<feature-name>` (e.g., `feature/appointment-booking`, `feature/doctor-schedule`, `feature/prescriptions`).

### Workflow Rules:
1. Always pull latest `dev` before creating a feature branch.
2. Commit in logical, clean increments.
3. Ensure `mvn test` passes before opening a Pull Request into `dev`.
