# AgriBridge — Testing and Regression Report (Week 4)

## 1. Week 3 Baseline (reported, not re-executed from scratch here)

As recorded in the project's own Week 3 documentation
(`docs/week3/WEEK3_CODE_IMPLEMENTATION_AND_UNIT_TESTING.md`) and the Week 3
report:

```
Tests run: 27
Failures:  0
Errors:    0
Skipped:   0
BUILD SUCCESS
```

Breakdown: `AuthServiceTest` (2 tests), `FarmServiceTest` (4 tests),
`CropServiceTest` (21 tests). This is treated as the established baseline
for Week 4 — it is **not** re-claimed as freshly executed in this
environment; see Section 4 below.

## 2. Week 4 — New/Modified Tests

| Test Class | New Tests | Purpose |
|---|---|---|
| `GlobalExceptionHandlerTest` | 7 (`WK4-EXC-01` – `WK4-EXC-07`) | Verifies every `GlobalExceptionHandler` method, including the new `AccessDeniedException` → 403 handler added to fix BUG-001. |

No existing Week 3 test file was modified. `GlobalExceptionHandler.java`
itself was modified (one handler method added); no other production file
was changed.

| Test ID | Scenario | Expected Result |
|---|---|---|
| WK4-EXC-01 | `ResourceNotFoundException` → handler | 404, error=NOT_FOUND |
| WK4-EXC-02 | `DuplicateResourceException` → handler | 409, error=CONFLICT |
| WK4-EXC-03 | `BadCredentialsException` → handler | 401, generic message |
| WK4-EXC-04 | `IllegalArgumentException` → handler | 400, error=BAD_REQUEST |
| WK4-EXC-05 | `AccessDeniedException` → handler (BUG-001 fix) | 403, error=ACCESS_DENIED, not the 500 generic message |
| WK4-EXC-06 | Unrecognized `RuntimeException` → generic handler | 500, message does not leak internal detail |
| WK4-EXC-07 | Any handler call | response body's `timestamp` is non-null |

## 3. Projected Combined Result

If the Week 3 baseline (27) and the Week 4 additions (7) both pass, the
project-wide total would be **34 tests, 34 passed, 0 failures, 0 errors, 0
skipped**. This figure is a **projection based on independent manual
tracing of each test against its implementation**, not a claim that this
exact combined run was executed — see Section 4.

## 4. Actual Execution Status in This Environment

**Not executed here.** The environment used to prepare this Week 4 package
has no Maven Central access and cannot compile or run this Spring Boot
project (the same limitation disclosed in the Week 2 and Week 3
documentation). Per the project's standing instruction never to fabricate a
build/test result, no "BUILD SUCCESS" is claimed for a run that did not
happen in this environment.

**What was done instead:** every new test was written against the actual,
current method signatures and behavior of `GlobalExceptionHandler` (both
before and after the fix), and each assertion was manually traced against
the fixed method body line-by-line — the same rigor applied in the Week 3
test documentation for `CropServiceTest`.

**To obtain the real, authoritative result:** run the following on a
machine with JDK 21 and this repository checked out (the same setup already
confirmed working for the Week 3 baseline):

```powershell
cd backend
.\mvnw.cmd clean test
```

Expected location of the surefire report after running:
`backend\target\surefire-reports\`. Please record the actual console output
and report back if the combined total differs from the 34 projected above —
a manual trace can miss what a real compiler and test runner catches (an
import typo, a Mockito/AssertJ API mismatch, etc.).

## 5. Regression Testing Approach

Because the Week 4 change is additive (one new `@ExceptionHandler` method;
no existing method body was altered), the regression risk is low and was
addressed as follows:

- **Unchanged-behavior tests**: `WK4-EXC-01`–`04`, `06`, `07` each
  independently re-assert that every *pre-existing* handler still returns
  its original status code and message shape, so any accidental side effect
  of the edit (e.g., a misplaced annotation affecting handler ordering)
  would be caught.
- **No interaction with Week 3 tests**: `AuthServiceTest`, `FarmServiceTest`
  and `CropServiceTest` test service-layer classes directly against mocked
  repositories; none of them instantiate or exercise `GlobalExceptionHandler`,
  so the Week 4 change cannot affect their outcome.
- **Scope check**: `git diff`-equivalent review confirms the only production
  file touched is `GlobalExceptionHandler.java`, with one method added and
  no existing method body edited.

## 6. Controller/Integration-Level Testing — Not Added This Week

`BUG_REGISTER.md` (BUG-001) concerns `@PreAuthorize` behavior, which is only
actually enforced inside a live Spring Security filter chain — a true
end-to-end reproduction would require a `@SpringBootTest` or `@WebMvcTest`
with a mocked `UserDetailsService`/security context. This was deliberately
**not** added in Week 4: building a reliable Spring context test without a
live MySQL instance requires additional configuration (an in-memory or test
database, or further mocking of the persistence layer) that was judged out
of scope for a single, already-well-isolated bug fix, consistent with the
instruction to avoid adding complexity merely to make the project look
larger. The direct unit test on `GlobalExceptionHandler` verifies the
handler's own logic conclusively; a full controller-level test remains a
reasonable Week 5-and-beyond recommendation (see `FUTURE_ENHANCEMENTS.md`).
