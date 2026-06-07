# Suuka Cleaning Platform - Backend

This backend is a Spring Boot service foundation for the Suuka Clean platform.

## What it includes

- JWT authentication with BCrypt password hashing
- Role plus permission-based authorization with `@PreAuthorize`
- Standard `ApiResponse<T>` envelopes for API responses and errors
- JPA entities and repositories for users, bookings, approvals, AI recommendations, audit logs, inventory, suppliers, supply requests, and purchase orders
- Client booking APIs, cleaner job workflow APIs, and admin booking assignment APIs
- Approval workflow APIs with audit logging
- Role-scoped chatbot endpoint with permission filtering and chatbot audit history
- H2 local database by default, with PostgreSQL driver available for deployed environments
- OpenAPI documentation support at `/swagger-ui/index.html`

## Run locally

```bash
cd backend
mvn clean package spring-boot:run
```

The default database is in-memory H2:

```text
jdbc:h2:mem:suuka_clean
```

To seed a local executive admin at startup, provide:

```bash
SUUKA_ADMIN_EMAIL=admin@suukaclean.test SUUKA_ADMIN_PASSWORD='ChangeMe123!' mvn spring-boot:run
```

Public registration always creates `CLIENT` users. Privileged roles must be assigned through the protected admin role endpoint.

## Notes for developers

- Replace `JPA_DDL_AUTO=update` with migrations before production.
- Set a strong `JWT_SECRET` with at least 32 bytes.
- Use PostgreSQL by setting `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, and `DATABASE_DRIVER`.
- Add external providers for email, SMS/WhatsApp, payment processing, file storage, and real AI generation.
