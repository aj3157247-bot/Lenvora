package com.lenvora.app

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OfflineTranslator {
    suspend fun translate(text: String, source: String, target: String): String {
        val sourceLanguage = LanguageMap.toMlKit(source) ?: error("Unsupported source language: $source")
        val targetLanguage = LanguageMap.toMlKit(target) ?: error("Unsupported target language: $target")

        if (sourceLanguage == targetLanguage) return text

        val translator = Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
        )

        try {
            awaitTask<Unit> { success, failure ->
                translator.downloadModelIfNeeded(DownloadConditions.Builder().build())
                    .addOnSuccessListener { success(Unit) }
                    .addOnFailureListener { failure(it) }
            }

            return awaitTask { success, failure ->
                translator.translate(text)
                    .addOnSuccessListener { success(it) }
                    .addOnFailureListener { failure(it) }
            }
        } finally {
            translator.close()
        }
    }

    private suspend fun <T> awaitTask(
        register: ((T) -> Unit, (Exception) -> Unit) -> Unit
    ): T = suspendCancellableCoroutine { continuation ->
        register(
            { value -> if (continuation.isActive) continuation.resume(value) },
            { error -> if (continuation.isActive) continuation.resumeWithException(error) }
        )
    }
}
