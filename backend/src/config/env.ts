import 'dotenv/config';
import { z } from 'zod';

const schema = z.object({
  PORT: z.coerce.number().default(4000),
  DATABASE_URL: z.string().min(1),
  JWT_SECRET: z.string().min(32),
  CORS_ORIGIN: z.string().default('*'),
  ADMIN_EMAIL: z.string().email(),
  ADMIN_PASSWORD_HASH: z.string().regex(/^\d+:[a-f0-9]+:[a-f0-9]+$/),
});

export const env = schema.parse(process.env);
