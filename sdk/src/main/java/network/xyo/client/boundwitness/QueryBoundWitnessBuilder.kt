package network.xyo.client.boundwitness

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.payload.XyoPayload


@RequiresApi(Build.VERSION_CODES.M)
class QueryBoundWitnessBuilder(context: Context) : XyoBoundWitnessBuilder(context) {
    private lateinit var queryHash: String

    fun query(query: XyoPayload): QueryBoundWitnessBuilder {
        this.queryHash = XyoSerializable.sha256String(query)
        this.payload(query.schema, query)
        return this
    }

    override fun signer(account: AccountInstance): QueryBoundWitnessBuilder {
        super.signer(account)
        return this
    }

    override fun signers(accounts: List<AccountInstance>): QueryBoundWitnessBuilder {
        super.signers(accounts)
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