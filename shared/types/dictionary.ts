export type LanguageCode='fa'|'en'|'ar'|'tr'|'de'|'fr'|'es';

export interface DictionaryWord{
 id:string;
 language_code:LanguageCode;
 word:string;
 pronunciation?:string;
 part_of_speech?:string;
 meanings:Array<{meaning:string;example?:string}>;
}
