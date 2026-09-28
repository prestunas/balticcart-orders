# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Commands

Run Maven commands inside `balticcart-orders/` using the wrapper:

```bash
cd balticcart-orders
./mvnw clean compile                     # Build project
./mvnw test                              # Run all tests
./mvnw test -Dtest=ClassName             # Run single test class (e.g. BalticcartOrdersApplicationTests)
./mvnw test -Dtest=ClassName#methodName  # Run single test method
./mvnw spring-boot:run                   # Start application locally
```

## Stack & Architecture

- **Java 21**, **Spring Boot 4.1.1** (Spring Framework 7.x / Hibernate 7.x baseline) with Jakarta persistence and Jackson v3 (`tools.jackson.databind`).
- **Base Package**: Root package uses uppercase segments `BalticCart.Orders` (e.g., `BalticCart.Orders.order`), not standard lowercase.
- **In-Memory H2 DB**: H2 console enabled at `/h2-console`, JDBC URL `jdbc:h2:mem:balticcart`.
- **Demo Data Seeding**: `DemoOrderDataSeeder` seeds 60 `CustomerOrder` records on startup if table is empty.

## Code Style & Conventions

- **Packages**: Use singular package names (e.g., `repository`, `mapper`, `util`, `dto`).
- **Lombok `@FieldDefaults`**: Use `@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)` rather than explicit `private final` fields.
- **Dependency Injection**: Use constructor injection via Lombok `@RequiredArgsConstructor`; never use field injection or `@Autowired`.
- **REST Layer**: Controllers only handle HTTP mapping and delegation—no business logic.
- **DTOs / Responses**: Use records or MapStruct mappers; do not expose entities directly across API boundaries.
- **Date & Time**: Always use `java.time` types (`Instant`, `LocalDateTime`, `Clock`). Never use legacy `java.util.Date` or `Calendar`.
