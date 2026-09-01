package com.lenvora.app.data

class DictionaryRepository(private val dao: DictionaryDao) {
    fun search(query: String, language: String) =
        dao.search(language, "%${query.trim()}%")

    fun favorites() = dao.favorites()
    fun history() = dao.history()

    suspend fun setFavorite(id: Long, value: Boolean) =
        dao.setFavorite(id, value)

    suspend fun recordSearch(query: String, language: String) {
        if (query.isNotBlank()) {
            dao.addHistory(SearchHistoryEntity(query = query.trim(), language = language))
        }
    }

    suspend fun seed() = dao.insertAll(DictionarySeed.entries)
}
