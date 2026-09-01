import { Router, Request, Response } from 'express';
import crypto from 'node:crypto';
import jwt from 'jsonwebtoken';
import { env } from '../config/env';

export const authRouter = Router();
const attempts = new Map<string, { count: number; blockedUntil: number }>();

function validOwnerPassword(password: string) {
  const [iterationsRaw, salt, expected] = env.ADMIN_PASSWORD_HASH.split(':');
  const iterations = Number(iterationsRaw);
  if (!iterations || !salt || !expected) return false;
  const actual = crypto.pbkdf2Sync(password, salt, iterations, 32, 'sha256').toString('hex');
  return expected.length === actual.length && crypto.timingSafeEqual(Buffer.from(actual), Buffer.from(expected));
}

function clientKey(req: Request, email: string) {
  return `${req.ip}:${email}`;
}

authRouter.post('/login', (req: Request, res: Response) => {
  const email = String(req.body?.email ?? '').trim().toLowerCase();
  const password = String(req.body?.password ?? '');
  const key = clientKey(req, email);
  const now = Date.now();
  const state = attempts.get(key);
  if (state?.blockedUntil && state.blockedUntil > now) {
    return res.status(429).json({ error: 'Too many attempts. Try again later.' });
  }
  const ok = email === env.ADMIN_EMAIL.toLowerCase() && validOwnerPassword(password);
  if (!ok) {
    const next = state ?? { count: 0, blockedUntil: 0 };
    next.count += 1;
    if (next.count >= 5) { next.count = 0; next.blockedUntil = now + 15 * 60 * 1000; }
    attempts.set(key, next);
    return res.status(401).json({ error: 'Invalid owner credentials' });
  }
  attempts.delete(key);
  const token = jwt.sign({ id: 'owner', role: 'owner', type: 'admin', email }, env.JWT_SECRET, { expiresIn: '12h' });
  return res.json({ data: { token, admin: { id: 'owner', email, name: 'Lenvora Owner', role: 'owner' } } });
});

authRouter.post('/logout', (_req, res) => res.json({ data: { ok: true } }));
