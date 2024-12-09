package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.account.model.Account
import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoValidationException
import network.xyo.client.types.Hash

@OptIn(ExperimentalStdlibApi::class)
@RequiresApi(Build.VERSION_CODES.M)
open class BoundWitnessBuilder {
    protected var _signers = mutableListOf<Account>()
    protected var _payload_hashes = mutableListOf<String>()
    protected var _payload_schemas = mutableListOf<String>()
    protected var _payloads = mutableListOf<Payload>()
    protected open var bw: BoundWitness = BoundWitness()

    var _timestamp: Long? = null

    @OptIn(ExperimentalStdlibApi::class)
    val addresses: List<String>
        get() = _signers.map { witness -> witness.address.toHexString() }

    open fun signers(signers: List<Account>): BoundWitnessBuilder {
        signers.forEach { signer -> signer(signer) }
        return this
    }

    open fun signer(signer: Account): BoundWitnessBuilder {
        _signers.add(signer)
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
        payloads.forEach {
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

        // update underscore fields
        bw._meta.client = "android"

        // construct fields involved in hashing
        constructHashableFieldsFields()
    }

    private suspend fun constructHashableFieldsFields() {
        // Note: Once fields are hashed, do not update class properties that are expected
        // in the serialized version of the bw because they will invalidate the hash
        setPreviousHashes()
        val dataHash = bw.dataHash()
        bw._meta.signatures = this.sign(dataHash)
    }

    open suspend fun build(): BoundWitness {
        return bw.let{
            // update fields
            constructFields()
            it
        }
    }
}