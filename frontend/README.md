# Palmery Auth Frontend (Next.js 16)

## Run locally

```bash
pnpm install
pnpm dev
```

Open `http://localhost:3000`.

## Environment

Copy `.env.sample` to `.env.local` and adjust values if needed.

The frontend calls backend endpoints:
- `POST /api/auth/register`
- `POST /api/auth/token`
- `POST /api/auth/introspect`
- `GET /api/debug/integration`
- `POST /api/debug/events`
- `GET /api/debug/events`
