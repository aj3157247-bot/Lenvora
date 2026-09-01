package com.lenvora.app
object AppStrings{
    fun title(fa:Boolean)="Lenvora V2"
    fun dictionary(fa:Boolean)=if(fa)"دیکشنری آفلاین" else "Offline Dictionary"
    fun search(fa:Boolean)=if(fa)"جستجوی کلمه یا معنی" else "Search word or meaning"
    fun camera(fa:Boolean)=if(fa)"📷 ترجمه از دوربین" else "📷 Translate from Camera"
    fun favorites(fa:Boolean)=if(fa)"علاقه‌مندی‌ها" else "Favorites"
    fun history(fa:Boolean)=if(fa)"تاریخچه" else "History"
    fun translate(fa:Boolean)=if(fa)"ترجمه" else "Translate"
}
