package com.lenvora.app.data
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DictionaryViewModel(app:Application):AndroidViewModel(app){
    private val repo=DictionaryRepository(AppDatabase.get(app).dictionaryDao())
    val query=MutableStateFlow("")
    val language=MutableStateFlow("en")
    val results=combine(query,language){q,l->q to l}.flatMapLatest{(q,l)->repo.search(q,l)}
        .stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
    val favorites=repo.favorites().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
    val history=repo.history().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
    init{viewModelScope.launch{repo.seed()}}
    fun setQuery(v:String){query.value=v}
    fun toggleFavorite(item:DictionaryEntity)=viewModelScope.launch{repo.favorite(item.id,!item.isFavorite);repo.recordHistory(item.word,language.value)}
}
