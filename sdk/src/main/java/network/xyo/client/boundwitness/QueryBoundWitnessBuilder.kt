package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.address.XyoAccount
import network.xyo.client.payload.XyoPayload


@RequiresApi(Build.VERSION_CODES.M)
class QueryBoundWitnessBuilder : XyoBoundWitnessBuilder() {
    private lateinit var queryHash: String

    override fun hashableFields(bw: XyoBoundWitnessJson): XyoBoundWitnessBodyJson {
        val timestamp = this._timestamp ?: System.currentTimeMillis()

        // return a bound witness json body that has query in its hashable fields
        return QueryBoundWitnessBodyJson(
            this._witnesses.map { witness -> witness.address.hex},
            this._previous_hashes,
            this._payload_hashes,
            this._payload_schemas,
            this.queryHash,
            timestamp,
        )
    }

    fun query(query: XyoPayload): QueryBoundWitnessBuilder {
        this.queryHash = XyoSerializable.sha256String(query)
        this.payload(query.schema, query)
        return this
    }

    override fun witness(account: XyoAccount, previousHash: String?): QueryBoundWitnessBuilder {
        _witnesses.add(account)
        _previous_hashes.add(previousHash)
        return this
    }

    override fun build(previousHash: String?): QueryBoundWitnessJson {
        // override to support additional properties for query bound witnesses
        return QueryBoundWitnessJson().let {
            it.query = this.queryHash
            it._previous_hash = previousHash
            constructFields(it)
            it
        }
    }

}