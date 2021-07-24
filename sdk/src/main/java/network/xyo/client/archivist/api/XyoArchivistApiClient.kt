package network.xyo.client.archivist.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import network.xyo.client.XyoBoundWitnessJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class PostBoundWitnessesResult (
    val count: Int,
    val errors: List<Error>? = null
    )

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
        entries: Array<XyoBoundWitnessJson>
    ): PostBoundWitnessesResult {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(entries.javaClass)
        val payloadString = adapter.toJson(entries)
        val apiDomain = config.apiDomain
        val archive = config.archive

        val request = Request.Builder()
            .url("$apiDomain/archive/$archive/bw")
            .post(payloadString.toRequestBody(MEDIA_TYPE_JSON))
            .build()

        return withContext(Dispatchers.IO) {
            try {
                return@withContext okHttp.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@use PostBoundWitnessesResult(0, arrayListOf(Error(response.message)))
                    }
                    return@use PostBoundWitnessesResult(1)
                }
            } catch (ex: IOException) {
                Log.e("xyoClient", ex.message ?: ex.toString())
                return@withContext PostBoundWitnessesResult(0, arrayListOf(Error(ex.message)))
            }
        }
    }

    suspend fun postBoundWitnessAsync(
        entry: XyoBoundWitnessJson
    ): PostBoundWitnessesResult {
        return postBoundWitnessesAsync(arrayOf(entry))
    }

    companion object {
        val MEDIA_TYPE_JSON = "text/json; charset=utf-8".toMediaType()
        fun get(config: XyoArchivistApiConfig): XyoArchivistApiClient {
            return XyoArchivistApiClient(config)
        }
    }
}