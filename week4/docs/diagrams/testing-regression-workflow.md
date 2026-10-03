# Testing and Regression Workflow

```mermaid
flowchart TD
    A[Week 3 baseline: 27 tests<br/>AuthServiceTest, FarmServiceTest, CropServiceTest] --> B[Week 4: new GlobalExceptionHandlerTest<br/>7 tests added]
    B --> C{Run mvnw.cmd clean test}
    C --> D[Existing 27 tests<br/>must still pass]
    C --> E[New 7 tests<br/>must pass, including<br/>BUG-001 verification]
    D --> F{All 34 pass?}
    E --> F
    F -->|Yes| G[BUILD SUCCESS<br/>Week 4 regression confirmed]
    F -->|No| H[Investigate failure<br/>update DEBUGGING_LOG.md]
    H --> C
```

This package was prepared without the ability to execute step **C** (no
Maven Central access in the preparation environment — see
`../../DEBUGGING_LOG.md`, "Environment Limitation"). The developer should
run this workflow locally, where the Week 3 baseline was already confirmed
working, and record the actual outcome.
