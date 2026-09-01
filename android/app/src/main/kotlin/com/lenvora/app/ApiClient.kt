package com.lenvora.app
import okhttp3.*
import java.io.IOException

class ApiClient(private val baseUrl:String){
    private val client=OkHttpClient()
    fun get(path:String,callback:(Result<String>)->Unit){
        val req=Request.Builder().url(baseUrl.trimEnd('/')+"/"+path.trimStart('/')).get().build()
        client.newCall(req).enqueue(object:Callback{
            override fun onFailure(call:Call,e:IOException){callback(Result.failure(e))}
            override fun onResponse(call:Call,response:Response){
                response.use{
                    if(it.isSuccessful) callback(Result.success(it.body?.string().orEmpty()))
                    else callback(Result.failure(IOException("HTTP ${it.code}")))
                }
            }
        })
    }
}
