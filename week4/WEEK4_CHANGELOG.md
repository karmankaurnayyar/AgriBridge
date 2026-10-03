# AgriBridge — Week 4 Changelog

## What Was Inspected

The live repository at `https://github.com/karmankaurnayyar/AgriBridge`
(default branch, commit `a4255cb` — "Complete Week 3 code implementation and
unit testing") was cloned and inspected directly. Files read in full
include:

- `backend/pom.xml`
- `backend/src/main/java/com/agribridge/**/*.java` (all entities,
  repositories, services, controllers, DTOs, security and config classes)
- `backend/src/test/java/com/agribridge/**/*.java` (all three Week 3 test
  classes — confirmed 2 + 4 + 21 = 27 `@Test` methods, matching the stated
  baseline)
- `backend/MAVEN_WRAPPER_README.md`, `backend/.mvn/wrapper/maven-wrapper.properties`
- `database/schema.sql`
- `docs/architecture.md`, `docs/api-documentation.md`, `docs/roadmap.md`
- Root `README.md`

This inspection is what the Bug Register, Debugging Log, and Performance
Evaluation documents are based on — not assumptions.

## What Was Changed

| File | Type of Change | Reason |
|---|---|---|
| `backend/src/main/java/com/agribridge/exception/GlobalExceptionHandler.java` | Modified — one method added (`handleAccessDenied`), one import added (`org.springframework.security.access.AccessDeniedException`) | Fixes BUG-001: role-based access denial was incorrectly returning HTTP 500 instead of HTTP 403. No existing method was altered. |
| `backend/src/test/java/com/agribridge/exception/GlobalExceptionHandlerTest.java` | New file | Adds 7 dependency-free unit tests covering every `GlobalExceptionHandler` method, including direct verification of the BUG-001 fix. |

No other source file was modified. No entity, repository, DTO, frontend
file, or database script was changed. No dependency version in `pom.xml`
was changed (the existing Lombok/Maven-Wrapper configuration from the
repository's own prior fix was left exactly as found, since it is unrelated
to BUG-001 and already functional per the Week 3 baseline).

## Why Each Change Was Needed

See `BUG_REGISTER.md` (BUG-001) and `DEBUGGING_LOG.md` (Entry 2) for the
full root-cause analysis. In short: `@PreAuthorize("hasAnyRole('FARMER','ADMIN')")`
on `FarmController`/`CropController` write methods throws
`AccessDeniedException` on a role mismatch, and `GlobalExceptionHandler` had
no specific handler for that exception type, so it fell through to the
generic 500 handler instead of returning the documented 403.

## How It Was Verified

By direct, careful manual tracing of the new test assertions against the
fixed method implementation (the same standard of rigor used in the Week 3
test documentation), since this environment cannot execute `mvnw.cmd test`
(no Maven Central access — see `DEBUGGING_LOG.md`, "Environment
Limitation"). The developer should run `.\mvnw.cmd clean test` from
`backend/` on their own machine (where the Week 3 baseline was already
confirmed working) to obtain the authoritative result — see
`TESTING_AND_REGRESSION_REPORT.md` for the exact command and what to check.

## Files Added to This Week 4 Package (not part of the existing repository)

```
AgriBridge_Week_4/
├── README_WEEK4.md
├── WEEK4_CHANGELOG.md                 (this file)
├── BUG_REGISTER.md
├── DEBUGGING_LOG.md
├── TESTING_AND_REGRESSION_REPORT.md
├── PERFORMANCE_EVALUATION.md
├── FUTURE_ENHANCEMENTS.md
├── FINAL_SUBMISSION_CHECKLIST.md
├── report/AgriBridge_Week_4_Evaluation_Debugging_Final_Report.docx
├── docs/diagrams/
│   ├── evaluation-workflow.md
│   ├── bug-lifecycle.md
│   └── testing-regression-workflow.md
└── src/
    ├── main/java/com/agribridge/exception/GlobalExceptionHandler.java   (replaces existing file)
    └── test/java/com/agribridge/exception/GlobalExceptionHandlerTest.java (new file)
```

See `README_WEEK4.md` for exactly where each `src/` file goes in the
existing repository and how to merge this package in without overwriting
Weeks 1–3 work.
