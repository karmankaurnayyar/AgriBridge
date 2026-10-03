# AgriBridge — Bug Register (Week 4)

This register distinguishes **confirmed, code-verified defects** from
**potential issues and recommendations** that were not reproduced as
functional bugs. Nothing in this file is invented: the confirmed bug below
was found by direct inspection of the actual source files in
`https://github.com/karmankaurnayyar/AgriBridge` (commit `a4255cb`, "Complete
Week 3 code implementation and unit testing"), and verified against Spring
Framework's documented exception-resolution behavior. See
`DEBUGGING_LOG.md` for the full investigation narrative.

---

## Confirmed Bugs

### BUG-001 — Role-based access denial returns HTTP 500 instead of HTTP 403

| Field | Detail |
|---|---|
| **Bug ID / Title** | BUG-001 — `AccessDeniedException` not handled; falls through to generic 500 |
| **Affected Module / File** | `exception/GlobalExceptionHandler.java` (cross-cutting; manifests through `farm/FarmController.java` and `crop/CropController.java`, both of which use `@PreAuthorize("hasAnyRole('FARMER','ADMIN')")` on write endpoints) |
| **Severity** | Medium |
| **Priority** | High (incorrect HTTP status on a security-relevant path; easy, low-risk fix) |
| **Preconditions** | A user is authenticated (valid credentials) but holds a role not permitted for the endpoint — e.g., a `BUYER` calling a Farm/Crop write endpoint. |
| **Reproduction Steps** | 1. Register/authenticate as a user with role `BUYER`.<br>2. Send `POST /api/farms` (or `POST /api/crops`) with a valid, well-formed body and valid HTTP Basic credentials for that `BUYER` user.<br>3. Observe the response. |
| **Expected Result** | `HTTP 403 Forbidden` with the standard `ApiErrorResponse` JSON shape (per `docs/api-documentation.md`, which documents 403 as "authenticated but wrong role"). |
| **Actual Result (before fix)** | `HTTP 500 Internal Server Error` with the generic `"Something went wrong. Please try again later."` body, because `AccessDeniedException` (thrown by Spring Security's method-security interceptor when `@PreAuthorize` fails) has no dedicated `@ExceptionHandler` in `GlobalExceptionHandler` and falls through to the catch-all `handleGeneric(Exception ex)` handler, which maps every unrecognized exception to 500. |
| **Root Cause** | `GlobalExceptionHandler` defines specific handlers for `ResourceNotFoundException`, `DuplicateResourceException`, `BadCredentialsException`, `MethodArgumentNotValidException`, and `IllegalArgumentException`, but was missing one for `org.springframework.security.access.AccessDeniedException`. Spring MVC's `ExceptionHandlerExceptionResolver` resolves exceptions in the order of most-specific match; with no specific match, `AccessDeniedException` (a `RuntimeException`) matched only the generic `Exception.class` handler. |
| **Code-Level Fix** | Added `@ExceptionHandler(AccessDeniedException.class)` → `handleAccessDenied()`, returning `HttpStatus.FORBIDDEN` with error code `ACCESS_DENIED`, placed alongside the other specific handlers and before the generic fallback. See `src/main/java/com/agribridge/exception/GlobalExceptionHandler.java` in this package. |
| **Test Case Used to Verify** | `GlobalExceptionHandlerTest.handleAccessDenied_returns403` (`WK4-EXC-05`) — directly instantiates `GlobalExceptionHandler` (it has no constructor dependencies) and asserts that calling the new handler with an `AccessDeniedException` returns status 403 and an error body that does not contain the generic 500 message. |
| **Result After Fix** | The handler method, called directly, returns `403 FORBIDDEN` with `error = "ACCESS_DENIED"`. This was verified by direct code review and by tracing the test's assertions against the fixed method body; it was **not** executed by a live test runner in the environment used to prepare this package — see `TESTING_AND_REGRESSION_REPORT.md` for the exact command to confirm this on a machine with Maven and JDK 21. |
| **Regression-Testing Result** | The fix only adds a new, more-specific `@ExceptionHandler`; it does not modify any existing handler method, the `build()` helper, or any other class. The existing 6 `GlobalExceptionHandler`-relevant behaviors (404, 409, 401, 400 ×2, generic 500) are unchanged and are each independently re-asserted by `WK4-EXC-01` through `WK4-EXC-04` and `WK4-EXC-06`/`07` in the same test class, so no regression is expected in existing error-handling behavior. The 27 Week 3 tests (`AuthServiceTest`, `FarmServiceTest`, `CropServiceTest`) do not touch `GlobalExceptionHandler` at all (they test service-layer logic directly against mocked repositories, not the HTTP/exception layer), so this fix carries no risk of affecting them. |

---

## Potential Issues, Risks and Recommendations

These were identified through code review but are **not** confirmed functional
defects — the application behaves as currently designed in each case. They
are documented as hardening opportunities, consistent with the instruction
not to present risks as bugs.

| ID | Area | Observation | Why It Matters | Suggested Priority |
|---|---|---|---|---|
| R-01 | `config/CorsConfig.java` | CORS is configured with `allowedOriginPatterns("*")` and `allowCredentials(true)`, permitting any origin to send credentialed requests. | Appropriate for local development only; before any real deployment, this should be restricted to the actual deployed frontend origin(s). Already flagged in `docs/architecture.md` from Week 2. | Medium (High before any deployment) |
| R-02 | `auth/AuthService.java` | `login()` calls `authenticationManager.authenticate()` unconditionally; a nonexistent email returns faster than a wrong password for an existing email (which pays the BCrypt hashing cost), creating a small response-time difference that could theoretically support email enumeration. | Minor security-hardening opportunity; does not affect functional correctness, and the error message is already uniform ("Email or password is incorrect.") regardless of which part was wrong. | Low |
| R-03 | `security/`, `config/SecurityConfig.java` | JWT is issued at login/registration (`JwtUtil`) but is not yet the mechanism that actually protects endpoints — HTTP Basic is. | Already documented as a known limitation since Week 2/3 (`docs/architecture.md`, root `README.md`); carried forward, not new. Planned for Week 4/beyond per `docs/roadmap.md`. | Documented — tracked, not new |
| R-04 | `farm/FarmController.java`, `crop/CropController.java` | `GET` list endpoints (`/api/farms`, `/api/crops`) return the caller's entire result set with no pagination. | Fine at current prototype data volumes; would need pagination (e.g., Spring Data `Pageable`) before supporting a large number of farms/crops per user. | Medium (as data grows) |
| R-05 | `backend/pom.xml` | The Maven Compiler Plugin is configured with `<source>21</source>`/`<target>21</target>` rather than the single `<release>21</release>` flag. | Functionally correct for the current codebase, but `<release>` additionally restricts compiled code from referencing any JDK-22+-only standard library API, which `source`/`target` alone do not enforce. Low-risk, optional hardening. | Low |
| R-06 | `frontend/js/api.js` | Basic-Auth credentials are stored (base64-encoded) in `localStorage` for the session. | Already documented as a known, intentional Week 2/3 simplification for local development; not production-safe. Carried forward, not new. | Documented — tracked, not new |

**Summary:** One confirmed, fixed, and newly-tested defect (BUG-001). No other functional defects were reproduced during this evaluation. Six items are logged as risks/recommendations for future hardening, three of which were already known from Weeks 2–3 and are repeated here only for completeness.
