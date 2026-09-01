package com.lenvora.app

import org.json.JSONArray
import okhttp3.OkHttpClient
import okhttp3.Request

 data class MobileAd(val id:String,val title:String,val description:String?,val targetUrl:String?)

class AdvertisementClient {
    private val client=OkHttpClient()
    fun load(callback:(List<MobileAd>)->Unit){
        val req=Request.Builder().url(BuildConfig.LENVORA_API_URL.trimEnd('/')+"/advertisements/active").build()
        client.newCall(req).enqueue(object:okhttp3.Callback{
            override fun onFailure(call:okhttp3.Call,e:java.io.IOException){callback(emptyList())}
            override fun onResponse(call:okhttp3.Call,response:okhttp3.Response){
                response.use {
                    if(!it.isSuccessful){callback(emptyList());return}
                    try{
                        val arr=it.body?.string()?.let{body->org.json.JSONObject(body).optJSONArray("data")?:JSONArray()}?:JSONArray()
                        val out=buildList{for(i in 0 until arr.length()){val x=arr.getJSONObject(i);add(MobileAd(x.getString("id"),x.getString("title"),x.optString("description").ifBlank{null},x.optString("target_url").ifBlank{null}))}}
                        callback(out)
                    }catch(_:Exception){callback(emptyList())}
                }
            }
        })
    }
}
