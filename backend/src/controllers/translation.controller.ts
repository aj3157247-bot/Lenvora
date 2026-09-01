import {Request,Response} from 'express';
import {z} from 'zod';

const schema=z.object({
 sourceLanguage:z.string().min(2).max(10),
 targetLanguage:z.string().min(2).max(10),
 text:z.string().min(1).max(5000)
});

export async function translate(req:Request,res:Response){
 const p=schema.safeParse(req.body);
 if(!p.success)return res.status(400).json({error:p.error.flatten()});

 // Offline translation belongs to the Android model engine.
 // Server endpoint is intentionally a contract for future sync/online fallback.
 res.status(501).json({
   error:'Translation engine is not installed on the server yet',
   mode:'offline-first',
   request:p.data
 });
}
