# AGENTS.md - Ask Mode

This file provides non-obvious project context when answering questions about the codebase.

- **Project Location**: The backend module is nested inside `balticcart-orders/`, containing `pom.xml`, source directories, and `./mvnw`.
- **Package Hierarchy**: Base Java packages are PascalCase `BalticCart.Orders`, containing `order` feature module (`CustomerOrder`, `OrderController`, `OrderRepository`, `OrderResponse`, `OrderStatus`, `DemoOrderDataSeeder`).
- **Spring Boot 4 Baseline**: Project is on Spring Boot 4.1.1 (Java 21), using Jakarta persistence, Hibernate 7, and Jackson 3 (`tools.jackson.*`).
- **Database & Data Seeding**: Uses an in-memory H2 database (`jdbc:h2:mem:balticcart`) with automatic schema updates (`hibernate.ddl-auto=update`) and 60 seeded mock orders created at boot.
