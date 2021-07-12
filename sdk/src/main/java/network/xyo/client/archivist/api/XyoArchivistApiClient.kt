package network.xyo.client.archivist.api

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import network.xyo.client.XyoBoundWitnessJson
import network.xyo.client.xyoScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

open class XyoArchivistApiClient(private val config: XyoArchivistApiConfig) {

    private val okHttp = OkHttpClient()

    val authenticated: Boolean
        get() {
            return this.token != null
        }

    var token: String?
        get() {
            return this.config.token
        }
        set(value) {
            this.config.token = value
        }

    suspend fun postBoundWitnessesAsync (
        entries: Array<XyoBoundWitnessJson>,
        closure: ((count: Int?, error: Error?) -> Void)? = null
    ) = xyoScope.async {
        val payloadString: String = Gson().toJson(entries)
        val apiDomain = config.apiDomain
        val archive = config.archive

        val request = Request.Builder()
            .url("$apiDomain/archive/$archive/bw")
            .post(payloadString.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        return@async withContext(Dispatchers.IO) {
            try {
                return@withContext okHttp.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        closure?.let { it(0, Error(response.message)) }
                        return@use false
                    }
                    return@use true
                }
            } catch (ex: IOException) {
                Log.e("xyoClient", ex.message ?: ex.toString())
            }
        }
    }

    suspend fun postBoundWitnessAsync(
        entry: XyoBoundWitnessJson
    ) = xyoScope.async {
        return@async postBoundWitnessesAsync(arrayOf(entry)).await()
    }

    companion object {
        val MEDIA_TYPE_JSON = "text/json; charset=utf-8".toMediaType()
        fun get(config: XyoArchivistApiConfig): XyoArchivistApiClient {
            return XyoArchivistApiClient(config)
        }
    }
}