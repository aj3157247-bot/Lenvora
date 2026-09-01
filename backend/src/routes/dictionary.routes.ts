import { Router } from 'express';
import { requireAuth } from '../middleware/auth';
import { searchDictionary, createWord, addMeaning } from '../controllers/dictionary.controller';

export const dictionaryRouter = Router();

dictionaryRouter.get('/search', searchDictionary);
dictionaryRouter.post('/words', requireAuth, createWord);
dictionaryRouter.post('/words/:id/meanings', requireAuth, addMeaning);
