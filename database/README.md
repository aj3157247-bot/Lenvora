# Lenvora V2 Database

PostgreSQL schema for users, admins, languages, dictionary, advertisements and ad analytics.

## Setup

Create a PostgreSQL database and run:

```bash
psql "$DATABASE_URL" -f database/schema.sql
```

Never commit production passwords or connection strings.
