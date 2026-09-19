# AgriBridge — Agribusiness Solution

**Individual internship project — Week 2: Software Architecture Design and Development Foundation**

## 1. Project Description

AgriBridge is a role-based platform intended to connect farmers and buyers
around farm records, crop cycles, harvest lots and purchase requests,
reducing the coordination and record-keeping friction that smallholder
farmers and small buyers often face.

## 2. Problem Statement

Smallholder farmers frequently keep farm and produce records on paper or in
disconnected notes, and have limited visibility into buyer demand. Buyers,
in turn, struggle to discover available, verified supply. AgriBridge aims to
give both sides a simple, shared system for this information.

## 3. Objectives

- Build a working software foundation (architecture, database, initial
  APIs) that a later phase can extend into a full MVP.
- Demonstrate a clean, realistic three-tier architecture suitable for one
  developer to build and maintain within a four-week timeline.
- Keep scope honest: implement a small number of modules completely rather
  than many modules partially.

## 4. Features

| Feature | Status |
|---|---|
| User registration & login (BCrypt-hashed passwords) | **Implemented** |
| Role-based access (FARMER / BUYER / ADMIN) | **Implemented** |
| Farm Management — add / view / update / delete | **Implemented** |
| Frontend for registration, login, dashboard, farms | **Implemented** |
| Crop Management | Schema ready — planned **Week 3** |
| Harvest Lot Management | Schema ready — planned **Week 3** |
| Buyer Requests | Schema ready — planned **Week 4** |
| Rule-Based Matching | Planned **Week 4** |
| Dashboard analytics (live data) | Planned **Week 4** |
| Full JWT-filter enforcement | Planned **Week 3** |

See [docs/roadmap.md](docs/roadmap.md) for the detailed week-by-week plan.

## 5. Technology Stack

- **Backend:** Java 21, Spring Boot 3.3, Spring Web, Spring Data JPA,
  Hibernate, Spring Security, Maven
- **Database:** MySQL 8+
- **Frontend:** HTML5, CSS3, vanilla JavaScript (Fetch API)
- **Security:** BCrypt, JWT foundation (`jjwt`), RBAC
- **Tools:** Git/GitHub, Postman, JUnit 5, Mockito

## 6. Architecture Overview

Three-tier, modular monolith: Presentation (static frontend) → Application/
Business Logic (Spring Boot controllers + services) → Data Access (Spring
Data JPA/Hibernate) → MySQL. Full write-up: [docs/architecture.md](docs/architecture.md).
Diagrams (GitHub-native Mermaid): [docs/diagrams/](docs/diagrams/).

## 7. Folder Structure

```text
AgriBridge/
├── backend/                 # Spring Boot application (Maven project)
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── frontend/                 # Static HTML/CSS/JS
│   ├── index.html, login.html, register.html, dashboard.html, farms.html
│   ├── crops.html, harvests.html, buyers.html   (Week 3/4 placeholders)
│   ├── css/
│   └── js/
├── database/
│   ├── schema.sql
│   └── sample_data.sql
├── docs/
│   ├── architecture.md
│   ├── api-documentation.md
│   ├── database-design.md
│   ├── roadmap.md
│   └── diagrams/             # Mermaid diagrams (render natively on GitHub)
├── postman/
│   └── AgriBridge_API_Collection.json
├── README.md
├── .gitignore
└── LICENSE
```

## 8. Database Setup

Requires MySQL 8+ running locally.

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample_data.sql
```

Details and the full ER diagram: [docs/database-design.md](docs/database-design.md).

## 9. Backend Setup

```bash
cd backend
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties
# edit application-dev.properties with your local MySQL username/password
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The API starts on `http://localhost:8080`. No real credentials are
committed anywhere in this repository — see `application.properties`, which
reads everything from environment variables.

> **Build note:** this project was developed in an offline sandbox without
> access to Maven Central, so `mvn compile`/`mvn test` could not be run to
> produce a verified build log before packaging this submission. The code
> follows current Spring Boot 3.3/Spring Security 6 APIs throughout; please
> run `mvn clean install` as your first step and see backend/README.md if
> anything needs adjustment.

## 10. Frontend Setup

The frontend is plain static files — no build step. Either open
`frontend/index.html` directly in a browser, or serve the folder with any
static server, e.g.:

```bash
cd frontend
python3 -m http.server 5500
# then visit http://localhost:5500
```

`frontend/js/api.js` points at `http://localhost:8080` by default (see
`API_BASE_URL`); update it if your backend runs elsewhere.

## 11. API Testing

Import [`postman/AgriBridge_API_Collection.json`](postman/AgriBridge_API_Collection.json)
into Postman. It includes working requests for every implemented endpoint
and a separate "Planned (Week 3-4)" folder documenting the agreed contract
for endpoints not yet built. Full request/response examples:
[docs/api-documentation.md](docs/api-documentation.md).

## 12. Testing Instructions

```bash
cd backend
mvn test
```

Runs `FarmServiceTest` and `AuthServiceTest` (JUnit 5 + Mockito). See
[docs/architecture.md](docs/architecture.md#7-testing-strategy) for what's
covered now vs. planned.

## 13. Individual Developer Information

```text
Developer:     Karman Kaur Nayyar
Role:          Junior Software Developer Intern
Project Type:  Individual Project
Duration:      4 Weeks
Current Week:  Week 2 — Software Architecture Design and Documentation
```

## 14. Known Limitations

- **JWT is issued but not yet enforced.** `/api/auth/login` returns a
  signed JWT, but Week 2 endpoints are actually protected via HTTP Basic
  (same credentials). Wiring a `JwtAuthenticationFilter` is planned for
  Week 3 — see [docs/architecture.md](docs/architecture.md#6-security-approach).
- **The frontend stores Basic-Auth credentials (base64) in `localStorage`**
  as a simplification for local Week 2 development. This is not a
  production-safe pattern and should not be reused once JWT enforcement
  lands in Week 3.
- **Crop, Harvest Lot, and Buyer Request modules have entities and database
  tables only** — no service or controller layer yet, so their API
  endpoints do not exist and their frontend pages are placeholders.
- **No live Dashboard data** beyond the farm count — the dashboard summary
  endpoint is planned for Week 4.
- **Not independently build-verified in this environment.** See the build
  note in Section 9 above.
- **CORS is wide open (`*`)** for local development convenience; this
  should be restricted to a specific origin before any real deployment.

## 15. Future Enhancements

Beyond the four-week window (not committed to any date): FPO/Coordinator
role, offline-capable frontend, live external market-price integration,
SMS/email notification delivery, containerized deployment, and a
non-rule-based matching model once enough real transaction data exists.

## 16. License

MIT — see [LICENSE](LICENSE).

---

*This README, and this repository, represent a Week 2 deliverable: an
architecture and development foundation, not the completed four-week
AgriBridge project. See [docs/roadmap.md](docs/roadmap.md) for what Weeks 3
and 4 will add.*
