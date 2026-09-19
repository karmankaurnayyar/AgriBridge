# Buyer Request & Matching Flow (Planned — Week 4)

```mermaid
flowchart TD
    A["1. Buyer submits requirement<br/>(commodity, quantity, price range, location)"] --> B["2. POST /api/buyer-requests (planned endpoint)"]
    B --> C["3. BuyerRequest persisted with status = OPEN"]
    C --> D["4. MatchingService queries available HarvestLots"]
    D --> E["5. Rule-based filter: commodity, quantity, location, price range"]
    E --> F["6. Candidate matches returned, ranked simply<br/>(e.g., closest price match first)"]
    F --> G["7. Buyer or farmer confirms a match"]
    G --> H["8. Match persisted, linking HarvestLot ↔ BuyerRequest"]
```

**Status:** the `BuyerRequest` entity and repository exist today
(schema-ready); the `matches` table exists in `database/schema.sql` but has
no JPA entity yet. `BuyerRequestService`/`Controller` and the matching engine
itself are **planned for Week 4**. The matching rule set documented here
(commodity, quantity, location, price range) is intentionally simple and
explainable rather than predictive — see
[../architecture.md](../architecture.md).
