# Data Model

## Core Entities

**Tenant discriminator:** every tenant-scoped table uses a `tenant_id` discriminator column and PostgreSQL Row Level Security (RLS) to enforce isolation. This is the default, simple, and cost-effective approach. Schema-per-tenant remains an optional pattern for customers requiring physical separation.

### Client

- `clientId`
- `tenant_id`
- `name`
- `email`
- `phoneNumber`
- `address`
- `paymentMethod`
- `rating`

### Cleaner

- `cleanerId`
- `tenant_id`
- `name`
- `email`
- `phoneNumber`
- `serviceCategories`
- `locationLatitude`
- `locationLongitude`
- `availability`
- `backgroundCheckStatus`
- `rating`

### Booking

- `bookingId`
- `tenant_id`
- `clientId`
- `cleanerId`
- `serviceType`
- `propertyAddress`
- `latitude`
- `longitude`
- `scheduledAt`
- `durationHours`
- `price`
- `status` (`pending_payment`, `confirmed`, `in_progress`, `completed`, `cancelled`)
- `paymentReference`
- `escrowStatus`
- `specialInstructions`

### PaymentTransaction

- `transactionId`
- `tenant_id`
- `bookingId`
- `amount`
- `currency`
- `status`
- `providerReference`
- `createdAt`

### Schedule

- `scheduleId`
- `tenant_id`
- `cleanerId`
- `bookingId`
- `startTime`
- `endTime`
- `recurringPattern`
- `status`

### Consultation

- `consultationId`
- `tenant_id`
- `clientId`
- `consultantId`
- `requestedAt`
- `scheduledAt`
- `serviceArea`
- `notes`
- `status`

### Product

- `productId`
- `tenant_id`
- `name`
- `description`
- `category`
- `price`
- `currency`
- `stockQuantity`
- `imageUrl`

### Order

- `orderId`
- `tenant_id`
- `clientId`
- `productId`
- `quantity`
- `totalAmount`
- `status`
- `createdAt`

### Rating

- `ratingId`
- `tenant_id`
- `bookingId`
- `reviewerId`
- `revieweeId`
- `score`
- `comment`
- `createdAt`

### EnterpriseAccount

- `enterpriseId`
- `name`
- `organizationType`
- `address`
- `contactEmail`
- `contactPhone`
- `billingAccountId`
- `creditLimit`
- `status`

### EnterpriseUser

- `enterpriseUserId`
- `enterpriseId`
- `userId`
- `role`
- `permissions`

## Relationships

- One `Client` can have many `Booking` entries.
- One `Cleaner` can have many `Booking` entries.
- One `Booking` can have one `PaymentTransaction`.
- One `Booking` can create one or more `Rating` records.
- One `Cleaner` can have multiple `Schedule` entries for availability and recurring assignments.
