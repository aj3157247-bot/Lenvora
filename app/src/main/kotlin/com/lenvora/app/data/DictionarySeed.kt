package com.lenvora.app.data

object DictionarySeed {
    val entries = listOf(
        DictionaryEntity("en", "hello", "/həˈloʊ/", "interjection", "سلام", "Hello, how are you?"),
        DictionaryEntity("en", "book", null, "noun", "کتاب", "I read a book."),
        DictionaryEntity("en", "computer", null, "noun", "کامپیوتر", "This computer is fast."),
        DictionaryEntity("en", "water", null, "noun", "آب", "Drink some water."),
        DictionaryEntity("en", "beautiful", null, "adjective", "زیبا", "What a beautiful day.")
    )
}
