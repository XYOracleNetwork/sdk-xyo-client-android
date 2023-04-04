package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.XyoWitness
import network.xyo.client.address.XyoAccount
import network.xyo.client.payload.XyoPayload


@RequiresApi(Build.VERSION_CODES.M)
class QueryBoundWitnessBuilder() : XyoBoundWitnessBuilder() {
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
        this.payload("network.xyo.boundwitness", query)
        return this
    }

    override fun witness(account: XyoAccount, previousHash: String): QueryBoundWitnessBuilder {
        _witnesses.add(account)
        _previous_hashes.add(previousHash)
        return this
    }

    override fun build(previousHash: String?): QueryBoundWitnessJson {
        val bw = QueryBoundWitnessJson()
        val hashable = hashableFields()
        val hash = XyoSerializable.sha256String(hashable)
        bw._previous_hash = previousHash
        bw._signatures = this.sign(hash)
        bw._hash = hash
        bw._client = "android"
        bw._payloads = _payloads
        bw.addresses = _witnesses.map { witness -> witness.address.hex}
        bw.payload_hashes = _payload_hashes
        bw.payload_schemas = _payload_schemas
        bw.previous_hashes = _previous_hashes
        bw.query = this.queryHash
        return bw
    }

}