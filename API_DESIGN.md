# API Design

This document defines the core REST API endpoints for the cleaning services marketplace.

## Authentication

- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh`

## Client Endpoints

- `GET /api/clients/{clientId}/bookings`
- `POST /api/bookings`
- `GET /api/bookings/{bookingId}`
- `PUT /api/bookings/{bookingId}/cancel`
- `POST /api/bookings/{bookingId}/complete`
- `POST /api/ratings`

## Cleaner Endpoints

- `GET /api/cleaners/{cleanerId}/jobs`
- `PUT /api/cleaners/{cleanerId}/availability`
- `POST /api/cleaners/{cleanerId}/accept`
- `POST /api/cleaners/{cleanerId}/decline`

## Consultation Endpoints

- `GET /api/consultations`
- `POST /api/consultations`
- `GET /api/consultations/{consultationId}`
- `PUT /api/consultations/{consultationId}/schedule`

## Product Endpoints

- `GET /api/products`
- `GET /api/products/{productId}`
- `POST /api/orders`
- `GET /api/orders/{orderId}`

## AI Endpoints

- `POST /api/ai/chat` — customer support chatbot. Body: `{ "tenantId","userId","message" }`. Returns `response`, `confidence`, `escalate` flag.
- `POST /api/ai/recommendations` — intelligent dispatch suggestions. Body: booking context; returns ranked `cleanerIds` with `scores` and `autoAssignCandidate` boolean.
- `POST /api/ai/product-summarize` — create short product descriptions or bundle recommendations. Body: `{ "productIds", "context" }`.

## Approval & Human-in-the-loop APIs

- `GET /api/ai/approvals` — list pending approvals (operators).
- `GET /api/ai/approvals/{approvalId}` — view approval details and AI-suggested actions.
- `POST /api/ai/approvals/{approvalId}/approve` — approve suggested action.
- `POST /api/ai/approvals/{approvalId}/reject` — reject suggested action and optionally provide notes.

## Enterprise Endpoints

- `GET /api/enterprises/{enterpriseId}`
- `POST /api/enterprises`
- `PUT /api/enterprises/{enterpriseId}`
- `GET /api/enterprises/{enterpriseId}/users`
- `POST /api/enterprises/{enterpriseId}/users`
- `GET /api/enterprises/{enterpriseId}/services`
- `GET /api/enterprises/{enterpriseId}/orders`

## Booking Request Example

```json
{
  "clientId": "client-123",
  "serviceType": "airbnb_turnover",
  "propertyAddress": "12 Kira Road, Kampala",
  "latitude": 0.347596,
  "longitude": 32.582520,
  "scheduledAt": "2026-06-10T09:00:00Z",
  "durationHours": 3,
  "cleanerCount": 1,
  "recurring": false,
  "paymentMethod": "mobile_money",
  "specialInstructions": "Deep clean kitchen and change towels"
}
```

## Booking Response Example

```json
{
  "bookingId": "booking-456",
  "status": "pending_payment",
  "assignedCleanerId": "cleaner-789",
  "estimatedPrice": 85000,
  "scheduledAt": "2026-06-10T09:00:00Z",
  "paymentReference": "pay-001122"
}
```

## Matching Workflow

1. Validate client location and requested service.
2. Search for available cleaners in the target radius.
3. Reserve the cleaner and lock the time slot.
4. Initiate payment authorization or escrow hold.
5. Notify cleaner and client of booking confirmation.

## Error Handling

- `400 Bad Request` — invalid input data.
- `401 Unauthorized` — authentication failure.
- `403 Forbidden` — unauthorized access.
- `404 Not Found` — resource missing.
- `409 Conflict` — booking conflicts or schedule overlaps.
- `500 Internal Server Error` — service errors.
