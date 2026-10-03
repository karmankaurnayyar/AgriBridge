# AgriBridge — Future Enhancement Recommendations

Prioritized roadmap beyond the four-week internship window. Each item states
the problem/opportunity, expected benefit, a brief implementation outline,
and priority. None of these are implemented — they are recommendations only.

---

## High Priority (reliability, validation, security, test coverage)

### H-1. Wire `JwtAuthenticationFilter` into the Spring Security filter chain
- **Problem/Opportunity**: A JWT is issued at login but HTTP Basic is what
  actually protects endpoints today (R-03 in `BUG_REGISTER.md`).
- **Expected Benefit**: Stateless, standard token-based authentication
  suitable beyond local development; removes the need to resend raw
  credentials on every request.
- **Implementation Outline**: Add a `OncePerRequestFilter` that reads the
  `Authorization: Bearer` header, validates it via the existing `JwtUtil`,
  and populates the `SecurityContext`; register it before
  `UsernamePasswordAuthenticationFilter` in `SecurityConfig`.
- **Priority**: High.

### H-2. Add controller-level (`MockMvc`) and integration tests
- **Problem/Opportunity**: All 34 current tests (27 Week 3 + 7 Week 4) are
  service-level unit tests against mocked repositories; the HTTP layer
  (status codes, security filter behavior, JSON serialization) is only
  validated manually via Postman.
- **Expected Benefit**: Would have caught BUG-001 automatically rather than
  requiring manual code review.
- **Implementation Outline**: `@WebMvcTest` for each controller with a
  mocked service layer and a mocked security context (`@WithMockUser`), or
  `@SpringBootTest` with an in-memory/test database (e.g., H2 in
  MySQL-compatibility mode, or Testcontainers if available) for true
  integration coverage.
- **Priority**: High.

### H-3. Expand validation coverage for Harvest Lot and Buyer Request entities before building their service/controller layers
- **Problem/Opportunity**: These entities exist but have no service layer
  yet; defining their validation rules now (alongside Crop's precedent)
  will keep Week 5-and-beyond implementation consistent.
- **Expected Benefit**: Avoids the same category of gap BUG-001 represents,
  by designing error handling alongside the feature rather than after.
- **Implementation Outline**: Follow the exact `CropService`/`CropController`
  pattern already established.
- **Priority**: High.

---

## Medium Priority (pagination, query optimization, logging, monitoring)

### M-1. Add pagination to list endpoints
- **Problem/Opportunity**: `GET /api/farms` and `GET /api/crops` return an
  unbounded list (R-04 in `BUG_REGISTER.md`).
- **Expected Benefit**: Predictable response sizes and latency as data
  grows.
- **Implementation Outline**: Accept `page`/`size` query parameters and
  return Spring Data's `Page<T>` (or a custom paged response wrapper).
- **Priority**: Medium.

### M-2. Combine CropService's two-level ownership check into a single query
- **Problem/Opportunity**: `resolveOwnedCrop()` issues two sequential
  repository calls (see `PERFORMANCE_EVALUATION.md`, Section 5).
- **Expected Benefit**: One fewer round-trip per crop read/update/delete.
- **Implementation Outline**: Add a `CropRepository` method such as
  `findByIdAndFarm_OwnerId(Long id, Long ownerId)` using a JPA relationship
  or a custom `@Query` joining `crops` to `farms`.
- **Priority**: Medium.

### M-3. Add structured application logging
- **Problem/Opportunity**: The application currently relies on Spring
  Boot's default logging with no structured, correlation-friendly format.
- **Expected Benefit**: Easier debugging and monitoring once deployed.
- **Implementation Outline**: Add a logging pattern/format (e.g., JSON logs)
  and log key lifecycle events (registration, farm/crop creation, access
  denials) at INFO level.
- **Priority**: Medium.

### M-4. CORS hardening
- **Problem/Opportunity**: `allowedOriginPatterns("*")` (R-01).
- **Expected Benefit**: Removes an unnecessary attack surface before any
  real deployment.
- **Implementation Outline**: Replace the wildcard with the actual deployed
  frontend origin(s), sourced from an environment variable.
- **Priority**: Medium (High immediately before deployment).

---

## Longer Term (farmer/buyer workflows, market data, analytics, deployment)

### L-1. Harvest Lot Management (service + controller)
Builds on the existing `HarvestLot` entity/repository; natural next module
after Crop Management, per `docs/roadmap.md`.

### L-2. Buyer Requests and rule-based Matching engine
Builds on the existing `BuyerRequest` entity; implements the
commodity/quantity/location/price-range matching logic already documented
in `docs/diagrams/buyer-matching-flow.md`.

### L-3. Market-price data integration
Currently out of scope entirely; would require an external data source
adapter, consistent with the integration-boundary pattern already noted in
`docs/architecture.md`.

### L-4. Notifications (in-app, then SMS/email)
Deferred since Week 2; would follow the adapter pattern already sketched in
`docs/diagrams/harvest-lot-flow.md`'s notion of a "planned adapter."

### L-5. Basic analytics/dashboard (live data)
The current dashboard shows live Farm and Crop counts only; a real
aggregate endpoint (`/api/dashboard/summary`) was deferred from Week 3 to
a future phase.

### L-6. Deployment
Containerization (Docker) and a basic cloud or single-VM deployment,
consistent with the "Deployment Approach" already sketched in
`docs/architecture.md` — deliberately not attempted in Week 4, per the
constraint against introducing deployment infrastructure not already part
of the existing project.

---

## Summary Table

| ID | Item | Priority |
|---|---|---|
| H-1 | JWT filter-chain enforcement | High |
| H-2 | Controller/integration tests | High |
| H-3 | Validation design for Harvest Lot / Buyer Request | High |
| M-1 | Pagination | Medium |
| M-2 | Single-query ownership check | Medium |
| M-3 | Structured logging | Medium |
| M-4 | CORS hardening | Medium |
| L-1 | Harvest Lot Management | Long term |
| L-2 | Buyer Requests + Matching | Long term |
| L-3 | Market-price integration | Long term |
| L-4 | Notifications | Long term |
| L-5 | Live dashboard analytics | Long term |
| L-6 | Deployment | Long term |
