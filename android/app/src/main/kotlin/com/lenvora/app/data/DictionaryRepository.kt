package com.lenvora.app.data
class DictionaryRepository(private val dao:DictionaryDao){
    fun search(q:String,l:String)=dao.search(l,"%${q.trim()}%")
    fun favorites()=dao.favorites()
    fun history()=dao.history()
    suspend fun seed()=dao.insertAll(listOf(
        DictionaryEntity(language="en",word="hello",pronunciation="/həˈloʊ/",partOfSpeech="interjection",meaning="سلام",example="Hello, how are you?"),
        DictionaryEntity(language="en",word="book",partOfSpeech="noun",meaning="کتاب",example="I read a book."),
        DictionaryEntity(language="en",word="computer",partOfSpeech="noun",meaning="کامپیوتر",example="This computer is fast."),
        DictionaryEntity(language="en",word="water",partOfSpeech="noun",meaning="آب",example="Drink some water."),
        DictionaryEntity(language="en",word="beautiful",partOfSpeech="adjective",meaning="زیبا",example="What a beautiful day.")
    ))
    suspend fun favorite(id:Long,v:Boolean)=dao.setFavorite(id,v)
    suspend fun recordHistory(q:String,l:String){
        if(q.isNotBlank()){
            dao.addHistory(SearchHistoryEntity(query=q,language=l))
        }
    }
}
