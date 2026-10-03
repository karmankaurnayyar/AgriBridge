# AgriBridge — Debugging Log (Week 4)

Chronological record of issues investigated during Week 4, in the order
they were worked through. Each entry includes what was observed, how it was
investigated, the root cause, what was changed, how the change was checked,
and its final status.

---

## Entry 1 (carried forward from Week 3) — JDK 24 / Byte Buddy test failures

**Problem:** During Week 3, running `mvnw.cmd test` on a machine whose active
JDK was 24.0.1 (while the project targets Java 21) produced intermittent
failures in authentication-related tests.

**Symptoms:** Test failures that did not point to a logic error in
`AuthService` itself, but to mock/proxy class generation problems during
test execution.

**Investigation Steps:**
1. Confirmed the project's own compiler configuration targets Java 21
   (`pom.xml`), while `java -version` on the development machine reported
   JDK 24.0.1.
2. Identified that Mockito's bundled Byte Buddy version — responsible for
   generating proxy/mock classes at test time — was not yet fully compatible
   with JDK 24's class file format at the time.
3. Confirmed separately that Lombok's annotation processor (which generates
   getters/setters/builders at compile time) has the same category of
   JDK-version sensitivity, since it also relies on compiler-internal APIs
   that change between JDK feature releases.

**Root Cause:** A mismatch between the JDK used to run the build/tests
(24.0.1) and the JDK the project is designed and declared to target (21),
affecting two different tools (Byte Buddy inside Mockito, and Lombok's
annotation processor) for related but distinct reasons.

**Changes Made:**
- JDK 21 LTS was installed and set as the active JDK for the Maven build.
- `pom.xml` explicitly pins the Lombok version (`1.18.38` in the current
  repository) and configures `annotationProcessorPaths` explicitly in the
  `maven-compiler-plugin` so the compiler and the annotation processor agree
  on exactly which Lombok version is in use, rather than leaving it to
  whichever version happened to resolve.
- A Maven Wrapper (`mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`
  pointing at Apache Maven 3.9.16) was added so the project builds
  consistently regardless of any globally installed Maven version.

**Test Used:** The full Week 3 suite (`AuthServiceTest`, `FarmServiceTest`,
`CropServiceTest` — 27 tests).

**Final Status:** Resolved. Documented in `docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md`
(Section 17/21) and in the Week 3 report. This entry is repeated here only
for continuity, consistent with the available Week 3 project records; it
was not re-investigated from scratch in Week 4.

---

## Entry 2 (Week 4) — Role-based access denial returns HTTP 500

**Problem:** While systematically evaluating the authorization behavior of
`FarmController` and `CropController` (Section 5 of the Week 4 task:
"Authentication failures and unauthorized access"), the write endpoints on
both controllers were found to be annotated:

```java
@PreAuthorize("hasAnyRole('FARMER','ADMIN')")
```

This raised the question of what response an authenticated `BUYER` would
actually receive when calling one of these endpoints, since `docs/api-documentation.md`
documents `403` as the expected outcome for "authenticated but wrong role."

**Symptoms (identified by code inspection, not by running the live
application — no MySQL instance was available in the environment used to
prepare this package):** `GlobalExceptionHandler.java` was inspected line by
line. It defines `@ExceptionHandler` methods for:

- `ResourceNotFoundException` → 404
- `DuplicateResourceException` → 409
- `BadCredentialsException` → 401
- `MethodArgumentNotValidException` → 400
- `IllegalArgumentException` → 400
- `Exception` (catch-all) → 500

No handler exists for `org.springframework.security.access.AccessDeniedException`
— the specific exception type Spring Security's method-security interceptor
throws when a `@PreAuthorize` check fails.

**Investigation Steps:**
1. Confirmed, from Spring's documented behavior, that `@PreAuthorize` is
   enforced via an AOP interceptor wrapping the controller method
   invocation, and that an `AccessDeniedException` thrown there propagates
   through Spring MVC's normal dispatch path — meaning it is visible to
   `@RestControllerAdvice`/`@ExceptionHandler` resolution exactly like any
   other exception thrown inside a controller method.
2. Confirmed `AccessDeniedException` is a `RuntimeException` that does not
   extend, and is not caught by, any of the five specifically-handled
   exception types above.
3. Traced the fallback path: with no specific handler, `AccessDeniedException`
   matches only `handleGeneric(Exception ex)`, which unconditionally returns
   `HttpStatus.INTERNAL_SERVER_ERROR` (500).
4. Cross-checked this conclusion against `docs/api-documentation.md`'s own
   documented contract ("403 — authenticated but wrong role") to confirm the
   observed code path contradicts the project's own intended behavior.

**Root Cause:** A missing `@ExceptionHandler(AccessDeniedException.class)`
in `GlobalExceptionHandler` — logged as **BUG-001** in `BUG_REGISTER.md`.

**Changes Made:** Added a dedicated handler mapping `AccessDeniedException`
to `HttpStatus.FORBIDDEN` with error code `ACCESS_DENIED`, placed before the
generic fallback so it is matched first. See the full diff context in
`WEEK4_CHANGELOG.md`.

**Test Used:** New test `GlobalExceptionHandlerTest.handleAccessDenied_returns403`
(`WK4-EXC-05`), plus six sibling tests (`WK4-EXC-01` through `WK4-EXC-04`,
`06`, `07`) re-confirming every other handler's behavior is unchanged by the
edit. All seven tests call `GlobalExceptionHandler`'s methods directly
(no Spring context, no mocks, since the class has no constructor
dependencies), making them fast and dependency-free to run.

**Final Status:** Fixed in code and covered by a new, dependency-free unit
test. **Not independently executed by a live test runner** in the
environment used to prepare this Week 4 package (see "Environment
Limitation" below) — the fix and test were verified by careful manual
tracing against the actual method implementations, consistent with the
honesty standard already established in the Week 3 documentation. The exact
command to confirm this on a machine with Maven and JDK 21 is given in
`TESTING_AND_REGRESSION_REPORT.md`.

---

## Environment Limitation (applies to this entire Week 4 package)

The environment used to prepare this Week 4 submission has no access to
Maven Central, no local JDK/Maven installation capable of running this
project's build, and no live MySQL instance. This is the same limitation
already disclosed in the Week 2 and Week 3 documentation for this project.
Consequently:

- The Week 3 baseline (27 tests, 27 passed, 0 failures, 0 errors, 0
  skipped, BUILD SUCCESS) is treated as a **reported baseline** from the
  project's own records and prior documentation, not as something
  re-executed from scratch in this environment.
- The new Week 4 test (`GlobalExceptionHandlerTest`, 7 tests) was written
  and manually traced against the fixed implementation, but **not** run by
  an actual JUnit test runner here.
- No new performance measurements were taken, since running the
  application requires a live MySQL connection that is not available in
  this environment (see `PERFORMANCE_EVALUATION.md` for the reproducible
  procedure to use instead).

This is stated plainly, per the project's standing instruction never to
fabricate a successful execution. The developer should run the commands in
`TESTING_AND_REGRESSION_REPORT.md` locally (where Maven, JDK 21 and MySQL
are available, as already confirmed working for the Week 3 baseline) and
treat that run as the authoritative Week 4 result.
