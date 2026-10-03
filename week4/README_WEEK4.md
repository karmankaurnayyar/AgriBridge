# AgriBridge — Week 4: Evaluation, Debugging, Testing and Final Documentation

**Project:** AgriBridge — Agribusiness Operations and Market Intelligence Platform
**Developer:** Karman Kaur Nayyar (Individual Project)
**Repository:** https://github.com/karmankaurnayyar/AgriBridge
**Week:** 4 of 4 (final)

## Scope

Week 4 evaluates the Week 3 prototype (Authentication, Farm Management, and
Crop Management), confirms one real defect through code-level investigation
and fixes it, documents several risks/recommendations that are **not**
confirmed defects, provides a reproducible performance-testing procedure
(no fabricated benchmark numbers), and produces the final stakeholder
report. It does **not** implement Harvest Lot Management, Buyer Requests,
Matching, full JWT enforcement, or deployment — those remain future work
(see `FUTURE_ENHANCEMENTS.md`).

## What's In This Package

| File | Purpose |
|---|---|
| `BUG_REGISTER.md` | 1 confirmed, fixed defect (BUG-001) + 6 risk/recommendation items |
| `DEBUGGING_LOG.md` | Investigation narrative for BUG-001, plus the Week 3 JDK24/Byte Buddy issue for continuity |
| `TESTING_AND_REGRESSION_REPORT.md` | Week 3 baseline (27 tests), Week 4 additions (7 tests), regression approach, and the real command to run them |
| `PERFORMANCE_EVALUATION.md` | Methodology and a reproducible measurement procedure — all results explicitly marked PENDING |
| `FUTURE_ENHANCEMENTS.md` | Prioritized roadmap (High/Medium/Long-term) |
| `WEEK4_CHANGELOG.md` | Exactly what was inspected, changed, why, and how it was verified |
| `FINAL_SUBMISSION_CHECKLIST.md` | Pre-submission verification checklist |
| `report/AgriBridge_Week_4_Evaluation_Debugging_Final_Report.docx` | The polished, submission-ready stakeholder report |
| `docs/diagrams/` | Evaluation workflow, bug lifecycle, and testing/regression workflow diagrams (Mermaid, render natively on GitHub) |
| `src/` | The two changed/added Java files (see below) — not a full project copy |

## Important Honesty Note

This package was prepared in an environment with no Maven Central access,
no local JDK/Maven able to build this project, and no live MySQL instance
— the same limitation already documented for Weeks 2 and 3. The Week 3
baseline (27/27 passing) is reported from the project's own existing
records, not re-executed here. The one new Week 4 test file was written and
carefully traced against the fixed code, but not run by an actual test
tool in this environment. **Please run the commands below yourself** and
treat that output as the authoritative result — see
`TESTING_AND_REGRESSION_REPORT.md` for full detail.

## Integrating This Package into Your Existing Repository (Windows + VS Code)

1. **Download and extract** this Week 4 ZIP to a convenient location (e.g.
   `Downloads\AgriBridge_Week_4`).

2. **Back up your existing repository** before making any changes:
   ```powershell
   cd C:\path\to\your\AgriBridge
   git status
   git checkout -b backup/pre-week4
   git checkout main
   ```
   (This creates a safety branch; your `main` branch is untouched by this
   step.)

3. **Copy the two changed Java files** into your existing project, replacing/
   adding them at these exact paths:
   - `AgriBridge_Week_4\src\main\java\com\agribridge\exception\GlobalExceptionHandler.java`
     → `backend\src\main\java\com\agribridge\exception\GlobalExceptionHandler.java`
     (**replaces** the existing file — it is the same file with one method
     added; nothing else is different)
   - `AgriBridge_Week_4\src\test\java\com\agribridge\exception\GlobalExceptionHandlerTest.java`
     → `backend\src\test\java\com\agribridge\exception\GlobalExceptionHandlerTest.java`
     (**new** file)

4. **Copy the Week 4 documentation** into your repository root (or into a
   `docs/week4/` subfolder, matching the `docs/week1`/`docs/week2`/`docs/week3`
   convention already used by the project, if you prefer — either location
   is fine, just be consistent):
   - `README_WEEK4.md`, `WEEK4_CHANGELOG.md`, `BUG_REGISTER.md`,
     `DEBUGGING_LOG.md`, `TESTING_AND_REGRESSION_REPORT.md`,
     `PERFORMANCE_EVALUATION.md`, `FUTURE_ENHANCEMENTS.md`,
     `FINAL_SUBMISSION_CHECKLIST.md`
   - `report/AgriBridge_Week_4_Evaluation_Debugging_Final_Report.docx` →
     e.g. `docs/week4/report/`
   - `docs/diagrams/evaluation-workflow.md`, `bug-lifecycle.md`,
     `testing-regression-workflow.md` → your existing `docs/diagrams/`
     folder

5. **Review the changes in VS Code** before committing:
   ```powershell
   git status
   git diff backend/src/main/java/com/agribridge/exception/GlobalExceptionHandler.java
   ```
   Confirm the diff shows only the new `AccessDeniedException` import and
   `handleAccessDenied` method being added — nothing else should change.

6. **Run the Maven test suite using Java 21**:
   ```powershell
   cd backend
   .\mvnw.cmd clean test
   ```
   Confirm the console output ends with `BUILD SUCCESS` and check the test
   count reported (expected: 34 — the 27 from Week 3 plus 7 new ones — see
   `TESTING_AND_REGRESSION_REPORT.md` if the number differs).

7. **Verify the report and supporting files are present**:
   ```powershell
   cd ..
   dir docs\week4\report\AgriBridge_Week_4_Evaluation_Debugging_Final_Report.docx
   ```

8. **Commit and push to `main`** (only after step 6 confirms a successful
   build — these are example commands; they have not been run against your
   repository from this environment):
   ```powershell
   git add backend/src/main/java/com/agribridge/exception/GlobalExceptionHandler.java
   git add backend/src/test/java/com/agribridge/exception/GlobalExceptionHandlerTest.java
   git add docs/week4 README_WEEK4.md WEEK4_CHANGELOG.md BUG_REGISTER.md DEBUGGING_LOG.md TESTING_AND_REGRESSION_REPORT.md PERFORMANCE_EVALUATION.md FUTURE_ENHANCEMENTS.md FINAL_SUBMISSION_CHECKLIST.md
   git commit -m "Week 4: fix BUG-001 (AccessDeniedException -> 403), add evaluation and final documentation"
   git push origin main
   ```

9. **Verify the final GitHub repository contents** by visiting
   `https://github.com/karmankaurnayyar/AgriBridge` in a browser and
   confirming the new files and the updated `GlobalExceptionHandler.java`
   appear as expected, and that Weeks 1–3 files are untouched.

No step above overwrites any Week 1–3 file other than the one,
intentionally-explained `GlobalExceptionHandler.java` change.
