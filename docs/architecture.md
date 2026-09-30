# AgriBridge — Architecture Documentation

Individual project — developed by **Karman Kaur Nayyar**, Junior Software
Developer Intern. This document covers the Week 2 architecture foundation.
For the day-by-day breakdown of what's implemented vs. planned, see
[roadmap.md](roadmap.md).

## 1. Overview

AgriBridge is a role-based platform connecting farmers, buyers and (in a
later phase) administrators around farm records, crops, harvest lots and
buyer requirements. It is built as a **three-tier, modular monolith**:

1. **Presentation Layer** — static HTML5/CSS3/vanilla JavaScript, calling
   the backend via the Fetch API.
2. **Business/Application Layer** — Spring Boot `@RestController` classes
   delegating to `@Service` classes that hold validation and business rules.
3. **Data Access Layer** — Spring Data JPA repositories over Hibernate ORM,
   persisting to MySQL.

See [diagrams/system-architecture.md](diagrams/system-architecture.md) for
the rendered diagram.

## 2. Why a Modular Monolith, Not Microservices

For a single developer working within a four-week timeline, microservices
would add overhead (inter-service network calls, multiple deployment
pipelines, service discovery) with no corresponding benefit at this scale.
A modular monolith gives the same logical separation — each business area
(`auth`, `farm`, `crop`, `harvest`, `buyer`) is its own Java package with its
own entity/repository/service/controller boundary — while keeping
deployment, debugging and local development simple. See
[diagrams/component-interaction.md](diagrams/component-interaction.md).

## 3. Module Responsibilities

| Module (package) | Responsibility | Week 2 Status |
|---|---|---|
| `user` | User entity, Role enum, shared across modules | Implemented |
| `auth` | Registration, login, password hashing, JWT issuance | **Implemented (Week 2)** |
| `farm` | Farm CRUD, ownership enforcement | **Implemented (Week 2)** |
| `crop` | Crop CRUD, farm-ownership enforcement, date/status validation | **Implemented (Week 3)** |
| `harvest` | HarvestLot entity/repository | Schema ready — service/controller **planned Week 4** |
| `buyer` | BuyerRequest entity/repository | Schema ready — service/controller **planned Week 4** |
| Matching engine | Rule-based lot/request matching | **Planned Week 4** |
| Dashboard/analytics | Aggregate counts across modules | **Planned Week 4** |

## 4. Data Flow

See the individual flow diagrams under [diagrams/](diagrams/):
registration & login, farm & crop registration, harvest lot (planned), and
buyer request & matching (planned).

## 5. Database Relationships

See [database-design.md](database-design.md) and
[diagrams/erd.md](diagrams/erd.md). In short: one `User` owns many `Farms`;
one `Farm` grows many `Crops`; one `Crop` produces `HarvestLots`; a `Match`
links one `HarvestLot` to one `BuyerRequest`.

## 6. Security Approach

- **Password hashing:** BCrypt via Spring Security's `BCryptPasswordEncoder`
  (`SecurityConfig`), implemented and exercised by `AuthServiceTest`.
- **Authentication (Week 2):** HTTP Basic, backed by `CustomUserDetailsService`
  reading the same `users` table and BCrypt hashes. This gives working,
  testable authentication today.
- **JWT (foundation):** `/api/auth/login` and `/api/auth/register` already
  return a signed JWT via `JwtUtil`, so the frontend and Postman collection
  are forward-compatible. Wiring a `JwtAuthenticationFilter` into the
  Spring Security filter chain — so the token itself is what protects
  endpoints, instead of HTTP Basic — was reassessed during Week 3 and moved
  to **Week 4** (Crop Management was chosen as the single Week 3 focus
  instead - see docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md,
  "Reason for Selecting" - so both cannot fit in one week without spreading
  Week 3 too thin).
- **Authorization:** role checks via `@PreAuthorize` on controller methods
  (e.g., only `FARMER`/`ADMIN` can create/update/delete a farm) in addition
  to URL-pattern rules in `SecurityConfig`.
- **Input validation:** Bean Validation annotations on every request DTO.
- **Error handling:** a single `GlobalExceptionHandler` returns a
  consistent JSON error shape and never leaks stack traces.
- **Ownership enforcement:** `FarmService` resolves the authenticated user
  and scopes every read/update/delete to farms that user owns.
- **Secrets:** no credentials are committed; `application.properties` reads
  everything from environment variables with local-only fallbacks (see
  `backend/src/main/resources/application-dev.properties.example`).

## 7. Testing Strategy

- **Unit tests** (`FarmServiceTest`, `AuthServiceTest` from Week 2;
  `CropServiceTest` added in Week 3 — 21 test methods covering normal,
  boundary, invalid-input, null-input and business-rule-violation cases)
  using JUnit 5 and Mockito, exercising ownership checks, duplicate-email
  rejection, date/status validation, and BCrypt hashing behavior without a
  live database. See docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md
  for the full test case table and execution notes.
- **Planned for Week 4:** controller-level tests with `MockMvc`,
  repository tests against a test database, and integration tests covering
  full request paths once the Crop/Harvest Lot/Buyer Request modules are
  built out.
- **Manual testing:** the Postman collection (`postman/AgriBridge_API_Collection.json`)
  covers every implemented endpoint.

## 8. Deployment Approach

Week 2 targets **local development only**: `mvn spring-boot:run` against a
local MySQL instance. A basic single-VM or PaaS deployment (packaged JAR +
managed MySQL) is a reasonable Week 4 step once the MVP feature set is
complete; containerization (Docker) is a good next step after that, but is
not a Week 2–4 blocker.

## 9. Future Improvements

See [roadmap.md](roadmap.md) for Week 3/4 plans. Beyond the four-week
window, reasonable next steps (not committed to any date) include: full JWT
filter-chain enforcement, the FPO/Coordinator role, offline-capable
frontend behavior, external market-price integration, and a smarter
(non-rule-based) matching algorithm once enough real transaction data
exists to justify one.
