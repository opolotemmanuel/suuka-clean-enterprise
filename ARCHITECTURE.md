## Architecture

## System Components

### Multi-Tenant Architecture

- **Purpose**: Run a single platform that serves multiple tenants (consumers and enterprises) while keeping logical isolation of data and configuration per tenant. Multi-tenancy reduces operational and infrastructure costs compared to per-tenant deployments.
- **Java stack (default)**: Spring Boot multi-tenant pattern using a tenant discriminator column (`tenant_id`) on tenant-scoped tables, Spring Security for tenant-scoped auth, tenancy-aware data sources, and PostgreSQL Row Level Security (RLS) to enforce isolation.
- **Notes**: Schema-per-tenant is available as an optional pattern for customers who require stronger separation, but the discriminator column is the simpler, cheaper default and scales well for hundreds of tenants.
- **Azure services**: Central Azure App Service hosting the tenant-aware backend, a shared tenant metadata store in Azure Database for PostgreSQL, and optional tenant-scoped resources for enterprises that require stricter isolation.

### 1. Real-Time Geolocation & Dispatch

- **Purpose**: Find the nearest available cleaner and dispatch jobs instantly.
- **Java stack**: Spring Boot, multithreading, spatial indexing, distance calculation.
- **Azure services**: Azure Maps API for geofencing, routing, and distance calculations.

### 2. Schedule Coordination & Booking Engine

- **Purpose**: Handle one-time requests, recurring cleans, and availability management.
- **Java stack**: Spring Boot, Quartz Scheduler, JPA/Hibernate.
- **Azure services**: Azure App Service for backend hosting, Azure Database for PostgreSQL for scheduling data.


### 3. Local Payment Processing & Escrow

- **Purpose**: Collect payment upfront, hold funds securely, release payment after job completion.
- **Java stack**: REST API integration, secure credential management, payment webhook handling.
- **Azure services**: Azure Key Vault for secrets, optional Azure Functions for payment callbacks.

### 4. Consultation & Product Commerce

- **Purpose**: Enable users to book cleaning consultations and purchase cleaning products through the platform.
- **Java stack**: Spring Boot APIs, product catalog management, order processing, and consultation scheduling.
- **Azure services**: Azure App Service for storefront APIs, Azure Blob Storage for product images, and Azure Database for PostgreSQL for catalog and order data (PostgreSQL is the single recommended DB across the platform).


### 5. Enterprise Accounts & Corporate Workflow

- **Purpose**: Let enterprises manage day-to-day cleaning operations, multiple users, budget controls, and service approvals within a corporate account. Enterprises are represented as tenants within the multi-tenant platform.
- **Java stack**: Role-based access control, tenant-aware authorization, corporate dashboard, and audit logging.
- **Azure services**: Azure Active Directory for enterprise identity, tenant-scoped configuration where required, Azure App Service for the corporate portal, Azure Monitor for usage and performance.

### 6. Network Isolation (Cost-effective)

- **Purpose**: Provide network-level isolation with minimal cost using VNets and private endpoints so tenant traffic and databases are not exposed publicly.
- **Java stack**: Configure services to use private endpoints and internal DNS; app and DB traffic flows over the VNet.
- **Azure services**: Azure Virtual Network, Private Endpoints for Azure Database for PostgreSQL and Storage, Application Gateway or simple internal load balancers as needed.

### 7. Notifications & Communication

- **Purpose**: Notify cleaners about new jobs, updates, and reminders.
- **Java stack**: Use Firebase Admin Java SDK solely for Firebase Cloud Messaging (FCM) to deliver mobile push notifications. Use the Azure Web PubSub SDK for in-app browser/websocket updates and Azure Event Grid for event routing and durable event handling.
- **Azure services**: Prefer Azure Web PubSub (free tier available) for in-app realtime updates and Event Grid for event-driven architectures. Use FCM only for mobile device push.

### AI Integration

- **Purpose**: Augment the platform with AI agents to automate low-risk tasks and provide intelligent assistance to operators and customers.
- **Use cases**:
	- **Customer support chatbot** — answer common questions, surface account and booking info, and escalate to human agents when confidence is low.
	- **Intelligent dispatch & recommendations** — propose optimal cleaner matches, estimated travel times, and suggested schedules. Provide confidence scores and require operator approval for critical or high-value jobs.
	- **Product advisory & summarisation** — generate short product descriptions, summarize reviews, and provide recommended product bundles.
- **Java integration**: Use Spring AI or LangChain4j client libraries to interact with Azure OpenAI models (GPT-4o-mini / GPT-4o). Implement a thin service layer that centralizes prompt templates, rate limiting, retries, and response validation.
- **Human approval model**: Low-risk actions (chat replies, product summaries) can be auto-approved. Critical operations (auto-assigning enterprise bookings, releasing disputed escrow funds) must create an approval record and push to an operator queue. Operators can approve/reject via the corporate dashboard or an `approvals` API. All approvals are auditable.
- **Security & privacy**: Route all model API credentials through Azure Key Vault; scrub or tokenize PII before sending to models when possible; keep logs and prompts for auditing while redacting sensitive data.

## Notes on Tenant Isolation Options

- Default: logical multi-tenancy (shared infra, tenant-scoped data) to minimize cost.
- Optional: for enterprise customers that require strict isolation, offer an "enterprise-isolated" deployment pattern where tenant-scoped resources (dedicated database or resource group) are provisioned on request. This is more costly and used only when contractually or legally required.

- Confidential computing is not part of the default platform; enable it only when legally required by the enterprise customer.

## Technical Blueprint

| System Feature | Java Framework / Tools | Azure Cloud Infrastructure |
|---|---|---|
| User & Cleaner API Business Logic | Spring Boot, Spring Security | Azure App Service |
| Map Tracking & Route Optimization | Hibernate Spatial, Geo libraries | Azure Maps API |
| Push Notifications | FCM (mobile) + Web PubSub (in-app) | Firebase + Azure Web PubSub / Event Grid |
| Database | JPA / Hibernate | Azure Database for PostgreSQL |
| Payment Secrets | Java Cryptography | Azure Key Vault |

## High-Level Flow

1. Client submits a booking request.
2. System verifies user, checks cleaner availability, and computes closest matches.
3. Booking is scheduled or dispatched immediately.
4. Payment is collected into escrow.
5. Cleaner completes service and client approves release.
6. Payment is released and records are updated.
