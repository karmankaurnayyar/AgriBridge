# Entity Relationship Diagram (ERD)

Reflects the full planned schema in [../../database/schema.sql](../../database/schema.sql).
`users` and `farms` are backed by working JPA entities in Week 2; the rest
are schema-ready for Week 3/4 (see [../roadmap.md](../roadmap.md)).

```mermaid
erDiagram
    USERS ||--o{ FARMS : owns
    USERS ||--o{ BUYER_REQUESTS : posts
    FARMS ||--o{ CROPS : grows
    CROPS ||--o{ HARVEST_LOTS : produces
    HARVEST_LOTS ||--o{ MATCHES : "matched via"
    BUYER_REQUESTS ||--o{ MATCHES : "matched via"

    USERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar password_hash
        enum role
        datetime created_at
    }
    FARMS {
        bigint id PK
        bigint owner_id FK
        varchar farm_name
        varchar location
        decimal land_area
        varchar soil_type
        datetime created_at
        datetime updated_at
    }
    CROPS {
        bigint id PK
        bigint farm_id FK
        varchar crop_name
        varchar crop_type
        date sowing_date
        date expected_harvest_date
        enum status
    }
    HARVEST_LOTS {
        bigint id PK
        varchar lot_code UK
        bigint crop_id FK
        decimal quantity
        varchar unit
        date harvest_date
        varchar quality_grade
        decimal expected_price
        enum availability_status
    }
    BUYER_REQUESTS {
        bigint id PK
        bigint buyer_id FK
        varchar commodity
        decimal quantity
        decimal min_price
        decimal max_price
        varchar location
        date required_by_date
        enum status
    }
    MATCHES {
        bigint id PK
        bigint lot_id FK
        bigint request_id FK
        enum status
        datetime confirmed_at
    }
```
