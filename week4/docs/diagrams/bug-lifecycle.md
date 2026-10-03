# Bug Lifecycle (as applied to BUG-001)

```mermaid
flowchart LR
    A[Detection<br/>Code review of<br/>@PreAuthorize usage] --> B[Reproduction<br/>Traced exception<br/>propagation path]
    B --> C[Diagnosis<br/>Missing AccessDeniedException<br/>handler identified]
    C --> D[Fix<br/>Added handleAccessDenied<br/>-> 403 FORBIDDEN]
    D --> E[Verification<br/>GlobalExceptionHandlerTest<br/>WK4-EXC-05]
    E --> F[Regression Testing<br/>WK4-EXC-01/02/03/04/06/07<br/>re-confirm other handlers unchanged]
    F --> G[Closed<br/>Documented in<br/>BUG_REGISTER.md]
```

Each stage above corresponds to a section of BUG-001's entry in
`../../BUG_REGISTER.md`. The Verification stage is explicitly marked in that
document as "traced, not executed by a live test runner" in the environment
used to prepare this package — see `../../DEBUGGING_LOG.md` for the honesty
disclosure.
