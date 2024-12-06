package network.xyo.client.node.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.boundwitness.BoundWitnessBody
import network.xyo.client.payload.Payload
import org.json.JSONArray
import org.json.JSONObject

open class QueryResponseWrapper(val rawResponse: String) {
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    var bwHash: String? = null
    var bw: BoundWitnessBody? = null
    var payloads: List<Payload>? = null

    private fun unwrap() {
        val response = JSONObject(rawResponse)
        val data = response.get("data") as JSONArray
        return splitTuple(data)
    }

    private fun splitTuple(tuple: JSONArray) {
        val wrapperBwString = tuple[0].toString()
        val wrapperBw = parseBW(wrapperBwString)
        if (wrapperBw !== null) {
            bwHash = wrapperBw.dataHash()
            bw = wrapperBw
        }

        val payloadsString = tuple[1].toString()
        payloads = parsePayloads(payloadsString)
    }

    protected open fun parseBW(bwString: String): BoundWitnessBody? {
        val bwAdapter = moshi.adapter(BoundWitnessBody::class.java)
        val bw = bwAdapter.fromJson(bwString)
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
    open fun parsePayloads(payloadsString: String): List<Payload>? {
        val type = Types.newParameterizedType(List::class.java, Payload::class.java)
        val payloadAdapter = moshi.adapter<List<Payload>>(type)
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