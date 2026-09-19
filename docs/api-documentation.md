# AgriBridge — REST API Documentation

Base URL (local): `http://localhost:8080`. All bodies are JSON. All
timestamps are ISO-8601. See [postman/AgriBridge_API_Collection.json](../postman/AgriBridge_API_Collection.json)
for a ready-to-import collection covering every endpoint below.

## Authentication

Week 2 uses **HTTP Basic** for protected endpoints (email + password from
registration/login), while `/api/auth/login` also returns a **JWT** as a
forward-compatible foundation for Week 3, when a `JwtAuthenticationFilter`
will replace HTTP Basic as the actual enforcement mechanism. Until then, the
`token` field in the login/register response is informational and can be
used for Week 3 development, but Week 2 protected requests should
authenticate with HTTP Basic (email + password), e.g. in Postman's
Authorization tab, or with `curl -u email:password`.

## Standard Error Shape

Every error response, from every endpoint, has this shape:

```json
{
  "timestamp": "2026-09-20T10:15:30",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "One or more fields are invalid.",
  "details": ["landArea: Land area must be greater than zero"]
}
```

---

## Implemented Endpoints (Week 2)

### `POST /api/auth/register`

No authentication required.

Request:
```json
{
  "name": "Ravi Kumar",
  "email": "ravi.kumar@example.com",
  "password": "SecurePass123",
  "role": "FARMER"
}
```

Response `201 Created`:
```json
{
  "userId": 1,
  "name": "Ravi Kumar",
  "email": "ravi.kumar@example.com",
  "role": "FARMER",
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresInSeconds": 3600
}
```

Errors: `400` (validation), `409` (email already registered).

### `POST /api/auth/login`

No authentication required.

Request:
```json
{ "email": "ravi.kumar@example.com", "password": "SecurePass123" }
```

Response `200 OK`: same shape as register. Errors: `401` (bad credentials).

### `GET /api/farms`

Requires authentication (HTTP Basic). Returns only farms owned by the
authenticated user.

Response `200 OK`:
```json
[
  {
    "id": 1,
    "ownerId": 1,
    "farmName": "Green Valley Farm",
    "location": "Village Rampur, Hoshiarpur, Punjab",
    "landArea": 2.5,
    "soilType": "Loamy",
    "createdAt": "2026-09-15T09:00:00",
    "updatedAt": "2026-09-15T09:00:00"
  }
]
```

### `POST /api/farms`

Requires authentication + role `FARMER` or `ADMIN`.

Request:
```json
{ "farmName": "Green Valley Farm", "location": "Village Rampur, Hoshiarpur, Punjab", "landArea": 2.5, "soilType": "Loamy" }
```

Response `201 Created`: a `FarmResponse` (as above). Errors: `400`
(validation), `401` (not authenticated), `403` (wrong role).

### `GET /api/farms/{id}`

Requires authentication. `404` if the farm doesn't exist or isn't owned by
the caller (ownership is never revealed via a `403` — this avoids leaking
which IDs exist).

### `PUT /api/farms/{id}`

Requires authentication + role `FARMER`/`ADMIN`. Same body as `POST
/api/farms`. Response `200 OK` with the updated `FarmResponse`.

### `DELETE /api/farms/{id}`

Requires authentication + role `FARMER`/`ADMIN`. Response `204 No Content`.

---

## Planned Endpoints (Not Yet Implemented)

These are documented now so the API contract is agreed up front, per
[docs/roadmap.md](roadmap.md). Calling them in Week 2 will return `404` (no
matching controller mapping exists yet).

| Method | Endpoint | Planned For |
|---|---|---|
| GET / POST / PUT / DELETE | `/api/crops` | Week 3 |
| GET / POST | `/api/harvest-lots` | Week 3 |
| GET / POST / PUT | `/api/buyer-requests` | Week 4 |
| GET | `/api/matches` | Week 4 |
| GET | `/api/dashboard/summary` | Week 4 |

## API Design Principles

- Plural nouns for collections (`/api/farms`), IDs for individual resources
  (`/api/farms/{id}`).
- Standard status codes: `200`/`201` success, `400` validation, `401`
  missing/invalid authentication, `403` valid but unauthorized role, `404`
  missing/not-owned resource, `409` conflict (duplicate email).
- Bean Validation on every request DTO; a global exception handler
  (`GlobalExceptionHandler`) guarantees the error shape above everywhere.
- No explicit `/api/v1` prefix in Week 2; the URL structure (`/api/...`)
  allows a future `/api/v2` without breaking these Week 2 endpoints.
