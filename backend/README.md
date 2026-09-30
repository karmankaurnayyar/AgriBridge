# AgriBridge Backend

Spring Boot 3 / Java 21 backend for AgriBridge. See the root [README.md](../README.md)
for full project setup; this file covers backend-specific details only.

## Week 3 status

| Module | Entity | Repository | Service | Controller | Status |
|---|---|---|---|---|---|
| Authentication | User | ✅ | ✅ | ✅ | **Implemented (Week 2)** |
| Farm Management | Farm | ✅ | ✅ | ✅ | **Implemented (Week 2)** |
| Crop Management | Crop | ✅ | ✅ | ✅ | **Implemented (Week 3)** |
| Harvest Lot Management | HarvestLot | ✅ | — | — | Schema ready; planned Week 4 |
| Buyer Requests | BuyerRequest | ✅ | — | — | Schema ready; planned Week 4 |
| Matching Engine | Match | (table only) | — | — | Planned Week 4 |
| Dashboard/Analytics | — | — | — | — | Planned Week 4 |

See [docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md](../docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md)
for why Crop Management was selected as the Week 3 functionality.

## Running locally

```bash
cd backend
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties
# edit application-dev.properties with your local MySQL credentials
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application starts on `http://localhost:8080` by default.

> **Note on this environment:** this codebase was written and reviewed without
> a live internet connection to Maven Central, so `mvn compile` / `mvn test`
> could not be executed inside the assistant's sandbox to produce a build
> log. The code follows standard, current Spring Boot 3.3 / Spring Security 6
> APIs throughout. Please run `mvn clean install` as your first step after
> extracting the project and report any compiler errors so they can be fixed
> promptly — see "Known Limitations" in the root README.

## Running tests

```bash
cd backend
mvn test
```

Covers `FarmServiceTest`, `AuthServiceTest` (Week 2) and `CropServiceTest`
(Week 3 — 21 test methods across normal, boundary, invalid-input, null-input
and business-rule-violation cases). All use JUnit 5 + Mockito and exercise
ownership checks, validation logic, and password-hashing behavior without
touching a real database. See
[docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md](../docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md)
for the full test case table and an honest account of what could/could not
be executed in this development environment.

## Package layout

```text
com.agribridge
├── AgribridgeApplication.java     — entry point
├── config/                        — SecurityConfig, CorsConfig
├── security/                      — JwtUtil, CustomUserDetailsService
├── exception/                     — GlobalExceptionHandler + custom exceptions
├── common/                        — shared DTOs (ApiErrorResponse)
├── user/                          — User entity, Role enum, UserRepository
├── auth/                          — Authentication module (implemented)
├── farm/                          — Farm Management module (implemented, Week 2)
├── crop/                          — Crop Management module (implemented, Week 3)
├── harvest/                       — HarvestLot entity + repository (planned Week 4)
└── buyer/                         — BuyerRequest entity + repository (planned Week 4)
```

Each module is a Java package with its own entity, repository, and (for
implemented modules) service and controller — see
[docs/architecture.md](../docs/architecture.md) for the reasoning behind this
modular-monolith structure.
