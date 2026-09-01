package com.lenvora.app

import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private object MlLanguageMap {
    fun ml(code: String): String? = when (code) {
        "en" -> TranslateLanguage.ENGLISH
        "fa" -> TranslateLanguage.PERSIAN
        "ar" -> TranslateLanguage.ARABIC
        "tr" -> TranslateLanguage.TURKISH
        "de" -> TranslateLanguage.GERMAN
        "fr" -> TranslateLanguage.FRENCH
        "es" -> TranslateLanguage.SPANISH
        else -> null
    }
}

class OfflineTranslator {
    suspend fun translate(text: String, source: String, target: String): String {
        val sourceLanguage = MlLanguageMap.ml(source)
            ?: error("Unsupported source language: $source")
        val targetLanguage = MlLanguageMap.ml(target)
            ?: error("Unsupported target language: $target")

        if (sourceLanguage == targetLanguage) return text

        val translator = Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
        )

        try {
            translator.downloadModelIfNeeded(
                DownloadConditions.Builder().build()
            ).await()

            return translator.translate(text).await()
        } finally {
            translator.close()
        }
    }
}

private suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) continuation.resume(result)
        }
        addOnFailureListener { error ->
            if (continuation.isActive) continuation.resumeWithException(error)
        }
        addOnCanceledListener {
            continuation.cancel()
        }
        continuation.invokeOnCancellation {
            cancel()
        }
    }
