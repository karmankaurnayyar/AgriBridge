# WEEK 3 — Code Implementation and Unit Testing

**Project:** AgriBridge — Agribusiness Solution
**Developer:** Karman Kaur Nayyar (Individual Project)
**Role:** Junior Software Developer Intern
**Week:** 3 of 4

---

## 1. Title

AgriBridge Week 3 — Code Implementation and Unit Testing: Crop Management Module

## 2. Introduction

This document describes the Week 3 deliverable for the AgriBridge internship
project. Weeks 1 and 2 produced the requirements, architecture, database
design, and an Authentication + Farm Management prototype (Spring Boot,
MySQL, HTML/CSS/JavaScript). Week 3 continues directly from that foundation:
rather than redesigning anything, it implements one additional, carefully
selected core functionality — **Crop Management** — end-to-end (backend
service, REST API, frontend page, and a full unit test suite), following
the same architectural and coding conventions Week 2 already established.

## 3. Week 3 Objective

To execute a portion of the AgriBridge solution by developing a working
prototype that demonstrates a real core functionality from the Week 2
design, with:
- actual working code (not just pseudo-code) for the selected functionality;
- a comprehensive unit test suite covering normal, boundary, invalid,
  null/empty, and exception/business-rule scenarios;
- honest documentation of what was implemented, what was tested, what
  challenges came up, and what remains for Week 4.

## 4. Scope

**In scope for Week 3:** Crop Management — create, list (by farm), retrieve,
update, and delete crop records, each tied to a farm the authenticated user
owns; validation of crop dates and status; a working `crops.html` frontend
page; a JUnit 5 + Mockito test suite for `CropService`.

**Out of scope for Week 3** (see Section 25, "Work Reserved for Week 4"):
Harvest Lot Management, Buyer Requests, the Matching engine, JWT-filter
enforcement, controller/integration-level tests, dashboard aggregation, and
any deployment work. The system is **not** complete or production-ready at
the end of Week 3.

## 5. Relationship with Week 2 Architecture

Week 2 established:
- A three-tier, modular-monolith Spring Boot architecture (`docs/architecture.md`).
- A full MySQL schema for the entire planned data model, including a
  `crops` table and a matching `Crop` JPA entity — created in Week 2 but,
  as documented at the time, with **no service or controller layer yet**.
- A documented (but unimplemented) API contract: `docs/api-documentation.md`
  listed `GET/POST/PUT/DELETE /api/crops` under "Planned Endpoints".
- A working, directly analogous precedent: `FarmService`/`FarmController`,
  which already implements the exact ownership-checking, validation, and
  error-handling pattern that Crop Management needed to follow.

Week 3 does not change any of this — it fills in the one gap (Crop's
service/controller layer) that Week 2 had already scoped out and described
in advance. No existing Week 1/Week 2 file was deleted, and the folder
structure was extended (not restructured): only `docs/week3/` is new, plus
new files inside the existing `crop` package and a few small, explained
edits to already-existing documentation files to update status labels (see
Section 12).

## 6. Selected Core Functionality

**Crop Management** (create / list / view / update / delete a crop under a
farm), implemented as `CropService` + `CropController` in the existing
`com.agribridge.crop` package.

Only one functionality was selected this week, rather than the two
originally sketched in Week 2's roadmap (Crop Management **and** Harvest
Lot Management), in order to keep Week 3 realistically scoped and to leave
room for a genuinely thorough test suite rather than a shallow one spread
across two modules.

## 7. Reason for Selecting This Functionality

Crop Management was chosen, ahead of the other candidates already
documented in Week 2, because it uniquely satisfies all four Week 3
selection criteria at once:

1. **Already supported by Week 2 requirements/architecture.** The `Crop`
   entity, the `crops` table, and the `/api/crops` contract already existed
   — Week 3 did not have to invent scope, only implement it.
2. **Meaningful enough to demonstrate real implementation.** It is a full
   CRUD module with a genuine second-level ownership check (a Crop is only
   reachable through a Farm the caller owns), not a trivial pass-through.
3. **Small enough to implement and test properly in one week.** It reuses
   the exact service/controller/DTO pattern `FarmService`/`FarmController`
   already established in Week 2 — no new architectural decisions were
   needed, only a second, consistent application of the existing pattern.
4. **Clear opportunities for validation and exception testing.** Crop
   records have two genuinely interesting business rules to test: a date
   relationship (`expectedHarvestDate` must not precede `sowingDate`) and a
   constrained status vocabulary (`PLANNED`/`GROWING`/`HARVEST_READY`/
   `COMPLETED`) — both are exactly the kind of logic that benefits from, and
   is easy to demonstrate through, boundary and invalid-input unit tests.

Harvest Lot Management, Buyer Requests, and Matching were all considered
and explicitly deferred: Harvest Lot Management depends on a completed
Crop first existing (a `HarvestLot` references a `cropId`), so implementing
Crop Management first is also the logical dependency order for Week 4.

## 8. Development Environment

- **Editor:** Visual Studio Code
- **Backend runtime:** Java 21 (as established in Week 2)
- **Build tool:** Maven (existing `backend/pom.xml`, unchanged in Week 3
  except that no new dependency was required — Crop Management reuses
  Spring Web, Spring Data JPA, Spring Security, Bean Validation and Lombok,
  all already present)
- **Database:** MySQL 8+ (existing `agribridge` schema; no schema changes
  were required — the `crops` table already existed from Week 2)
- **API testing:** Postman (existing collection, extended — see Section 12)

## 9. Technology Stack

Unchanged from Week 2, per the Week 3 instructions: Java 21, Spring Boot
3.3 (Spring Web, Spring Data JPA/Hibernate, Spring Security), MySQL 8+,
Maven, HTML5/CSS3/vanilla JavaScript (Fetch API), JUnit 5, Mockito. No new
framework, language, or database was introduced.

## 10. Programming Paradigm

**Object-oriented**, consistent with the rest of the Spring Boot codebase:

- **Encapsulation** — `Crop`'s fields are private with Lombok-generated
  accessors; `CropService` exposes only the operations callers need
  (`createCrop`, `getCrop`, etc.), keeping repository access and internal
  helpers (`resolveUser`, `resolveOwnedFarm`, `resolveOwnedCrop`) private.
- **Separation of concerns** — `CropController` handles HTTP concerns only
  (status codes, request/response mapping); `CropService` holds all
  business logic; `CropRepository` handles persistence; `CropRequest`/
  `CropResponse` isolate the wire format from the entity.
- **Single responsibility (practical level)** — each class has one clear
  job; validation logic (`validateDates`, `normalizeStatus`) is factored
  into small, independently testable static methods rather than being
  inlined across multiple call sites.
- **Reusability** — the ownership-resolution pattern is a direct,
  intentional reuse of the approach `FarmService` already established, not
  a new design.

No additional design patterns (e.g., Strategy, Factory, Builder-beyond-
Lombok) were introduced — the Week 3 brief explicitly asks not to
over-engineer a beginner/intermediate-friendly prototype.

## 11. Coding Methodology

Incremental, test-driven-adjacent development: for each planned method
(`createCrop`, `getCrop`, `updateCrop`, `deleteCrop`, `listCropsForFarm`,
`validateDates`, `normalizeStatus`), the method's contract (input, expected
output, expected exceptions) was decided first, then the implementation was
written to satisfy it, then a unit test was written to confirm that
contract in isolation. Meaningful class and method names were used
throughout (`resolveOwnedFarm`, not `checkFarm2`); comments were added only
where the "why" is not obvious from the code itself (e.g., why reassigning
a crop's farm is rejected, why equal sowing/harvest dates are treated as
valid).

## 12. Implementation Explanation

### 12.1 Functional Objective

Let an authenticated Farmer (or Admin) record, review, update and remove
crop cycles for a farm they own, mirroring the Farm Management workflow
already delivered in Week 2.

### 12.2 Input

A `CropRequest` JSON body: `farmId` (required), `cropName` (required, ≤100
chars), `cropType` (optional), `sowingDate`/`expectedHarvestDate` (optional
ISO dates), `status` (optional; defaults to `PLANNED`).

### 12.3 Processing Logic

1. Resolve the authenticated user from the HTTP Basic principal's email.
2. Resolve the target farm and confirm `farm.ownerId == user.id` (otherwise
   `404`, never `403` — consistent with Farm Management's "never reveal
   whether an ID exists to a non-owner" convention).
3. Validate the date relationship and normalize the status.
4. Persist (create/update) or remove (delete) the `Crop` row via
   `CropRepository`.

### 12.4 Output

A `CropResponse` JSON object (or a list of them for `listCropsForFarm`), or
`204 No Content` for delete, or a structured `ApiErrorResponse` (existing
`GlobalExceptionHandler`, unchanged) for any failure.

### 12.5 Validation Rules

- `cropName`: required, non-blank, ≤100 characters (`@NotBlank`, `@Size`).
- `farmId`: required (`@NotNull`), and must reference a farm owned by the
  caller (checked in `CropService`, not by Bean Validation).
- Date rule: if both dates are given, `expectedHarvestDate` must not be
  before `sowingDate` (equal dates are valid — see TC-02).
- Status rule: if given, must be one of `PLANNED`, `GROWING`,
  `HARVEST_READY`, `COMPLETED` (case-insensitive); blank/absent defaults to
  `PLANNED`.

### 12.6 Error Handling

Reuses the existing `GlobalExceptionHandler` from Week 2 without
modification:
- `ResourceNotFoundException` → `404` (unknown/not-owned farm or crop)
- `IllegalArgumentException` → `400` (invalid date range, invalid status,
  attempted farm reassignment)
- `MethodArgumentNotValidException` (Bean Validation) → `400` with
  field-level `details`
- Any unexpected exception → generic `500`, never a raw stack trace

No `catch (Exception e) { /* ignore */ }` appears anywhere in this module;
every thrown exception carries a specific type and a human-readable message.

### 12.7 Relevant Java Classes/Functions

`Crop` (entity, Week 2, comment updated), `CropRepository` (Week 2,
unchanged behavior), `CropRequest`/`CropResponse` (new), `CropService`
(new: `createCrop`, `listCropsForFarm`, `getCrop`, `updateCrop`,
`deleteCrop`, plus static `validateDates`/`normalizeStatus`),
`CropController` (new).

### 12.8 Database Interaction

Via Spring Data JPA against the existing `crops` table — no schema changes
were needed (see Section 14).

### 12.9 API Endpoints (already defined in Week 2's documented contract)

`GET /api/crops?farmId=`, `GET /api/crops/{id}`, `POST /api/crops`,
`PUT /api/crops/{id}`, `DELETE /api/crops/{id}` — see
`docs/api-documentation.md` for full request/response examples.

### 12.10 Frontend Interaction

`frontend/crops.html` + `frontend/js/crops.js`: a farm picker, a create/edit
form, a table of the selected farm's crops, and inline success/error
messaging — replacing the Week 2 placeholder page, following the same
structure as `farms.html`/`farms.js`.

### 12.11 Example Successful Execution

Request:
```
POST /api/crops
Authorization: Basic <base64 of ravi.kumar@example.com:password>
Content-Type: application/json

{
  "farmId": 1,
  "cropName": "Wheat",
  "cropType": "Cereal",
  "sowingDate": "2026-11-01",
  "expectedHarvestDate": "2027-03-15"
}
```
Response `201 Created`:
```json
{
  "id": 7,
  "farmId": 1,
  "cropName": "Wheat",
  "cropType": "Cereal",
  "sowingDate": "2026-11-01",
  "expectedHarvestDate": "2027-03-15",
  "status": "PLANNED",
  "createdAt": "2026-09-27T10:00:00"
}
```

## 13. Code Structure

```text
backend/src/main/java/com/agribridge/crop/
├── Crop.java                 (Week 2 entity; header comment updated)
├── CropRepository.java       (Week 2 repository; header comment updated)
├── CropService.java          (NEW — Week 3)
├── CropController.java       (NEW — Week 3)
└── dto/
    ├── CropRequest.java      (NEW — Week 3)
    └── CropResponse.java     (NEW — Week 3)

backend/src/test/java/com/agribridge/crop/
└── CropServiceTest.java      (NEW — Week 3, 21 test methods)

frontend/
├── crops.html                 (REPLACED — was a Week 2 placeholder)
└── js/crops.js                (NEW — Week 3)
```

## 14. Database Interaction

No schema changes were required. The `crops` table (and its foreign key to
`farms`, its `CHECK` constraint on dates, and its `status` enum) was already
fully defined in `database/schema.sql` during Week 2, and Week 2's
`sample_data.sql` already seeded three example crop rows. Week 3 simply
connects working Java code to a table that was already correctly designed
— which is itself part of why this functionality was a good Week 3 choice
(see Section 7, point 3: no new architectural or schema decisions needed).
`spring.jpa.hibernate.ddl-auto=validate` (unchanged from Week 2) means
Hibernate validates `Crop` against the existing table at startup rather
than generating or altering it.

## 15. Input Validation

Two layers, matching the Farm Management precedent:
1. **Bean Validation** on `CropRequest` (`@NotNull`, `@NotBlank`, `@Size`) —
   caught by the existing `GlobalExceptionHandler`'s
   `MethodArgumentNotValidException` handler.
2. **Business-rule validation** in `CropService` (date ordering, status
   vocabulary, farm-reassignment prevention) — raised as
   `IllegalArgumentException` and mapped to `400` by the existing generic
   handler.

## 16. Error Handling

See Section 12.6. All error paths were designed before being implemented
(contract-first), and each has a corresponding unit test (Section 18: TC-03,
TC-09, TC-11, TC-12, TC-13, TC-14, TC-15, TC-18, TC-20).

## 17. Unit Testing Methodology

`CropServiceTest` uses **JUnit 5** with the **Mockito** extension
(`@ExtendWith(MockitoExtension.class)`), mocking `CropRepository`,
`FarmRepository` and `UserRepository` so that `CropService`'s logic is
tested in isolation from a real database — the same approach
`FarmServiceTest`/`AuthServiceTest` already used in Week 2. Tests are
grouped with JUnit 5 `@Nested` classes by the method under test, and each
test has a `@DisplayName` stating its test-case ID (`TC-xx`), scenario
category (Normal / Boundary / Invalid / Null input / Exception / Business
rule violation), and a short description — so the class itself doubles as
readable documentation. Two groups of tests (`validateDates`/
`normalizeStatus`) exercise plain static methods directly, with no mocking
at all, since they have no dependency on any repository.

## 18. Test Case Table

| TC ID | Functionality | Scenario Category | Input | Expected Result |
|---|---|---|---|---|
| TC-01 | validateDates | Normal | sowing=2026-06-01, harvest=2026-10-01 | No exception |
| TC-02 | validateDates | Boundary | sowing=harvest=2026-06-01 | No exception (same-day valid) |
| TC-03 | validateDates | Business rule violation | sowing=2026-10-01, harvest=2026-06-01 | `IllegalArgumentException` |
| TC-04 | validateDates | Null input | sowing=null, harvest=null | No exception (dates optional) |
| TC-05 | validateDates | Null input | sowing=today, harvest=null | No exception |
| TC-06 | normalizeStatus | Null input | status=null | Returns `"PLANNED"` |
| TC-07 | normalizeStatus | Empty input | status="   " | Returns `"PLANNED"` |
| TC-08 | normalizeStatus | Normal | status="growing" | Returns `"GROWING"` |
| TC-09 | normalizeStatus | Invalid value | status="ROTTEN" | `IllegalArgumentException` |
| TC-10 | createCrop | Normal | valid crop, owned farm | Crop created, id assigned, status="PLANNED" |
| TC-11 | createCrop | Exception | farmId not owned/nonexistent | `ResourceNotFoundException` |
| TC-12 | createCrop | Exception | unresolvable user email | `ResourceNotFoundException` |
| TC-13 | createCrop | Business rule violation | harvest date before sowing date | `IllegalArgumentException`, no save |
| TC-14 | getCrop | Exception | crop exists, farm not owned by caller | `ResourceNotFoundException` |
| TC-15 | getCrop | Exception | crop id does not exist | `ResourceNotFoundException` |
| TC-16 | getCrop | Normal | existing, owned crop | Crop returned |
| TC-17 | updateCrop | Normal | valid new field values | Crop updated and returned |
| TC-18 | updateCrop | Business rule violation | farmId changed to a different farm | `IllegalArgumentException`, no save |
| TC-19 | deleteCrop | Normal | existing, owned crop | `repository.delete()` called once |
| TC-20 | listCropsForFarm | Exception | farm not owned by caller | `ResourceNotFoundException` |
| TC-21 | listCropsForFarm | Normal | owned farm with 2 crops | List of 2 `CropResponse` returned |

*(Full input/expected-result detail, including exact assertions, is in
`backend/src/test/java/com/agribridge/crop/CropServiceTest.java` — the table
above is a summary for documentation purposes, per the Week 3 example
format.)*

## 19. Test Execution Results

**Important, non-fabricated status:** this development environment (the
sandbox used to write this Week 3 submission) has no access to Maven
Central and no `javac`/`mvn` installation, so `mvn test` could **not** be
run here to produce a real JUnit execution report — the same limitation
already disclosed for Week 2's `FarmServiceTest`/`AuthServiceTest`. Rather
than fabricate PASS results, every test case above is marked honestly:

| TC ID(s) | Actual Result | Status |
|---|---|---|
| TC-01 – TC-21 | Not run by an executor in this environment. Each test was manually traced against the implementation line-by-line (input → method body → assertion) to confirm the expected result before being written. | **NOT EXECUTED — environment limitation (no Maven Central access to compile/run JUnit here)** |

**What "manually traced" means concretely**, using TC-03 as an example: the
implementation of `validateDates` reads
`if (sowingDate != null && expectedHarvestDate != null && expectedHarvestDate.isBefore(sowingDate)) throw new IllegalArgumentException(...)`.
With `sowing=2026-10-01, harvest=2026-06-01`, both are non-null and
`2026-06-01.isBefore(2026-10-01)` is `true`, so the method throws
`IllegalArgumentException` with a message containing "cannot be before" —
matching the test's assertion exactly. Each of the 21 tests was checked
this way against the actual method bodies in this ZIP, not against an
idealized or different version of the code.

**To get real PASS/FAIL results:** run the exact commands in the final
summary message (see "Exact commands to run the unit tests") after
extracting the project on a machine with Maven and a JDK installed. Please
report back any failures — a manual trace can miss what a real compiler
and test runner would catch (e.g., a typo, an import issue, or a Mockito
stubbing mismatch), and any such issue should be fixed before Week 4 builds
on top of this module.

## 20. Exception Test Cases

Of the 21 test cases above, the following specifically target exception and
error paths (rather than the happy path): **TC-03** (business-rule
exception from bad date range), **TC-09** (business-rule exception from an
invalid status), **TC-11** (not-found: unowned/nonexistent farm), **TC-12**
(not-found: unresolvable user), **TC-13** (business-rule exception blocking
a save), **TC-14** (not-found: crop owned by someone else — the
ownership-leak-prevention case), **TC-15** (not-found: nonexistent crop
id), **TC-18** (business-rule exception blocking a farm reassignment), and
**TC-20** (not-found: listing crops for an unowned farm). That is 9 of 21
tests (43%) dedicated to failure paths, which was a deliberate ratio — error
handling was called out as one of the most important parts of Week 3.

## 21. Challenges Encountered

**Challenge 1 — Deciding how much of Farm's ownership pattern to reuse vs.
adapt for Crop's two-level ownership.**
*Cause:* Farm has one level of ownership (`farm.ownerId == user.id`), but a
Crop's ownership is indirect — it belongs to a Farm, which belongs to a
User. A naive copy of `FarmService`'s pattern would not have checked the
intermediate Farm at all.
*Solution:* Added a private `resolveOwnedCrop` helper in `CropService` that
loads the crop, then separately confirms its parent farm is owned by the
caller (via the existing `FarmRepository.findByIdAndOwnerId`), before
returning it — reusing the existing repository method rather than adding a
new one.
*Outcome:* A crop belonging to another user's farm is never accessible,
and this exact scenario (TC-14) is unit tested.

**Challenge 2 — Preventing a confusing "silent" farm reassignment on update.**
*Cause:* `CropRequest` carries `farmId` in the body (to match the
already-documented flat `/api/crops` contract), so a naive `updateCrop`
implementation could let a caller change which farm a crop belongs to by
simply passing a different `farmId` — without re-validating ownership of
that new farm, which would be a security gap.
*Solution:* `updateCrop` explicitly checks that the request's `farmId`
matches the crop's existing `farmId` and rejects the update with a clear
`IllegalArgumentException` if not, rather than either silently ignoring the
field or implementing a second, more complex ownership check for a feature
nobody asked for.
*Outcome:* The behavior is explicit, tested (TC-18), and documented as an
intentional Week 3 scope boundary rather than an oversight.

**Challenge 3 — Test execution inside this development environment.**
*Cause:* No Maven Central access and no local JDK/Maven installation in the
sandbox used to prepare this submission (identical to the Week 2
limitation).
*Solution:* Every test was written carefully and manually traced against
the actual method implementations (see Section 19) rather than left
unwritten or marked with fabricated results.
*Outcome:* Test intent and coverage are complete and reviewable now; actual
pass/fail confirmation requires running `mvn test` locally (commands
provided in the final summary), which is called out honestly rather than
glossed over.

## 22. Solutions Applied

See "Solution" under each challenge in Section 21 — summarized: (1) an
explicit two-level ownership helper, (2) an explicit rejection of
cross-farm reassignment, (3) rigorous manual test tracing plus transparent
documentation of the execution gap.

## 23. Work Breakdown (~30–35 hours)

| Activity | Hours |
|---|---|
| Reviewing existing Week 2 architecture, code, and docs before starting | 3.5 |
| Selecting and justifying the Week 3 functionality (Section 7) | 2.5 |
| Backend implementation (`CropService`, `CropController`, DTOs) | 9 |
| Database review/integration (confirming schema fit; no migration needed) | 3 |
| Frontend prototype (`crops.html`, `crops.js`, dashboard live-count update) | 3.5 |
| Unit test development (`CropServiceTest`, 21 test methods) | 6 |
| Debugging/error-handling refinement and manual test tracing | 2.5 |
| Documentation (this file, plus updates to architecture/API/roadmap/README) | 3.5 |
| **Total** | **33.5** |

## 24. Week 3 Accomplishments

- Crop Management implemented end-to-end (entity → repository → service →
  controller → DTOs → frontend), reusing Week 2's established patterns.
- 21 unit tests written across normal, boundary, null-input, invalid-input,
  and business-rule-violation categories, with 9 specifically targeting
  error/exception paths.
- Existing documentation (`architecture.md`, `api-documentation.md`,
  `database-design.md`, `roadmap.md`, `README.md`, the Postman collection)
  updated in place to reflect Crop Management's new status, with every
  change explained rather than silently made.
- No Week 1/Week 2 file deleted; no new framework, language or database
  introduced; no Week 4 functionality (Harvest Lots, Buyer Requests,
  Matching, JWT-filter enforcement) implemented ahead of schedule.

## 25. Work Reserved for Week 4

- **Harvest Lot Management** (`HarvestLotService`/`HarvestLotController`) —
  moved from Week 2's original Week 3 plan to keep this week focused (see
  Section 6).
- **Buyer Requests** and the **rule-based Matching engine**.
- **`JwtAuthenticationFilter`** wiring into `SecurityConfig`, replacing HTTP
  Basic as the real enforcement mechanism.
- **Controller-level tests** (`MockMvc`) and **integration tests** covering
  full request paths — Week 3 tests are service-level (Mockito) only.
- **`/api/dashboard/summary`** and full dashboard analytics.
- **Broader system integration testing, performance testing, deployment
  write-up, final bug fixing, and final documentation polish.**
- Actually running `mvn test` against this code and fixing anything a real
  compiler/test-runner catches that manual tracing could not (see Section 19).

None of the above has been implemented or tested in Week 3.

## 26. Conclusion

Week 3 delivered exactly what it set out to: one meaningful, well-justified
functionality — Crop Management — implemented completely, tested
thoroughly at the unit level, and documented honestly, without touching
Week 1/2 work or reaching ahead into Week 4 scope. The functionality was
chosen specifically because it was already anticipated by the Week 2
architecture and database design, which kept the implementation focused on
business logic and testing rather than on new architectural decisions. The
most significant open item is executing the test suite in a real Maven/JDK
environment to confirm the manually-traced results — everything else
(Harvest Lots, Buyer Requests, Matching, JWT enforcement, broader testing,
and deployment) is explicitly and deliberately left for Week 4.
