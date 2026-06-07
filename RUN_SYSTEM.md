# Running Suuka Clean

This guide explains how to run the Suuka Clean frontend and backend locally.

## Prerequisites

- Node.js 20 or newer
- npm
- Java 17
- Maven

Check your tools:

```bash
node --version
npm --version
java --version
mvn --version
```

## Project Structure

```text
SUUKA-CLEAN/
  frontend/   React + TypeScript + Vite app
  backend/    Spring Boot API
```

## Run The Backend

From the project root:

```bash
cd backend
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

Useful backend endpoints:

```text
POST http://localhost:8080/api/bookings
GET  http://localhost:8080/api/bookings/{bookingId}
POST http://localhost:8080/api/chatbot/message
GET  http://localhost:8080/api/chatbot/audit-logs
```

Run backend tests:

```bash
cd backend
mvn test
```

Build backend:

```bash
cd backend
mvn clean package
```

## Run The Frontend

From the project root:

```bash
cd frontend
npm install
npm run dev
```

The frontend usually runs on:

```text
http://localhost:5173
```

Build frontend:

```bash
cd frontend
npm run build
```

Preview the production build:

```bash
cd frontend
npm run preview
```

## Recommended Development Flow

Open two terminals.

Terminal 1:

```bash
cd backend
mvn spring-boot:run
```

Terminal 2:

```bash
cd frontend
npm run dev
```

Then open:

```text
http://localhost:5173
```

## Notes

- The frontend is currently a Vite app and calls backend APIs under `/api/...`.
- The backend is configured in `backend/src/main/resources/application.yml`.
- Current backend port is `8080`.
- If Vite fails with a file watcher limit error such as `ENOSPC`, close unused dev servers or increase the OS watcher limit.

## Verification Commands

Run these before committing or deploying:

```bash
cd frontend
npm run build
```

```bash
cd backend
mvn test
```
