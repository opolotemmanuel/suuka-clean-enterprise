# Kampala Cleaning Services Marketplace

An automated on-demand booking platform for homes, Airbnbs, offices, and organizations in Uganda. This project uses Java and Azure to power a scalable, secure, and localized cleaning services marketplace.

## Purpose

- Build a marketplace where clients can instantly find and book verified cleaners.
- Provide consultation services for cleaning plans, hygiene protocols, and product recommendations.
- Offer a product storefront so users can buy cleaning supplies and equipment directly.
- Support emergency cleans, scheduled bookings, recurring subscriptions, and office contracts.
- Operate as a cost-efficient multi-tenant platform (default) and enable enterprise accounts for organizations to manage day-to-day services, multiple users, and corporate workflows.
- Offer tenant-isolated deployments only when required by enterprise customers (optional, higher cost).
- Use local payment methods with escrow to build trust and reliability.
- Scale from Kampala to broader East Africa.

## Core Components

- **Real-time geolocation and dispatch**
- **Consultation scheduling and service recommendations**
- **Product catalog and checkout for cleaning supplies**
- **Scheduling and automated booking engine**
- **Secure local payment processing with escrow**
- **Cleaner and client management**
- **Ratings, verification, and service history**

## Documentation

- `PROJECT_OVERVIEW.md` — market opportunity, target users, and business goals.
- `ARCHITECTURE.md` — system components, Java frameworks, and Azure infrastructure.
- `API_DESIGN.md` — API patterns, booking endpoint examples, and payloads.
- `DEPLOYMENT.md` — cloud deployment plan, Azure services, and security.
- `ROADMAP.md` — phased implementation plan for the project.
- `DATA_MODEL.md` — entity definitions and relationships.

## Next Steps

1. Review project goals and architecture.
2. Define minimum viable product (MVP) requirements.
3. Begin Spring Boot service development with booking and matching APIs.
4. Configure Azure resources, database, and secrets.

## Developer Pitch Samples

- `backend/` contains a Spring Boot sample backend skeleton with a booking API prototype.
- `frontend/` contains a React + TypeScript prototype landing page to show the platform vision to developers.
