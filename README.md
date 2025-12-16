# CampusTech Connect
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

Local run:
```bash
cd CampusConnect
./mvnw clean spring-boot:run
```

Tests:
```bash
./mvnw test
```

Docker:
```bash
docker compose up --build
```

Config:
- Edit src/main/resources/application.yml for DB and JWT settings.

Swagger:
- http://localhost:8080/swagger-ui
- http://localhost:8080/swagger-ui/index.html

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
