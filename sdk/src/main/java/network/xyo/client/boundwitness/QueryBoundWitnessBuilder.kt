package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.Account
import network.xyo.client.payload.Payload
import network.xyo.client.types.Hash


@OptIn(ExperimentalStdlibApi::class)
@RequiresApi(Build.VERSION_CODES.M)
class QueryBoundWitnessBuilder : BoundWitnessBuilder() {
    private lateinit var queryHash: Hash

    fun query(query: Payload): QueryBoundWitnessBuilder {
        this.queryHash = query.dataHash()
        this.payload(query.schema, query)
        return this
    }

    override fun signer(signer: Account): QueryBoundWitnessBuilder {
        super.signer(signer)
        return this
    }

    override fun signers(signers: List<Account>): QueryBoundWitnessBuilder {
        super.signers(signers)
        return this
    }

    override suspend fun build(): QueryBoundWitness {
        bw = QueryBoundWitness()
        // override to support additional properties for query bound witnesses
        return bw.let {
            val qbw = it as QueryBoundWitness
            qbw.query = this.queryHash.toHexString()
            constructFields()
            it
        }
    }

}