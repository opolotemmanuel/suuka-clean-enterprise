# Project Overview

## Opportunity

The cleaning services market in Uganda is large and growing, with strong demand from:

- Airbnb hosts and vacation rental managers
- Office managers and small businesses
- Homeowners and landlords
- Property management companies

Existing providers are mostly manual or directory-based. There is a clear gap for a fully automated marketplace similar to Urban Company, TaskRabbit, or an on-demand local cleaning app.

## Goal

Create a Java-based marketplace that matches clients with verified cleaners in real time, supports scheduling, and provides secure payment handling through an escrow-like flow. The platform operates as a cost-efficient multi-tenant SaaS by default, and also enables consultation services and a storefront for cleaning products. Tenant-isolated deployments for enterprises are available only when required.

## Target Users

- **Clients**: people requesting cleaning services for homes, offices, Airbnbs, and event venues.
- **Cleaners**: verified professionals who accept jobs, update availability, and get paid after client approval.
- **Consultants**: experts who provide cleaning advice, hygiene planning, and supply recommendations.
- **Product buyers**: customers who purchase cleaning supplies, equipment, and packaged cleaning kits.
- **Enterprise accounts**: businesses, property managers, and organizations managing day-to-day cleaning and procurement through corporate accounts.
- **Admins / Operations**: oversee cleaner verification, dispute resolution, pricing, and service quality.

## Business Value

- Faster booking and cleaner dispatch for urgent requests.
- Better utilization of local cleaners through real-time assignment.
- Additional revenue from consultation services and product sales.
- Support enterprise workflows, multi-user access, and corporate purchase controls.
- Improved customer trust through payment escrow and ratings.
- Scalable platform for repeat subscriptions, enterprise clients, and B2B procurement.

- Faster booking and cleaner dispatch for urgent requests.
- Better utilization of local cleaners through real-time assignment.
- Additional revenue from consultation services and product sales.
- Improved customer trust through payment escrow and ratings.
- Scalable platform for repeat subscriptions, enterprise clients, and B2B procurement.

## Languages Used

| Language | Role | Notes |
|----------|------|-------|
| **Java** | Backend services (Spring Boot, JPA/Hibernate, Spring Security, scheduling, AI agent orchestration) | Open source, no license fees. Runs on any Java 17+ runtime. Selected for maturity, enterprise readiness, and extensive ecosystem. |
| **SQL** (PostgreSQL dialect) | Data definition, queries, and row‑level security policies | Standard SQL used via JPA/Hibernate or direct queries. No extra cost. |
| **HCL** (Terraform) or **Bicep** | Infrastructure as Code to provision and manage Azure resources | Both are free, open‑format configuration languages. Bicep is Azure‑native; Terraform is multi‑cloud. Choose one – no license required. |
| **YAML / JSON** | CI/CD pipeline definitions (GitHub Actions workflows), configuration files | Ubiquitous, free, no runtime license. |
| **JavaScript / TypeScript** (optional) | Frontend web application or admin dashboard (not yet specified) | Open‑source ecosystem. Not required for the backend; added only if a UI is built. |
| **Shell** (Bash / PowerShell) | Deployment scripts, automation glue | Standard on any OS. No license concerns. |

**Key take‑away**: Every language and tool in the stack is **open source and free of licensing costs**. The project avoids any proprietary languages or runtimes that would require per‑seat or per‑core license fees.


