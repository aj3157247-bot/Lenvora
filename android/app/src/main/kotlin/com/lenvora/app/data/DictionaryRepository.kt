package com.lenvora.app.data

class DictionaryRepository(private val dao: DictionaryDao) {
    fun search(q: String, language: String) = dao.search(language, "%${q.trim()}%")
    fun favorites() = dao.favorites()
    fun history() = dao.history()

    suspend fun seed() = dao.insertAll(
        listOf(
            DictionaryEntity(
                language = "en",
                word = "hello",
                pronunciation = "/həˈloʊ/",
                partOfSpeech = "interjection",
                meaning = "سلام",
                example = "Hello, how are you?"
            ),
            DictionaryEntity(
                language = "en",
                word = "book",
                partOfSpeech = "noun",
                meaning = "کتاب",
                example = "I read a book."
            ),
            DictionaryEntity(
                language = "en",
                word = "computer",
                partOfSpeech = "noun",
                meaning = "کامپیوتر",
                example = "This computer is fast."
            ),
            DictionaryEntity(
                language = "en",
                word = "water",
                partOfSpeech = "noun",
                meaning = "آب",
                example = "Drink some water."
            ),
            DictionaryEntity(
                language = "en",
                word = "beautiful",
                partOfSpeech = "adjective",
                meaning = "زیبا",
                example = "What a beautiful day."
            )
        )
    )

    suspend fun favorite(id: Long, value: Boolean) = dao.setFavorite(id, value)

    suspend fun addHistory(query: String, language: String) {
        if (query.isNotBlank()) {
            dao.addHistory(SearchHistoryEntity(query = query, language = language))
        }
    }
}
