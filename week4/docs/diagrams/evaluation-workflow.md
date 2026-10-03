# Week 4 Evaluation Workflow

```mermaid
flowchart TD
    A[Clone and inspect existing repository] --> B[Read architecture, schema and API docs]
    B --> C[Review each module's source code<br/>Auth, Farm, Crop]
    C --> D[Identify candidate issues by code inspection]
    D --> E{Reproducible by<br/>code-level reasoning?}
    E -->|Yes| F[Log as confirmed bug<br/>BUG_REGISTER.md]
    E -->|No / design trade-off| G[Log as risk or recommendation<br/>BUG_REGISTER.md]
    F --> H[Root-cause analysis<br/>DEBUGGING_LOG.md]
    H --> I[Implement minimal, targeted fix]
    I --> J[Write verification test]
    J --> K[Manually trace test against fixed code]
    K --> L[Document in WEEK4_CHANGELOG.md]
    G --> M[Prioritize in FUTURE_ENHANCEMENTS.md]
    L --> N[Final report assembled]
    M --> N
```

This is the actual process followed for Week 4: every confirmed bug in
`BUG_REGISTER.md` passed through the "reproducible by code-level reasoning"
gate; everything else was logged as a risk or recommendation instead.
