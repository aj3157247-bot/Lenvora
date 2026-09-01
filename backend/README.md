# Lenvora V2 Backend

Initial TypeScript/Express API for Lenvora.

## Included
- Health API
- Seven supported UI languages
- Advertisement API prototype
- PostgreSQL schema
- JWT middleware
- Environment configuration
- Build/test scripts

## Run locally
1. Copy `.env.example` to `.env`.
2. Install dependencies with `npm install`.
3. Create the PostgreSQL database and run `database/schema.sql`.
4. Run `npm run dev`.

The advertisement list is currently in-memory as a prototype. PostgreSQL persistence will be connected in the next backend step.
