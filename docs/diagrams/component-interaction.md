# Component Interaction Diagram

```mermaid
flowchart TB
    Farmer(["Farmer"])
    Buyer(["Buyer"])
    Admin(["Administrator"])

    Frontend["Frontend<br/>(HTML/CSS/JS)"]

    subgraph App["Spring Boot Application (Modular Monolith)"]
        Auth["Authentication Module<br/>(implemented)"]
        FarmMod["Farm Module<br/>(implemented)"]
        CropMod["Crop Module<br/>(implemented — Week 3)"]
        LotMod["Harvest Lot Module<br/>(entity only — Week 4)"]
        BuyerMod["Buyer Request Module<br/>(entity only — Week 4)"]
        MatchMod["Matching Module<br/>(planned — Week 4)"]
    end

    DB[("MySQL Database")]

    Farmer --> Frontend
    Buyer --> Frontend
    Admin --> Frontend

    Frontend -->|REST/JSON| Auth
    Frontend -->|REST/JSON| FarmMod

    Auth -.validates session.-> FarmMod
    FarmMod --> CropMod
    CropMod --> LotMod
    BuyerMod --> MatchMod
    LotMod --> MatchMod

    Auth --> DB
    FarmMod --> DB
    CropMod --> DB
    LotMod --> DB
    BuyerMod --> DB
```

All modules inside the "Spring Boot Application" box are Java packages within
a single deployable application — not independent microservices (see
[../architecture.md](../architecture.md), "Why a Modular Monolith").
