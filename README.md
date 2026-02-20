# Palmery Auth

Microservice-ready authentication service with this hierarchy:

- `backend/` Spring Boot + Spring Security (JWT + OAuth-style grants)
- `frontend/` Next.js 16 app for register/login and token introspection

## Backend

```bash
cd backend
./gradlew bootRun
```

Main endpoints:
- `POST /api/auth/register`
- `POST /api/auth/token`
  - `grant_type=password`
  - `grant_type=client_credentials`
- `POST /api/auth/introspect`
- `GET /api/debug/integration`
- `POST /api/debug/events`
- `GET /api/debug/events`

Copy `backend/.env.sample` to `.env` (or export env vars in your shell).

## Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Copy `frontend/.env.sample` to `.env.local`.

Open `http://localhost:3000/debug` to validate frontend -> backend -> database integration.

## Docker

Build and run both services:

```bash
docker compose up --build
```

Services:
- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`

If needed, override compose env values via shell env vars before running `docker compose up`.

## GitHub Action + SonarCloud

Workflow file: `.github/workflows/ci-sonarcloud.yml`

It runs on every push to every branch and performs:
- Frontend install + build
- Backend test + JaCoCo report
- SonarCloud scan
- SonarCloud Quality Gate check (fails CI when gate fails)

You need to configure in GitHub repository settings:
- Secret: `SONAR_TOKEN` (from SonarCloud account token)
- Variable: `SONAR_PROJECT_KEY` (SonarCloud project key)
- Variable: `SONAR_ORGANIZATION` (SonarCloud organization key)

## OAuth-style integration examples

Password grant:

```bash
curl -X POST http://localhost:8080/api/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"grant_type":"password","email":"user@example.com","password":"password"}'
```

Client credentials grant (for other services):

```bash
curl -X POST http://localhost:8080/api/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"grant_type":"client_credentials","client_id":"palmery-internal-service","client_secret":"replace-with-service-client-secret"}'
```
