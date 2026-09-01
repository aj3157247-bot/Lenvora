package com.lenvora.app

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

data class MobileAd(
    val id: String,
    val title: String,
    val description: String?,
    val targetUrl: String?
)

class AdvertisementClient(
    private val baseUrl: String = "http://10.0.2.2:4000/api/v1"
) {
    private val client = OkHttpClient()

    fun load(callback: (List<MobileAd>) -> Unit) {
        val url = baseUrl.trimEnd('/') + "/advertisements/active"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                callback(emptyList())
            }

            override fun onResponse(
                call: okhttp3.Call,
                response: okhttp3.Response
            ) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(emptyList())
                        return
                    }

                    try {
                        val array = it.body?.string()
                            ?.let { body ->
                                JSONObject(body).optJSONArray("data") ?: JSONArray()
                            }
                            ?: JSONArray()

                        val ads = buildList {
                            for (i in 0 until array.length()) {
                                val item = array.getJSONObject(i)
                                add(
                                    MobileAd(
                                        id = item.getString("id"),
                                        title = item.getString("title"),
                                        description = item.optString("description")
                                            .ifBlank { null },
                                        targetUrl = item.optString("target_url")
                                            .ifBlank { null }
                                    )
                                )
                            }
                        }

                        callback(ads)
                    } catch (_: Exception) {
                        callback(emptyList())
                    }
                }
            }
        })
    }
}
