import { Request, Response } from 'express';
import { z } from 'zod';
import { createAdmin, loginAdmin } from '../services/auth.service';

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
});

const createSchema = loginSchema.extend({
  name: z.string().min(2).max(120),
  role: z.enum(['admin','editor']).default('admin'),
});

export async function login(req: Request, res: Response) {
  const parsed = loginSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });

  const result = await loginAdmin(parsed.data.email, parsed.data.password);
  if (!result) return res.status(401).json({ error: 'Invalid email or password' });

  return res.json({ data: result });
}

export async function create(req: Request, res: Response) {
  const parsed = createSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });

  try {
    const admin = await createAdmin(
      parsed.data.email,
      parsed.data.password,
      parsed.data.name,
      parsed.data.role
    );
    return res.status(201).json({ data: admin });
  } catch (error: any) {
    if (error?.code === '23505') {
      return res.status(409).json({ error: 'Admin email already exists' });
    }
    return res.status(500).json({ error: 'Could not create admin' });
  }
}
