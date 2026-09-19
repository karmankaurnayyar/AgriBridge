# AgriBridge Backend

Spring Boot 3 / Java 21 backend for AgriBridge. See the root [README.md](../README.md)
for full project setup; this file covers backend-specific details only.

## Week 2 status

| Module | Entity | Repository | Service | Controller | Status |
|---|---|---|---|---|---|
| Authentication | User | ✅ | ✅ | ✅ | **Implemented** |
| Farm Management | Farm | ✅ | ✅ | ✅ | **Implemented** |
| Crop Management | Crop | ✅ | — | — | Schema ready; planned Week 3 |
| Harvest Lot Management | HarvestLot | ✅ | — | — | Schema ready; planned Week 3 |
| Buyer Requests | BuyerRequest | ✅ | — | — | Schema ready; planned Week 4 |
| Matching Engine | Match | (table only) | — | — | Planned Week 4 |
| Dashboard/Analytics | — | — | — | — | Planned Week 4 |

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

Covers `FarmServiceTest` and `AuthServiceTest` (JUnit 5 + Mockito), exercising
ownership checks, validation-adjacent behavior, and password-hashing
behavior without touching a real database.

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
├── farm/                          — Farm Management module (implemented)
├── crop/                          — Crop entity + repository (planned Week 3)
├── harvest/                       — HarvestLot entity + repository (planned Week 3)
└── buyer/                         — BuyerRequest entity + repository (planned Week 4)
```

Each module is a Java package with its own entity, repository, and (for
implemented modules) service and controller — see
[docs/architecture.md](../docs/architecture.md) for the reasoning behind this
modular-monolith structure.
