# Harvest Lot Flow (Planned — Week 3)

```mermaid
flowchart TD
    A["1. Farmer selects a harvest-ready crop"] --> B["2. Enters quantity, quality grade, expected price"]
    B --> C["3. POST /api/harvest-lots (planned endpoint)"]
    C --> D["4. HarvestLotService validates crop status"]
    D --> E["5. Server generates a unique lot code"]
    E --> F["6. HarvestLot persisted with status = AVAILABLE"]
    F --> G["7. Lot becomes visible for future buyer matching"]
```

**Status:** the `HarvestLot` entity and `HarvestLotRepository` exist today
(schema-ready), but `HarvestLotService` and `HarvestLotController` are not
yet implemented. This diagram documents the intended flow for Week 3 — see
[../roadmap.md](../roadmap.md).
