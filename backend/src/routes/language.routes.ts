import { Router } from 'express';

export const languageRouter = Router();

const languages = [
  { code: 'fa', name: 'فارسی', direction: 'rtl' },
  { code: 'en', name: 'English', direction: 'ltr' },
  { code: 'ar', name: 'العربية', direction: 'rtl' },
  { code: 'tr', name: 'Türkçe', direction: 'ltr' },
  { code: 'de', name: 'Deutsch', direction: 'ltr' },
  { code: 'fr', name: 'Français', direction: 'ltr' },
  { code: 'es', name: 'Español', direction: 'ltr' },
];

languageRouter.get('/', (_req, res) => res.json({ data: languages }));
