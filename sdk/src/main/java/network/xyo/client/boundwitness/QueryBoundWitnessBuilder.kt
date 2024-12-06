package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.payload.Payload


@RequiresApi(Build.VERSION_CODES.M)
class QueryBoundWitnessBuilder : BoundWitnessBuilder() {
    private lateinit var queryHash: String

    fun query(query: Payload): QueryBoundWitnessBuilder {
        this.queryHash = query.dataHash()
        this.payload(query.schema, query)
        return this
    }

    override fun signer(signer: AccountInstance): QueryBoundWitnessBuilder {
        super.signer(signer)
        return this
    }

    override fun signers(signers: List<AccountInstance>): QueryBoundWitnessBuilder {
        super.signers(signers)
        return this
    }

    override suspend fun build(): QueryBoundWitness {
        bw = QueryBoundWitness()
        // override to support additional properties for query bound witnesses
        return bw.let {
            val qbw = it as QueryBoundWitness
            qbw.query = this.queryHash
            constructFields()
            it
        }
    }

}