# AGENTS.md - Agent Mode

This file provides non-obvious advance coding and development rules for agents.

- **Non-Standard Package Casing**: Package names follow `BalticCart.Orders.<feature>` (e.g. `BalticCart.Orders.order.CustomerOrder`). Keep casing consistent when creating new files.
- **Spring Boot 4.x / Jackson 3 Imports**: Jackson classes reside under `tools.jackson.databind.*` (Jackson v3 package structure), not `com.fasterxml.jackson.databind.*`.
- **Field Modifiers**: Use `@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)` with `@RequiredArgsConstructor` instead of manually writing `private final` fields.
- **Seeder Count in Tests**: `DemoOrderDataSeeder` seeds 60 demo records upon startup (`count() > 0` check). End-to-end and slice tests against initialized context must account for 60 initial orders.
- **Working Directory**: The Maven wrapper and POM are located in subdirectory `balticcart-orders/`. Always execute `./mvnw` from that directory.
