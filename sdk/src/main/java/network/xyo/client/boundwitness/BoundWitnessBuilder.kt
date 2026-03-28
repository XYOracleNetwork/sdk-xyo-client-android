package network.xyo.client.boundwitness

import network.xyo.client.lib.JsonSerializable
import network.xyo.client.account.model.Account
import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoValidationException
import network.xyo.client.types.Hash

@OptIn(ExperimentalStdlibApi::class)
open class BoundWitnessBuilder {
    protected var _signers = mutableListOf<Account>()
    protected var _payload_hashes = mutableListOf<String>()
    protected var _payload_schemas = mutableListOf<String>()
    protected var _payloads = mutableListOf<Payload>()
    protected open var bw: BoundWitness = BoundWitness()

    var _timestamp: Long? = null
    protected var _sourceQuery: String? = null
    protected var _destination: String? = null

    @OptIn(ExperimentalStdlibApi::class)
    val addresses: List<String>
        get() = _signers.map { witness -> witness.address.toHexString() }

    open fun signers(signers: List<Account>): BoundWitnessBuilder {
        for (signer in signers) { signer(signer) }
        return this
    }

    open fun signer(signer: Account): BoundWitnessBuilder {
        _signers.add(signer)
        return this
    }

    /** Set the $sourceQuery field (hash of the originating query). */
    fun sourceQuery(sourceQuery: String): BoundWitnessBuilder {
        _sourceQuery = sourceQuery
        return this
    }

    /** Set the $destination field (target address for directed messages). */
    fun destination(destination: String): BoundWitnessBuilder {
        _destination = destination
        return this
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun setPreviousHashes() {
        bw.previous_hashes = _signers.map {
            signer -> signer.previousHash?.toHexString()
        }
    }

    @Throws(XyoValidationException::class)
    fun <T: Payload>payload(schema: String, payload: T): BoundWitnessBuilder {
        payload.validate()
        _payloads.add(payload)
        _payload_hashes.add(payload.hash().toHexString())
        _payload_schemas.add(schema)
        return this
    }

    @Throws(XyoValidationException::class)
    fun payloads(payloads: List<Payload>): BoundWitnessBuilder {
        for (it in payloads) {
            payload(it.schema, it)
        }
        return this
    }

    private suspend fun sign(hash: Hash): List<String> {
        return _signers.map {
            val sig = JsonSerializable.bytesToHex(it.sign(hash))
            sig
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    protected suspend fun constructFields() {
        // update json class properties
        bw.payload_hashes = _payload_hashes
        bw.payload_schemas = _payload_schemas
        bw.previous_hashes = _signers.map {account -> account.previousHash?.toHexString()}
        bw.addresses = addresses

        // set client metadata ($-prefixed fields, not included in dataHash)
        bw.__sourceQuery = _sourceQuery
        bw.__destination = _destination

        // construct fields involved in hashing
        constructHashableFieldsFields()
    }

    private suspend fun constructHashableFieldsFields() {
        // Note: Once fields are hashed, do not update class properties that are expected
        // in the serialized version of the bw because they will invalidate the hash
        setPreviousHashes()
        val dataHash = bw.dataHash()
        bw.__signatures = this.sign(dataHash)
    }

    open suspend fun build(): Pair<BoundWitness, List<Payload>> {
        return bw.let {
            // update fields
            constructFields()
            Pair(it, _payloads.toList())
        }
    }
}