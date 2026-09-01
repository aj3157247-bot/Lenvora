package com.lenvora.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM dictionary_entries WHERE language = :language AND (word LIKE :pattern OR meaning LIKE :pattern) ORDER BY word LIMIT 50")
    fun search(language: String, pattern: String): Flow<List<DictionaryEntity>>

    @Query("SELECT * FROM dictionary_entries WHERE isFavorite = 1 ORDER BY word")
    fun favorites(): Flow<List<DictionaryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<DictionaryEntity>)

    @Query("UPDATE dictionary_entries SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Insert
    suspend fun addHistory(item: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 30")
    fun history(): Flow<List<SearchHistoryEntity>>
}
