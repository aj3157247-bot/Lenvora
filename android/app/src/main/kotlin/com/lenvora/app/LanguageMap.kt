package com.lenvora.app

import com.google.mlkit.nl.translate.TranslateLanguage

object LanguageMap {
    fun toMlKit(code: String): String? = when(code) {
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
