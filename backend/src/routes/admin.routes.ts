import { Router } from 'express';
import { requireAuth, AuthRequest } from '../middleware/auth';

export const adminRouter = Router();

adminRouter.get('/me', requireAuth, (req: AuthRequest, res) => {
  res.json({ data: req.user });
});
