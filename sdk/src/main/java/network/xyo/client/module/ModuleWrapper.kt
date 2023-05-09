package network.xyo.client.module

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import network.xyo.client.XyoSerializable
import network.xyo.client.address.Account
import network.xyo.client.boundwitness.QueryBoundWitnessBuilder
import network.xyo.client.boundwitness.QueryBoundWitnessJson
import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoPayload
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject

class ModuleWrapper<TConfig: ModuleConfig, TParams: ModuleParams<TConfig>, TModule: Module<TConfig, TParams>>(val module: TModule, val params: TParams) {
    val account: Account
        get() {
            return this.params.account
        }

    val address: String
        get() {
            return this.module.address
        }

    val config: TConfig
        get() {
            return this.params.config
        }

    val queries: List<String>
        get() {
            return this.module.queries
        }

    @ExperimentalCoroutinesApi
    suspend fun query(query: QueryBoundWitness, payloads: List<Payload>?): ModuleQueryResult {
        return this.module.query(query, payloads)
    }

    private fun buildQuery(query: XyoPayload, payloads: List<XyoPayload>?, previousHash: String?): String {
        val builtQuery = queryBuilder(query, payloads, previousHash)
        val queryPayloads = buildQueryPayloads(query, payloads)
        val queryPayloadsJsonArray = queryPayloadsJsonArray(queryPayloads)
        val builtQueryTuple = arrayListOf(XyoSerializable.toJson((builtQuery)), queryPayloadsJsonArray.toString())
        return builtQueryTuple.joinToString(",", "[", "]")
    }

    private fun queryBuilder(query: XyoPayload, payloads: List<XyoPayload>?, previousHash: String?): QueryBoundWitnessJson {
        return QueryBoundWitnessBuilder().let {
            payloads?.let { payload ->
                it.payloads(payload)
            }
            it.witness(this.account, previousHash).query(query).build(previousHash)

        }
    }

    // combine payloads and query
    private fun buildQueryPayloads(query: XyoPayload, payloads: List<XyoPayload>?): List<XyoPayload>{
        return arrayListOf<XyoPayload>().let { queryPayloads ->
            payloads?.let { payloads ->
                payloads.forEach { payload ->
                    queryPayloads.add(payload)
                }
            }
            queryPayloads.add(query)
            queryPayloads
        }
    }

    // stringify combined payloads
    private fun queryPayloadsJsonArray(payloads: List<XyoPayload>): JSONArray {
        return JSONArray().apply {
            payloads.forEach { payload ->
                val serializedPayload = XyoSerializable.toJson(payload)
                val obj = JSONObject(serializedPayload)
                this.put(obj)
            }
        }
    }

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }
}