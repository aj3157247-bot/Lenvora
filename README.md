# Lenvora V2 Dictionary Engine

This package adds the dictionary foundation for Lenvora V2.

## Features
- Word search API
- Meanings
- Examples
- Pronunciation fields
- Favorites/history database tables
- Admin-only word creation
- Admin-only meaning creation
- Translation API contract for the future offline engine

## Database

Run:

```bash
psql "$DATABASE_URL" -f database/schema.sql
psql "$DATABASE_URL" -f database/dictionary.sql
```

## Important

The sentence translation engine is **not falsely implemented as a normal dictionary lookup**. Real offline sentence translation requires downloadable/on-device language models. That engine will be added in the Android stage.
