import { Request, Response } from 'express';
import { z } from 'zod';
import { db } from '../config/database';

const querySchema=z.object({
  q:z.string().min(1).max(200),
  source:z.string().min(2).max(10).default('en'),
  target:z.string().min(2).max(10).default('fa')
});

export async function searchDictionary(req:Request,res:Response){
  const parsed=querySchema.safeParse(req.query);
  if(!parsed.success) return res.status(400).json({error:parsed.error.flatten()});
  const {q,source,target}=parsed.data;

  const result=await db.query(`
    SELECT w.id,w.language_code,w.word,w.pronunciation,w.part_of_speech,
           COALESCE(json_agg(json_build_object(
             'meaning',m.meaning,'example',m.example
           )) FILTER (WHERE m.id IS NOT NULL),'[]') AS meanings
    FROM words w
    LEFT JOIN meanings m
      ON m.word_id=w.id AND m.target_language_code=$3
    WHERE w.language_code=$1
      AND w.word ILIKE $2
    GROUP BY w.id
    ORDER BY CASE WHEN lower(w.word)=lower($2) THEN 0 ELSE 1 END,w.word
    LIMIT 50
  `,[source,q,resultLike(q)]);

  res.json({data:result.rows});
}

function resultLike(q:string){ return `%${q}%`; }

const createSchema=z.object({
 sourceLanguage:z.string().min(2).max(10),
 word:z.string().min(1).max(200),
 pronunciation:z.string().max(200).optional(),
 partOfSpeech:z.string().max(40).optional()
});

export async function createWord(req:Request,res:Response){
 const p=createSchema.safeParse(req.body);
 if(!p.success)return res.status(400).json({error:p.error.flatten()});
 const x=p.data;
 try{
  const r=await db.query(`
   INSERT INTO words(language_code,word,pronunciation,part_of_speech)
   VALUES($1,$2,$3,$4) RETURNING *`,
   [x.sourceLanguage,x.word,x.pronunciation,x.partOfSpeech]);
  res.status(201).json({data:r.rows[0]});
 }catch(e:any){
  if(e.code==='23505')return res.status(409).json({error:'Word already exists'});
  res.status(500).json({error:'Could not create word'});
 }
}

export async function addMeaning(req:Request,res:Response){
 const p=z.object({
  targetLanguage:z.string().min(2).max(10),
  meaning:z.string().min(1).max(1000),
  example:z.string().max(1000).optional()
 }).safeParse(req.body);
 if(!p.success)return res.status(400).json({error:p.error.flatten()});
 const r=await db.query(`
   INSERT INTO meanings(word_id,target_language_code,meaning,example)
   VALUES($1,$2,$3,$4) RETURNING *`,
   [req.params.id,p.data.targetLanguage,p.data.meaning,p.data.example]);
 res.status(201).json({data:r.rows[0]});
}
