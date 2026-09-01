package com.lenvora.app
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OcrEngine{
    private val r=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    suspend fun recognize(bitmap:Bitmap)=suspendCancellableCoroutine<String>{c->
        r.process(InputImage.fromBitmap(bitmap,0)).addOnSuccessListener{if(c.isActive)c.resume(it.text)}
            .addOnFailureListener{if(c.isActive)c.resumeWithException(it)}
    }
    fun close()=r.close()
}
