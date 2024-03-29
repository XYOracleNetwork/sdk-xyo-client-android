package network.xyo.client.node.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.XyoSerializable
import network.xyo.client.boundwitness.XyoBoundWitnessBodyJson
import network.xyo.client.payload.XyoPayload
import org.json.JSONArray
import org.json.JSONObject

data class QueryResponse(val data: String) {}

open class QueryResponseWrapper(private val rawResponse: String) {
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    var bwHash: String? = null
    var bw: XyoBoundWitnessBodyJson? = null
    var payloads: List<XyoPayload>? = null

    private fun unwrap() {
        val response = JSONObject(rawResponse)
        val data = response.get("data") as JSONArray
        return splitTuple(data)
    }

    private fun splitTuple(tuple: JSONArray) {
        val bwString = tuple[0].toString()
        val payloadsString = tuple[1].toString()
        bw = parseBW(bwString)
        payloads = parsePayloads(payloadsString)
    }

    protected open fun parseBW(bwString: String): XyoBoundWitnessBodyJson? {
        val bwAdapter = moshi.adapter(XyoBoundWitnessBodyJson::class.java)
        val bw = bwAdapter.fromJson(bwString)
        if (bw !== null) {
            bwHash = XyoSerializable.sha256String(bw)
        }
        return bw
    }


    /**
     * Parse payloads
     *
     * Override to parse payloads into their specific types
     *
     * @param payloadsString
     * @return
     */
    open fun parsePayloads(payloadsString: String): List<XyoPayload>? {
        val type = Types.newParameterizedType(List::class.java, XyoPayload::class.java)
        val payloadAdapter = moshi.adapter<List<XyoPayload>>(type)
        return payloadAdapter.fromJson(payloadsString)
    }

    companion object {
        fun parse(rawResponse: String?): QueryResponseWrapper? {
            if (rawResponse != null) {
                val instance = QueryResponseWrapper(rawResponse)
                instance.unwrap()
                return instance
            }
            return null
        }
    }
}