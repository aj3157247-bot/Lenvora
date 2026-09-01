package com.lenvora.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dictionary_entries",
    indices = [Index(value = ["language", "word"], unique = true)]
)
data class DictionaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val language: String,
    val word: String,
    val pronunciation: String? = null,
    val partOfSpeech: String? = null,
    val meaning: String,
    val example: String? = null,
    val isFavorite: Boolean = false
)
