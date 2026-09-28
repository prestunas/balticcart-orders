# AGENTS.md - Plan Mode

This file provides non-obvious architectural constraints and guidelines for planning changes.

- **Strict Controller Boundaries**: Controllers must not hold domain logic or direct DB mutations—delegate all business logic to service layers or helper components.
- **DTO Isolation**: Entities (`CustomerOrder`) must never be returned directly across REST endpoints; map through DTOs or Java records (`OrderResponse`).
- **Persistence & Seeding**: `DemoOrderDataSeeder` runs automatically on boot if `OrderRepository` is empty. Plan integration tests or migrations taking the 60 pre-seeded orders into account.
- **Package & Naming Structure**: Maintain singular package naming (`dto`, `mapper`, `repository`, `service`) and PascalCase base root `BalticCart.Orders`.
