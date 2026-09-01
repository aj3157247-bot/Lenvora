package com.lenvora.app

import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
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
    suspend fun translate(
        text: String,
        source: String,
        target: String
    ): String {
        val sourceLanguage = MlLanguageMap.ml(source)
            ?: error("Unsupported source language: $source")
        val targetLanguage = MlLanguageMap.ml(target)
            ?: error("Unsupported target language: $target")

        if (sourceLanguage == targetLanguage) {
            return text
        }

        val translator = Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
        )

        return try {
            translator.downloadModelIfNeeded(
                DownloadConditions.Builder().build()
            ).await()

            translator.translate(text).await()
        } finally {
            translator.close()
        }
    }
}

private suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) {
                continuation.resume(result)
            }
        }

        addOnFailureListener { error ->
            if (continuation.isActive) {
                continuation.resumeWithException(error)
            }
        }

        addOnCanceledListener {
            if (continuation.isActive) {
                continuation.cancel()
            }
        }

        // The Google Task will finish on its own if the coroutine is cancelled.
        // We intentionally do not call Task.cancel(), because Task<T> does not
        // expose a cancel() API in all supported Play Services versions.
    }
