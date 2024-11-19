package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.XyoWitness
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.payload.XyoPayload


@RequiresApi(Build.VERSION_CODES.M)
class QueryBoundWitnessBuilder : XyoBoundWitnessBuilder() {
    private lateinit var queryHash: String

    fun query(query: XyoPayload): QueryBoundWitnessBuilder {
        this.queryHash = XyoSerializable.sha256String(query)
        this.payload(query.schema, query)
        return this
    }

    override fun witness(account: AccountInstance, previousHash: String?): QueryBoundWitnessBuilder {
        super.witness(account, previousHash)
        return this
    }

    override fun witnesses(witnesses: List<XyoWitness<XyoPayload>>): QueryBoundWitnessBuilder {
        super.witnesses(witnesses)
        return this
    }

    override suspend fun build(previousHash: String?): QueryBoundWitnessJson {
        bw = QueryBoundWitnessJson()
        // override to support additional properties for query bound witnesses
        return bw.let {
            val qbw = it as QueryBoundWitnessJson
            qbw.query = this.queryHash
            qbw._previous_hash = previousHash
            constructFields()
            it
        }
    }

}