package com.lenvora.app
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private object MlLanguageMap{
    fun ml(code:String)=when(code){
        "en"->TranslateLanguage.ENGLISH
        "fa"->TranslateLanguage.PERSIAN
        "ar"->TranslateLanguage.ARABIC
        "tr"->TranslateLanguage.TURKISH
        "de"->TranslateLanguage.GERMAN
        "fr"->TranslateLanguage.FRENCH
        "es"->TranslateLanguage.SPANISH
        else->null
    }
}
class OfflineTranslator{
    suspend fun translate(text:String,source:String,target:String):String{
        val s=MlLanguageMap.ml(source)?:error("Unsupported source language")
        val t=MlLanguageMap.ml(target)?:error("Unsupported target language")
        if(s==t)return text
        val tr=Translation.getClient(TranslatorOptions.Builder().setSourceLanguage(s).setTargetLanguage(t).build())
        try{
            awaitVoid{tr.downloadModelIfNeeded(DownloadConditions.Builder().build()).addOnSuccessListener(it).addOnFailureListener(it)}
            return await{ok,fail->tr.translate(text).addOnSuccessListener(ok).addOnFailureListener(fail)}
        }finally{tr.close()}
    }
    private suspend fun <T> await(r:((T)->Unit,(Exception)->Unit)->Unit)=suspendCancellableCoroutine{c->
        r({if(c.isActive)c.resume(it)},{if(c.isActive)c.resumeWithException(it)})
    }
    private suspend fun awaitVoid(r:((Void)->Unit,(Exception)->Unit)->Unit)=suspendCancellableCoroutine<Unit>{c->
        r({if(c.isActive)c.resume(Unit)},{if(c.isActive)c.resumeWithException(it)})
    }
}
