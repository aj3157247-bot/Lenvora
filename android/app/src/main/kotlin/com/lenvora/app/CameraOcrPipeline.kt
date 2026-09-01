package com.lenvora.app

import android.graphics.Bitmap

class CameraOcrPipeline(
    private val ocr: OcrEngine = OcrEngine(),
    private val detector: LanguageDetector = LanguageDetector(),
    private val translator: OfflineTranslator = OfflineTranslator()
) {
    suspend fun scanAndTranslate(bitmap: Bitmap, target: String): String {
        val text = ocr.recognize(bitmap).trim()
        require(text.isNotEmpty()) { "No text detected" }
        val source = detector.detect(text)
        return if (source == target) text else translator.translate(text, source, target)
    }

    fun close() {
        ocr.close()
        detector.close()
    }
}
