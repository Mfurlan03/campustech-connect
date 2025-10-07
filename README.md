# CampusTech Connect

A peer-to-peer laptop marketplace tailored for university students. CampusTech Connect focuses on academic relevance, student trust (university email), and smarter buying decisions via spec-based filters, recommendations by major, wishlists with target prices, and a comparison tool with external market context.

- Topic: Consumer Information Site
- Course: MIE 350 – Design and Analysis of Information Systems (UofT)
- Team: Group 2

## Why this exists

Students often struggle with:
- Device malfunctions and performance gaps for academic software
- Budget constraints and unreliable public marketplaces
- Listings not tailored to academic needs

CampusTech Connect addresses this with student-only access, academic filters, spec comparisons, and price context.

## Core Features

- Authentication
  - University email registration and JWT-based login
  - Role support: Buyer, Seller (users can perform both)
- Listings (Seller)
  - Create, view, update, delete laptop listings
  - Spec fields: brand, model, CPU, GPU, RAM, storage, price
  - Listing verification flag
- Browsing (Buyer)
  - Search + filters (price, specs, etc.)
- Wishlist
  - Add/remove laptops, set/update target price
  - View your wishlist
- Recommendations
  - Program/major-based suggested specs and models
- Comparison
  - Compare a listing against similar market options (e.g., via eBay-based criteria) to assess price fairness

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

## System Design (from Final Report)

- Requirements
  - Functional: university email login, spec-based filters, recommendations, wishlist + target price, comparison tool
  - Non-Functional: security (HTTPS, hashed passwords), usability, performance (indexed queries, pagination), maintainability, browser compatibility
- Models/Diagrams
  - UML Class Diagram: User, Buyer, Seller, Laptop, WishlistItem, RecommendationRule; relationships for listings and wishlists
  - DFDs: Context, Level 0, and Level 1 (Compare Laptops)
  - Statechart: Laptop lifecycle (Listed → In Wishlist/Unlisted/Purchased)
  - Sequence: Compare flow with external API
- Testing
  - Spring mockMvc integration tests for auth, listings, wishlist
  - Informal usability testing with students → UI clarity improvements
- Known limitations
  - Mobile UI is accessible but not fully optimized; responsive redesign and PWA planned
  - Messaging and full transaction flow out-of-scope for initial version

## Roadmap

- Mobile-first responsive UI, improved navigation and touch targets
- Progressive Web App (offline, installable)
- Performance and security testing (JMeter/Locust, pentests)
- Usability improvements (clearer compare/wishlist affordances)
- Enhanced comparison data sources and caching
- Optional in-app messaging and admin verification workflows

## Team

- Frontend: Ali Ahmad, Kuval Brar, Samrath Singh, Michael Furlano
- Backend: Nithilan Suresh, Fengrui Yang, Peter Bing, Isabella Liang
- Roles include presenters, editors, and Scrum Master; see report for details