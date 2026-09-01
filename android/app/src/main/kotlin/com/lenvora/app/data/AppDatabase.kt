package com.lenvora.app.data
import android.content.Context
import androidx.room.*

@Database(entities=[DictionaryEntity::class,SearchHistoryEntity::class],version=1,exportSchema=false)
abstract class AppDatabase:RoomDatabase(){
    abstract fun dictionaryDao():DictionaryDao
    companion object{
        @Volatile private var instance:AppDatabase?=null
        fun get(context:Context)=instance?: synchronized(this){
            instance?:Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"lenvora.db")
                .fallbackToDestructiveMigration().build().also{instance=it}
        }
    }
}
