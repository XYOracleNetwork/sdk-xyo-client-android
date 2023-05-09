package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.address.Account
import network.xyo.client.payload.XyoPayload


class QueryBoundWitnessBuilder : XyoBoundWitnessBuilder() {
    private lateinit var queryHash: String

    override fun hashableFields(): QueryBoundWitnessBodyJson {
        return QueryBoundWitnessBodyJson(
            this._witnesses.map { witness -> witness.address.hex},
            this._previous_hashes,
            this._payload_hashes,
            this._payload_schemas,
            this.queryHash
        )
    }

    fun query(query: XyoPayload): QueryBoundWitnessBuilder {
        this.queryHash = XyoSerializable.sha256String(query)
        this.payload(query.schema, query)
        return this
    }

    override fun witness(account: Account, previousHash: String?): QueryBoundWitnessBuilder {
        _witnesses.add(account)
        _previous_hashes.add(previousHash)
        return this
    }

    override fun build(previousHash: String?): QueryBoundWitnessJson {
        val bw = QueryBoundWitnessJson().let {
            constructFields(it, previousHash)
            it.query = this.queryHash
            it
        }
        return bw
    }

}