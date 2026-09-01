package com.lenvora.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DictionaryViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val repo = DictionaryRepository(
        AppDatabase.get(app).dictionaryDao()
    )

    val query = MutableStateFlow("")
    val language = MutableStateFlow("en")

    val results = combine(query, language) { q, l ->
        q to l
    }.flatMapLatest { (q, l) ->
        repo.search(q, l)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val favorites = repo.favorites().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val history = repo.history().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        viewModelScope.launch {
            repo.seed()
        }
    }

    fun setQuery(value: String) {
        query.value = value
    }

    fun setLanguage(value: String) {
        language.value = value
    }

    fun toggleFavorite(item: DictionaryEntity) {
        viewModelScope.launch {
            repo.favorite(item.id, !item.isFavorite)
            repo.recordHistory(item.word, language.value)
        }
    }
}
