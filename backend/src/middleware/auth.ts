import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';

export interface AuthRequest extends Request { user?: { id: string; role: string; type?: string; email?: string } }

export function requireAuth(req: AuthRequest, res: Response, next: NextFunction) {
  const token = req.headers.authorization?.startsWith('Bearer ') ? req.headers.authorization.slice(7) : '';
  if (!token) return res.status(401).json({ error: 'Authentication required' });
  try {
    const user = jwt.verify(token, process.env.JWT_SECRET ?? '') as AuthRequest['user'];
    if (!user || user.type !== 'admin' || user.role !== 'owner' || user.id !== 'owner') {
      return res.status(403).json({ error: 'Owner access only' });
    }
    req.user = user;
    next();
  } catch {
    return res.status(401).json({ error: 'Invalid or expired token' });
  }
}
