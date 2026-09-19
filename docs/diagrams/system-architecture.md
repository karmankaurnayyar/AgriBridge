# System Architecture Diagram

This diagram renders natively on GitHub (Mermaid). It shows the layered,
three-tier architecture described in [docs/architecture.md](../architecture.md).

```mermaid
flowchart TB
    Users["User Roles<br/>Farmer · Buyer · Administrator"]
    Frontend["Presentation Layer<br/>HTML5 / CSS3 / Vanilla JavaScript"]
    API["Application Layer — REST API<br/>Spring Boot Controllers"]
    Business["Business Logic Layer<br/>Services · Validation"]
    Data["Data Access Layer<br/>Spring Data JPA / Hibernate"]
    DB[("MySQL Database")]
    Security["Security Layer<br/>Spring Security · BCrypt · RBAC<br/>JWT foundation"]

    Users --> Frontend
    Frontend -->|HTTPS / JSON| API
    API --> Business
    Business --> Data
    Data --> DB
    Security -.secures.-> API

    style Users fill:#2E75B6,color:#fff
    style DB fill:#1F3864,color:#fff
    style Security fill:#FCE9C6,color:#5A4300,stroke:#B8860B,stroke-dasharray: 4 2
```

**Week 2 status:** the full path (Frontend → API → Business → Data → MySQL)
is implemented and working for the Authentication and Farm Management
modules. The Security layer enforces authentication via HTTP Basic (backed
by BCrypt-hashed passwords) for Week 2; a dedicated JWT filter is planned for
Week 3 (see [../roadmap.md](../roadmap.md)).
