package com.lenvora.app

import com.google.mlkit.nl.languageid.LanguageIdentification
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LanguageDetector {
    private val detector = LanguageIdentification.getClient()

    suspend fun detect(text: String): String =
        suspendCancellableCoroutine { cont ->
            detector.identifyLanguage(text)
                .addOnSuccessListener { if(cont.isActive) cont.resume(if(it == "und") "en" else it) }
                .addOnFailureListener { if(cont.isActive) cont.resumeWithException(it) }
        }

    fun close() { detector.close() }
}
