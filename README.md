# Lenvora V2 - Database + Authentication

This package adds the PostgreSQL schema and secure admin authentication foundation.

## Important
- Do not commit `.env`.
- Use a strong `JWT_SECRET` (32+ characters).
- Production credentials belong in GitHub Actions Secrets / your hosting provider's secret manager.
- The admin creation endpoint is protected; there is no public "create admin" endpoint.

## Install backend dependencies

```bash
cd backend
npm install
cp .env.example .env
```

Then create the PostgreSQL database and run:

```bash
psql "$DATABASE_URL" -f ../database/schema.sql
```

Run:

```bash
npm run dev
```

## API

`POST /api/v1/auth/login`

```json
{
  "email": "admin@example.com",
  "password": "your-password"
}
```

Then send:

`Authorization: Bearer <token>`

to protected endpoints.

This is the authentication foundation. Password reset, refresh tokens, rate limiting, audit logs and production deployment controls should be added before public release.
