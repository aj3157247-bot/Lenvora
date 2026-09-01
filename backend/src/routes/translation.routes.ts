import {Router} from 'express';
import {translate} from '../controllers/translation.controller';
export const translationRouter=Router();
translationRouter.post('/',translate);
