import { Request, Response } from 'express';
import { z } from 'zod';
import { db } from '../config/database';

const schema = z.object({
  title: z.string().min(1).max(120),
  description: z.string().max(500).optional(),
  imageUrl: z.string().url().optional(),
  targetUrl: z.string().url().optional(),
  active: z.boolean().default(true),
  startDate: z.string().datetime().optional(),
  endDate: z.string().datetime().optional(),
});

export async function listAds(_req: Request, res: Response) {
  const result = await db.query(`
    SELECT a.*, COALESCE(SUM(CASE WHEN s.event_type='impression' THEN 1 ELSE 0 END),0) impressions,
           COALESCE(SUM(CASE WHEN s.event_type='click' THEN 1 ELSE 0 END),0) clicks
    FROM advertisements a
    LEFT JOIN ad_statistics s ON s.advertisement_id=a.id
    GROUP BY a.id
    ORDER BY a.created_at DESC
  `);
  res.json({ data: result.rows });
}

export async function getActiveAds(_req: Request, res: Response) {
  const result = await db.query(`
    SELECT id,title,description,image_url,target_url,start_date,end_date
    FROM advertisements
    WHERE active=true
      AND (start_date IS NULL OR start_date <= NOW())
      AND (end_date IS NULL OR end_date >= NOW())
    ORDER BY created_at DESC
  `);
  res.json({ data: result.rows });
}

export async function createAd(req: Request, res: Response) {
  const parsed = schema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });

  const a=parsed.data;
  const result=await db.query(`
    INSERT INTO advertisements(id,title,description,image_url,target_url,active,start_date,end_date,created_by)
    VALUES(gen_random_uuid(),$1,$2,$3,$4,$5,$6,$7,$8)
    RETURNING *
  `,[a.title,a.description,a.imageUrl,a.targetUrl,a.active,a.startDate,a.endDate,(req as any).user?.id ?? null]);

  res.status(201).json({data:result.rows[0]});
}

export async function updateAd(req: Request, res: Response) {
  const parsed=schema.partial().safeParse(req.body);
  if(!parsed.success) return res.status(400).json({error:parsed.error.flatten()});
  const a=parsed.data;
  const result=await db.query(`
    UPDATE advertisements SET
      title=COALESCE($1,title),
      description=COALESCE($2,description),
      image_url=COALESCE($3,image_url),
      target_url=COALESCE($4,target_url),
      active=COALESCE($5,active),
      start_date=COALESCE($6,start_date),
      end_date=COALESCE($7,end_date),
      updated_at=NOW()
    WHERE id=$8 RETURNING *
  `,[a.title,a.description,a.imageUrl,a.targetUrl,a.active,a.startDate,a.endDate,req.params.id]);

  if(!result.rows[0]) return res.status(404).json({error:'Advertisement not found'});
  res.json({data:result.rows[0]});
}

export async function deleteAd(req: Request, res: Response) {
  const result=await db.query('DELETE FROM advertisements WHERE id=$1 RETURNING id',[req.params.id]);
  if(!result.rows[0]) return res.status(404).json({error:'Advertisement not found'});
  res.json({success:true});
}

export async function recordEvent(req: Request, res: Response) {
  const event=req.body?.event;
  if(event!=='impression' && event!=='click') return res.status(400).json({error:'Invalid event'});
  await db.query('INSERT INTO ad_statistics(advertisement_id,event_type,user_id) VALUES($1,$2,$3)',[
    req.params.id,event,(req as any).user?.id ?? null
  ]);
  res.status(201).json({success:true});
}
