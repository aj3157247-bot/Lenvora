import { Router } from 'express';
import { z } from 'zod';

export const adRouter = Router();

const ads: Array<{
  id: string; title: string; description?: string; imageUrl?: string;
  targetUrl?: string; active: boolean; startDate?: string; endDate?: string;
}> = [];

const adSchema = z.object({
  title: z.string().min(1).max(120),
  description: z.string().max(500).optional(),
  imageUrl: z.string().url().optional(),
  targetUrl: z.string().url().optional(),
  active: z.boolean().default(true),
  startDate: z.string().optional(),
  endDate: z.string().optional(),
});

adRouter.get('/', (_req, res) => res.json({ data: ads.filter(a => a.active) }));

adRouter.post('/', (req, res) => {
  const result = adSchema.safeParse(req.body);
  if (!result.success) return res.status(400).json({ error: result.error.flatten() });

  const ad = { id: crypto.randomUUID(), ...result.data };
  ads.push(ad);
  return res.status(201).json({ data: ad });
});
