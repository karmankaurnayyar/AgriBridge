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

## Week 3 — Code Implementation and Unit Testing (this deliverable)

The Week 2 plan for Week 3 originally listed both Crop Management **and**
Harvest Lot Management. The Week 3 task brief tightened scope to "one or two
core functionalities, small enough to implement and unit-test properly
within the week" — so **Crop Management was selected as the single Week 3
functionality** (see
[week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md](week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md),
"Reason for Selecting", for the full justification), and Harvest Lot
Management moves to Week 4 alongside Buyer Requests and Matching.

**Implemented:**
- Crop Management: `CropService` + `CropController` (full CRUD: create,
  list-by-farm, get, update, delete), wired to the existing `Crop` entity
  and enforcing farm ownership exactly as `FarmService` does for farms.
- Business-rule validation: expected harvest date cannot precede sowing
  date; crop status is restricted to a defined set and defaults to
  `PLANNED`.
- `crops.html` + `js/crops.js`: a working frontend (farm picker, add/edit/
  delete crop, validation and error display), replacing the Week 2
  placeholder page.
- `CropServiceTest`: 21 JUnit 5 + Mockito test methods covering normal,
  boundary, invalid-input, null-input and business-rule-violation cases.
- `docs/api-documentation.md`, `docs/database-design.md` and
  `docs/architecture.md` updated to move Crop Management from "planned" to
  "implemented", and the Postman collection updated to match.

**Explicitly not implemented in Week 3** (moved to Week 4):
- Harvest Lot Management (`HarvestLotService`/`HarvestLotController`).
- `JwtAuthenticationFilter` wiring — Week 2's plan to move this into Week 3
  was reassessed; keeping Week 3 focused on one thoroughly-tested
  functionality took priority. HTTP Basic remains the Week 3 enforcement
  mechanism.
- Controller-level (`MockMvc`) and integration tests — Week 3 tests are
  service-level (Mockito) only, consistent with Week 2's testing depth.

## Week 4 — Buyer Matching, Dashboard, Remaining Modules, Polish (planned)

- Harvest Lot Management: `HarvestLotService` + `HarvestLotController`,
  including server-side lot-code generation (moved from Week 3 — see above).
- Buyer Request Management: `BuyerRequestService` + `BuyerRequestController`.
- Rule-based Matching engine (`Match` entity + service), filtering by
  commodity, quantity, location and price range — see
  [diagrams/buyer-matching-flow.md](diagrams/buyer-matching-flow.md).
- `JwtAuthenticationFilter` wired into `SecurityConfig`, replacing HTTP
  Basic as the actual enforcement mechanism (the token itself already
  works — see `JwtUtil`).
- Dashboard/analytics endpoint aggregating counts across modules, and the
  `dashboard.html` page reading from it (currently shows live Farm and Crop
  counts computed client-side, with the rest as placeholders).
- Controller-level (`MockMvc`) and integration tests covering full request
  paths for all modules.
- Basic deployment write-up (packaged JAR + managed MySQL).
- Final documentation pass and demo walkthrough.

## Beyond Week 4 (not committed to any date)

FPO/Coordinator role, offline-capable frontend, live external market-price
integration, SMS/email notification delivery, a non-rule-based matching
model once real transaction data exists to justify one.
