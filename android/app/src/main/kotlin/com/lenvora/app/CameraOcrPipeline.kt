package com.lenvora.app
import android.graphics.Bitmap
data class ScanResult(val originalText:String,val detectedLanguage:String,val translatedText:String)
class CameraOcrPipeline{
    private val ocr=OcrEngine(); private val detector=LanguageDetector(); private val translator=OfflineTranslator()
    suspend fun run(bitmap:Bitmap,target:String):ScanResult{
        val text=ocr.recognize(bitmap).trim()
        require(text.isNotEmpty()){"No text detected"}
        val source=detector.detect(text)
        val translated=translator.translate(text,source,target)
        return ScanResult(text,source,translated)
    }
    fun close(){ocr.close();detector.close()}
}
