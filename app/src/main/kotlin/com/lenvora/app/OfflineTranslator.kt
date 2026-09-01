package com.lenvora.app

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OfflineTranslator {
    suspend fun translate(text: String, source: String, target: String): String {
        val src = LanguageMap.toMlKit(source) ?: error("Unsupported source language: $source")
        val dst = LanguageMap.toMlKit(target) ?: error("Unsupported target language: $target")
        require(src != dst) { "Source and target languages must differ" }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(src)
            .setTargetLanguage(dst)
            .build()

        val translator = Translation.getClient(options)
        try {
            val conditions = DownloadConditions.Builder().build()
            awaitVoid { translator.downloadModelIfNeeded(conditions).addOnSuccessListener(it).addOnFailureListener(it) }
            return await { success, failure ->
                translator.translate(text).addOnSuccessListener(success).addOnFailureListener(failure)
            }
        } finally {
            translator.close()
        }
    }

    private suspend fun <T> await(register: ( (T)->Unit, (Exception)->Unit ) -> Unit): T =
        suspendCancellableCoroutine { cont ->
            register({ if(cont.isActive) cont.resume(it) }, { if(cont.isActive) cont.resumeWithException(it) })
        }

    private suspend fun awaitVoid(register: ( (Void)->Unit, (Exception)->Unit ) -> Unit) =
        suspendCancellableCoroutine<Unit> { cont ->
            register({ if(cont.isActive) cont.resume(Unit) }, { if(cont.isActive) cont.resumeWithException(it) })
        }
}
