package com.lenvora.app
import android.graphics.Bitmap
class CameraOcrPipeline(private val ocr: OcrEngine=OcrEngine(), private val detector: LanguageDetector=LanguageDetector(), private val translator: OfflineTranslator=OfflineTranslator()){
 suspend fun scanAndTranslate(bitmap:Bitmap,target:String):ScanResult{
  val text=ocr.recognize(bitmap).trim(); require(text.isNotEmpty()){"No text detected"}
  val source=detector.detect(text)
  val translated=if(source==target) text else translator.translate(text,source,target)
  return ScanResult(text,source,translated)
 }
 fun close(){ocr.close();detector.close()}
}
data class ScanResult(val originalText:String,val detectedLanguage:String,val translatedText:String)
