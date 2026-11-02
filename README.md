# CampusTech Connect

[![CI](https://github.com/Mfurlan03/campustech-connect/actions/workflows/ci.yml/badge.svg)](https://github.com/Mfurlan03/campustech-connect/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7-green)
CampusTech Connect is a peer-to-peer laptop marketplace built specifically for university students. The idea came from seeing how often students struggle to find reliable devices that actually meet the needs of their programs, especially when budgets are tight and public marketplaces feel risky.

Instead of being a general resale platform, CampusTech Connect focuses on academic relevance, trust, and context, helping students make better decisions, not just faster ones

## Why this project exists

Students regularly deal with:
- Laptops that can’t handle required academic software
- Budget constraints and inconsistent quality on public resale sites
- Listings that don’t explain whether a device is actually suitable for their program

CampusTech Connect addresses this by limiting access to students, structuring listings around technical specs, and providing tools that help buyers understand whether a laptop makes sense for their major and price range.

## Core Features

Authentication & Access
- University-email registration to limit access to students
- JWT-based authentication
- Buyer and Seller roles (users can act as both)

Listings (Sellers)
- Create, update, and manage laptop listings
- Structured spec fields (CPU, GPU, RAM, storage, price)
- Optional verification flag to improve trust

Browsing & Search (Buyers)
- Search and filter listings by specs and price
- Wishlist functionality with target prices
- Ability to track listings over time

Recommendations
- Suggested specs and laptop types based on program or major
- Designed to help students avoid under- or over-buying

Price Comparison
- Compare listings against similar devices in external marketplaces
- Provides price context rather than just “cheap vs expensive”

## Tech Stack

- Backend: Java 17, Spring Boot (Web, Security, Data JPA, Scheduling)
- Auth: JWT
- Build/Dev: Maven, Docker Compose
- API Docs: Swagger/OpenAPI
- Frontend (prototype used during course): AppSmith
- DB: Configurable via application.yml

## Quickstart

Prereqs: Java 17, Maven, Docker (optional)

Local run (H2 in-memory database, seeded from `data.sql`):
```bash
cd backend
export JWT_SECRET="$(openssl rand -hex 32)"   # optional locally; required anywhere shared
export GOOGLE_API_KEY=...                      # optional: enables /compare price lookups
./mvnw spring-boot:run
```

Tests:
```bash
cd backend && ./mvnw test
```

AppSmith frontend prototype (optional):
```bash
cd backend && docker compose -f docker-compose.dev.yml up
```

Configuration:
- Secrets come from environment variables (`JWT_SECRET`, `GOOGLE_API_KEY`) and are never committed.
- Other settings live in `backend/src/main/resources/application.properties` / `application.yml`.

Swagger:
- http://localhost:8080/api/swagger-ui/index.html (the API is served under the `/api` context path)

## API Overview

Auth
- POST /auth/register
- POST /auth/login → { token, message }

Laptops
- GET /laptops
- GET /laptops/seller (JWT)
- POST /laptops (JWT)
- PUT /laptops/{id}/verify?isVerified=true|false
- DELETE /laptops/{id} (JWT)

Wishlist (JWT)
- POST /wishlist
- GET /wishlist/my-wishlist
- PUT /wishlist/my-wishlist/{laptopId}?newPrice=number
- DELETE /wishlist/my-wishlist/{laptopId}

Recommendations
- POST /recommendations
- GET /recommendations/majors
- GET /recommendations/specs/{major}

Price Compare
- GET /compare/{laptopId}

## Team

This project was built as a team effort. Roles included backend development, frontend prototyping, documentation, and presentation.

- Backend: Nithilan Suresh, Fengrui Yang, Peter Bing, Isabella Liang
- Frontend / Product: Ali Ahmad, Kuval Brar, Samrath Singh, Michael Furlano
