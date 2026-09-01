import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import { healthRouter } from './routes/health.routes';
import { languageRouter } from './routes/language.routes';
import { adRouter } from './routes/advertisement.routes';

export function createApp() {
  const app = express();
  app.use(helmet());
  app.use(cors({ origin: process.env.CORS_ORIGIN?.split(',') ?? '*' }));
  app.use(express.json({ limit: '2mb' }));

  app.get('/', (_req, res) => res.json({ name: 'Lenvora API', version: 'v1' }));
  app.use('/api/v1/health', healthRouter);
  app.use('/api/v1/languages', languageRouter);
  app.use('/api/v1/advertisements', adRouter);

  return app;
}
