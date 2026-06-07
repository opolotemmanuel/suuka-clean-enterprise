# Roadmap

## Phase 1: MVP

- Define core user roles: client, cleaner, admin.
- Build Spring Boot REST API for authentication, bookings, cleaner availability, and ratings.
- Implement booking workflow with location-based cleaner matching.
- Integrate PostgreSQL persistence for users, bookings, schedules, and payments.
- Deploy backend to Azure App Service.

## Phase 2: Local Payments & Escrow

- Integrate a Uganda-focused payment gateway such as Flutterwave, Beyonic, or Yo! Payments.
- Build escrow flow: collect payment, hold funds, release on completion.
- Store payment keys in Azure Key Vault.
- Add webhook support for payment status updates.
- Add checkout flow for cleaning products and supply orders.

## Phase 3: Consultation & Product Marketplace

- Build a consultation booking flow for cleaning planning and hygiene advice.
- Add a product catalog for cleaning supplies, detergents, and equipment.
- Implement product order management and delivery coordination.
- Add consultations and product order tracking in client dashboards.

## Phase 4: Scheduling & Recurring Jobs

- Implement Quartz Scheduler for recurring and subscription-based services.
- Add cleaner calendar availability checks and conflict prevention.
- Build automated reminders for upcoming jobs.
- Support multi-cleaner and team bookings for larger sites.

## Phase 4: Real-Time Dispatch & Notifications

- Add Azure Maps integration for route calculations and geofencing.
- Implement real-time job matching and cleaner notifications.
- Use Azure Web PubSub or Event Grid for instant updates.
- Add mobile push notifications with Firebase or a similar service.

## Phase 5: Growth & Scaling

- Add ratings, reviews, and cleaner verification workflows.
- Build admin dashboards for operations, disputes, and cleaner onboarding.
- Optimize APIs and database for scale.
- Expand to additional cities and service categories.

## Phase 6: AI Integration

- Integrate Azure OpenAI for customer support chatbot, intelligent dispatch recommendations, and product summarisation.
- Implement `AIService` abstraction, backend endpoints, and operator approval workflows for critical actions.
- Add monitoring, auditing, and cost controls (model selection, caching, rate limiting).
- Roll out in stages: internal testing -> beta customers -> gradual production rollout with manual approvals enabled.
