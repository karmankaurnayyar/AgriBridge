# AgriBridge — Performance Evaluation (Week 4)

## Status: No measurements were taken. All results below are explicitly PENDING.

Running the AgriBridge backend requires a live MySQL connection and a JVM
capable of executing the compiled Spring Boot application. Neither is
available in the environment used to prepare this Week 4 package (the same
environment that could not run `mvnw.cmd test` — see `DEBUGGING_LOG.md`).
Per the project's standing instruction never to present hypothetical values
as measured results, **no benchmark numbers are reported**. Instead, this
document specifies exactly how to obtain real measurements once the
application is running locally (which the developer has already
demonstrated is possible, per the Week 3 baseline).

## 1. Environment to Record (fill in when measuring)

| Field | Value |
|---|---|
| Java version | *(record `java -version` output)* |
| Spring Boot version | 3.3.4 (from `pom.xml`) |
| MySQL version | *(record `SELECT VERSION();` output)* |
| Machine | *(CPU, RAM, OS — e.g., Windows 11, local developer machine)* |
| Data volume | *(number of users/farms/crops seeded for the test)* |
| Date of measurement | *(fill in)* |

## 2. Representative Operations to Measure

| Operation | Endpoint | Why It's Representative |
|---|---|---|
| User login | `POST /api/auth/login` | Involves a BCrypt hash comparison, the most CPU-intensive single step in the current codebase. |
| Farm creation | `POST /api/farms` | A simple single-table insert with validation. |
| Crop creation | `POST /api/crops` | Two repository reads (user, farm ownership) plus validation plus one insert — the most "chatty" write operation currently implemented. |
| Crop listing for a farm | `GET /api/crops?farmId=` | A read scaling with the number of crops on one farm; relevant to the pagination recommendation (R-04 in `BUG_REGISTER.md`). |

## 3. Measurement Method (reproducible procedure)

### 3.1 Simple single-request timing (no extra tooling required)

From PowerShell, after starting the backend (`mvnw.cmd spring-boot:run`)
and obtaining a valid Basic-Auth credential pair:

```powershell
Measure-Command {
  curl.exe -u "ravi.kumar@example.com:Password123" `
    -X POST http://localhost:8080/api/farms `
    -H "Content-Type: application/json" `
    -d '{"farmName":"Perf Test Farm","location":"Test","landArea":1.0}'
}
```

Repeat 10–20 times after a few warm-up calls (the JVM's JIT compiler needs a
short warm-up period before steady-state timing is meaningful) and record
the mean and range of the reported `TotalMilliseconds`.

### 3.2 Load-style measurement (optional, if a tool is installed)

Any standard HTTP load-testing tool can be pointed at the running backend,
for example Apache Bench (if installed):

```powershell
ab -n 100 -c 10 -A ravi.kumar@example.com:Password123 http://localhost:8080/api/farms
```

Record requests/second, mean latency, and the 95th-percentile latency it
reports.

### 3.3 Query-count sanity check

Set `JPA_SHOW_SQL=true` (already supported by `application-dev.properties.example`)
before a measurement run, and manually count the SQL statements logged for
a single `POST /api/crops` call — this directly validates whether the
"unnecessary repeated queries" concern in the Week 4 task brief applies. By
code inspection (not yet confirmed against logged output), `createCrop()`
is expected to issue: one `SELECT` for the user, one `SELECT` for the farm
ownership check, and one `INSERT` for the crop — three statements, which is
reasonable for the validation this endpoint performs.

## 4. Results Table (template — to be completed by running the above)

| Operation | Test Conditions | Measurement Method | Observed Result | Interpretation |
|---|---|---|---|---|
| `POST /api/auth/login` | *(e.g., 1 user, warm JVM)* | §3.1 `Measure-Command` | **PENDING** | — |
| `POST /api/farms` | *(fill in)* | §3.1 | **PENDING** | — |
| `POST /api/crops` | *(fill in)* | §3.1 | **PENDING** | — |
| `GET /api/crops?farmId=` with N crops | *(vary N: 10, 100, 1000)* | §3.1 or §3.2 | **PENDING** | — |

## 5. Other Performance Considerations (code-review-based, not measured)

- **Database indexing**: `database/schema.sql` already defines indexes on
  the foreign-key/lookup columns used by the current ownership-check
  queries (`idx_farms_owner_id`, `idx_crops_farm_id`,
  `idx_harvest_lots_crop_id`, `idx_harvest_lots_status`,
  `idx_buyer_requests_buyer_id`, `idx_buyer_requests_status`,
  `idx_matches_status`). By inspection, no obvious missing index was found
  for the queries the current (Farm/Crop/Auth) services issue.
- **Repeated queries**: `CropService`'s two-level ownership check
  (`resolveOwnedCrop`) issues two separate repository calls
  (`findById` then `findByIdAndOwnerId`) rather than one combined query.
  This is a reasonable, deliberate trade-off for code clarity at current
  scale (see `docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md`,
  "Challenge 1"); it is not flagged as a defect, but a single joined query
  would reduce round-trips if this path became a measured bottleneck.
- **Pagination**: `GET /api/farms` and `GET /api/crops` return an
  unbounded result set (see R-04 in `BUG_REGISTER.md`). This has no
  measurable effect at prototype data volumes but is the most likely
  future bottleneck as real usage grows.
- **Memory/resource considerations**: the application has no caching layer
  and no connection-pool tuning beyond Spring Boot's defaults (HikariCP
  defaults). Reasonable for a prototype; worth revisiting only if a real
  measured bottleneck is found.
- **Concurrency**: no explicit concurrency testing was performed. Spring's
  default request-per-thread model and HikariCP's default pool size are
  unmodified from Spring Boot's defaults; no concurrency-specific code
  (e.g., manual synchronization) exists in the current services, so no
  concurrency-specific risk was identified by code review.

## 6. Conclusion

No performance claims are made in this document beyond what code review can
support. All numeric results are pending a real measurement run, which the
developer is positioned to perform given the Week 3 baseline already
confirmed a working local build. The procedure above is intended to be
followed exactly as written and the results table filled in afterward.
