package com.lenvora.app
import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LanguageDetector{
    private val d=LanguageIdentification.getClient()
    suspend fun detect(text:String)=suspendCancellableCoroutine<String>{c->
        d.identifyLanguage(text).addOnSuccessListener{if(c.isActive)c.resume(if(it=="und")"en" else it)}
            .addOnFailureListener{if(c.isActive)c.resumeWithException(it)}
    }
    fun close()=d.close()
}
