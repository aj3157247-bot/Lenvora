import { Router } from 'express';
import { requireAuth } from '../middleware/auth';
import { createAd, deleteAd, getActiveAds, listAds, recordEvent, updateAd } from '../controllers/advertisement.controller';

export const adRouter=Router();

adRouter.get('/active', getActiveAds);
adRouter.post('/:id/event', recordEvent);

adRouter.get('/', requireAuth, listAds);
adRouter.post('/', requireAuth, createAd);
adRouter.patch('/:id', requireAuth, updateAd);
adRouter.delete('/:id', requireAuth, deleteAd);
