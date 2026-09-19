# AgriBridge — Database Design

MySQL 8+. Full DDL: [`database/schema.sql`](../database/schema.sql). Sample
rows: [`database/sample_data.sql`](../database/sample_data.sql). Rendered
ERD: [diagrams/erd.md](diagrams/erd.md).

## Tables

### `users` — implemented, in active use

| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK, auto-increment | |
| name | VARCHAR(120), NOT NULL | |
| email | VARCHAR(150), NOT NULL, UNIQUE | Used as the Spring Security username |
| password_hash | VARCHAR(255), NOT NULL | BCrypt hash — never returned by any API |
| role | ENUM('FARMER','BUYER','ADMIN'), NOT NULL | |
| created_at | DATETIME, default CURRENT_TIMESTAMP | |

### `farms` — implemented, in active use

| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK, auto-increment | |
| owner_id | BIGINT, FK → users.id, ON DELETE CASCADE | |
| farm_name | VARCHAR(150), NOT NULL | |
| location | VARCHAR(200), NOT NULL | |
| land_area | DECIMAL(10,2), NOT NULL, CHECK > 0 | Hectares |
| soil_type | VARCHAR(100), nullable | |
| created_at / updated_at | DATETIME | `updated_at` auto-updates on row change |

### `crops` — schema ready, not yet wired to the backend (planned Week 3)

| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| farm_id | BIGINT, FK → farms.id, ON DELETE CASCADE | |
| crop_name | VARCHAR(100), NOT NULL | |
| crop_type | VARCHAR(100) | |
| sowing_date / expected_harvest_date | DATE | CHECK: harvest date ≥ sowing date |
| status | ENUM('PLANNED','GROWING','HARVEST_READY','COMPLETED') | |
| created_at | DATETIME | |

### `harvest_lots` — schema ready, not yet wired to the backend (planned Week 3)

| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| lot_code | VARCHAR(40), UNIQUE | Server-generated, not client-supplied |
| crop_id | BIGINT, FK → crops.id, ON DELETE CASCADE | |
| quantity | DECIMAL(10,2), CHECK > 0 | |
| unit | VARCHAR(20), default 'kg' | |
| harvest_date | DATE | |
| quality_grade | VARCHAR(20) | |
| expected_price | DECIMAL(10,2) | |
| availability_status | ENUM('AVAILABLE','RESERVED','SOLD') | |
| created_at | DATETIME | |

### `buyer_requests` — schema only (planned Week 4)

| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| buyer_id | BIGINT, FK → users.id, ON DELETE CASCADE | |
| commodity | VARCHAR(100), NOT NULL | |
| quantity | DECIMAL(10,2), CHECK > 0 | |
| min_price / max_price | DECIMAL(10,2) | |
| location | VARCHAR(200) | |
| required_by_date | DATE | |
| status | ENUM('OPEN','MATCHED','CANCELLED') | |
| created_at | DATETIME | |

### `matches` — schema only (planned Week 4)

| Column | Type | Notes |
|---|---|---|
| id | BIGINT, PK | |
| lot_id | BIGINT, FK → harvest_lots.id, ON DELETE CASCADE | |
| request_id | BIGINT, FK → buyer_requests.id, ON DELETE CASCADE | |
| status | ENUM('SUGGESTED','CONFIRMED','REJECTED') | |
| confirmed_at | DATETIME, nullable | |
| created_at | DATETIME | |
| — | UNIQUE (lot_id, request_id) | Prevents a duplicate match pairing |

## Relationships

- One `User` (FARMER) → many `Farm` (owner_id)
- One `Farm` → many `Crop` (farm_id)
- One `Crop` → many `HarvestLot` (crop_id) — simplifying assumption: no
  partial-lot splitting across multiple harvests in the current schema
- One `User` (BUYER) → many `BuyerRequest` (buyer_id)
- `Match` links one `HarvestLot` to one `BuyerRequest`, with a unique
  constraint preventing the same pairing from being recorded twice

## Setup

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample_data.sql
```

`spring.jpa.hibernate.ddl-auto=validate` is used (see
`application.properties`) so that `schema.sql` remains the single source of
truth for the schema — Hibernate checks the entities against it at startup
rather than auto-generating or altering tables.
