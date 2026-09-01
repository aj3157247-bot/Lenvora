import { Request, Response } from 'express';
import { z } from 'zod';
import { db } from '../config/database';

const searchSchema = z.object({
  q: z.string().trim().min(1).max(200),
  source: z.string().min(2).max(10).default('en'),
  target: z.string().min(2).max(10).default('fa')
});

export async function searchDictionary(req: Request, res: Response) {
  const parsed = searchSchema.safeParse(req.query);
  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });

  const { q, source, target } = parsed.data;
  const result = await db.query(`
    SELECT w.id, w.language_code, w.word, w.pronunciation, w.part_of_speech,
      COALESCE(json_agg(
        json_build_object('meaning', m.meaning, 'example', m.example)
      ) FILTER (WHERE m.id IS NOT NULL), '[]') AS meanings
    FROM words w
    LEFT JOIN meanings m
      ON m.word_id = w.id AND m.target_language_code = $3
    WHERE w.language_code = $1 AND w.word ILIKE $2
    GROUP BY w.id
    ORDER BY CASE WHEN lower(w.word) = lower($4) THEN 0 ELSE 1 END, w.word
    LIMIT 50
  `, [source, `%${q}%`, target, q]);

  return res.json({ data: result.rows });
}

const wordSchema = z.object({
  sourceLanguage: z.string().min(2).max(10),
  word: z.string().trim().min(1).max(200),
  pronunciation: z.string().max(200).optional(),
  partOfSpeech: z.string().max(40).optional()
});

export async function createWord(req: Request, res: Response) {
  const parsed = wordSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });
  const x = parsed.data;

  try {
    const result = await db.query(`
      INSERT INTO words(language_code, word, pronunciation, part_of_speech)
      VALUES($1,$2,$3,$4) RETURNING *
    `, [x.sourceLanguage, x.word, x.pronunciation, x.partOfSpeech]);
    return res.status(201).json({ data: result.rows[0] });
  } catch (error: any) {
    if (error?.code === '23505') return res.status(409).json({ error: 'Word already exists' });
    return res.status(500).json({ error: 'Could not create word' });
  }
}

export async function addMeaning(req: Request, res: Response) {
  const parsed = z.object({
    targetLanguage: z.string().min(2).max(10),
    meaning: z.string().trim().min(1).max(1000),
    example: z.string().max(1000).optional()
  }).safeParse(req.body);

  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });

  const result = await db.query(`
    INSERT INTO meanings(word_id, target_language_code, meaning, example)
    VALUES($1,$2,$3,$4) RETURNING *
  `, [req.params.id, parsed.data.targetLanguage, parsed.data.meaning, parsed.data.example]);

  return res.status(201).json({ data: result.rows[0] });
}
