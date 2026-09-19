# Farm & Crop Registration Flow

```mermaid
sequenceDiagram
    participant U as Farmer (Browser)
    participant F as Frontend (JS)
    participant C as FarmController
    participant S as FarmService
    participant D as FarmRepository / MySQL

    U->>F: Submit farm details (name, location, area, soil type)
    F->>C: POST /api/farms (Authorization: Basic ...)
    C->>S: createFarm(ownerEmail, request)
    S->>D: resolve owner by email
    D-->>S: User
    S->>D: save(Farm)
    D-->>S: saved Farm (with id)
    S-->>C: FarmResponse
    C-->>F: 201 Created
    F-->>U: Farm added to list

    Note over U,D: Crop registration (Crop entity exists,<br/>service/controller planned for Week 3)
```

The Farm half of this flow is **implemented** in Week 2. Crop registration
will follow the identical controller → service → repository pattern once
built in Week 3.
