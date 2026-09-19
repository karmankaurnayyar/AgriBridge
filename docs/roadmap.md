# AgriBridge — Roadmap (Individual, 4-Week Project)

Developer: **Karman Kaur Nayyar** · Junior Software Developer Intern ·
Individual project.

## Week 1 — Requirements (complete, prior deliverable)

Problem research, requirements, user roles, MVP scope, feasibility.

## Week 2 — Architecture & Development Foundation (this deliverable)

**Implemented:**
- Maven project skeleton (Spring Boot 3.3, Java 21) that builds a runnable
  Spring Boot application.
- Full MySQL schema for the entire planned data model
  (`database/schema.sql`) plus sample data.
- Authentication module: registration, login, BCrypt hashing, JWT issuance
  (`auth` package) — working end-to-end against MySQL.
- Farm Management module: full CRUD, ownership enforcement, role-restricted
  writes (`farm` package) — working end-to-end against MySQL.
- Crop, HarvestLot and BuyerRequest JPA entities + repositories (schema-ready,
  no service/controller yet).
- Global exception handling with a consistent JSON error shape.
- Static frontend foundation: login, register, dashboard and farms pages
  wired to the implemented APIs via the Fetch API; crops/harvests/buyers
  pages present as placeholders describing what's coming.
- Architecture, component, data-flow and ER diagrams (Mermaid, GitHub-native).
- API documentation and a Postman collection covering implemented +
  planned endpoints.
- Unit tests for `FarmService` and `AuthService` (JUnit 5 + Mockito).

**Explicitly not implemented in Week 2:** Crop/Harvest Lot/Buyer Request
service & controller layers, the matching engine, the dashboard's live data,
notifications, JWT-filter enforcement (HTTP Basic is used instead for now),
and any deployment beyond local development.

## Week 3 — Core Domain Modules (planned)

- Crop Management: `CropService` + `CropController` (full CRUD), wired to
  the existing `Crop` entity.
- Harvest Lot Management: `HarvestLotService` + `HarvestLotController`,
  including server-side lot-code generation.
- `JwtAuthenticationFilter` wired into `SecurityConfig`, replacing HTTP
  Basic as the actual enforcement mechanism (the token itself already
  works — see `JwtUtil`).
- Expanded test coverage: controller tests (`MockMvc`) and repository
  tests for the new modules.
- Frontend: complete the `crops.html` and `harvests.html` pages against the
  new endpoints.

## Week 4 — Buyer Matching, Dashboard, Polish (planned)

- Buyer Request Management: `BuyerRequestService` + `BuyerRequestController`.
- Rule-based Matching engine (`Match` entity + service), filtering by
  commodity, quantity, location and price range — see
  [diagrams/buyer-matching-flow.md](diagrams/buyer-matching-flow.md).
- Dashboard/analytics endpoint aggregating counts across modules, and the
  `dashboard.html` page reading from it (Week 2's dashboard page shows
  static/placeholder counts only).
- Integration tests covering full request paths.
- Basic deployment write-up (packaged JAR + managed MySQL).
- Final documentation pass and demo walkthrough.

## Beyond Week 4 (not committed to any date)

FPO/Coordinator role, offline-capable frontend, live external market-price
integration, SMS/email notification delivery, a non-rule-based matching
model once real transaction data exists to justify one.
