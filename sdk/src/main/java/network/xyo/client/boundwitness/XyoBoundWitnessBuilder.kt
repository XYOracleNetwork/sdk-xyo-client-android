package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.lib.JsonSerializable
import network.xyo.client.account.hexStringToByteArray
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.payload.Payload
import network.xyo.client.payload.XyoValidationException

@RequiresApi(Build.VERSION_CODES.M)
open class BoundWitnessBuilder {
    protected var _signers = mutableListOf<AccountInstance>()
    protected var _payload_hashes = mutableListOf<String>()
    protected var _payload_schemas = mutableListOf<String>()
    protected var _payloads = mutableListOf<Payload>()
    protected open var bw: BoundWitnessJson = BoundWitnessJson()

    var _timestamp: Long? = null

    @OptIn(ExperimentalStdlibApi::class)
    val addresses: List<String>
        get() = _signers.map { witness -> witness.address.toHexString() }

    open fun signers(signers: List<AccountInstance>): BoundWitnessBuilder {
        signers.forEach { signer -> signer(signer) }
        return this
    }

    open fun signer(signer: AccountInstance): BoundWitnessBuilder {
        _signers.add(signer)
        return this
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun hashableFields(): BoundWitnessBodyJson {
        // if a timestamp is not provided, set one at the time hashable fields are set
        bw.timestamp = _timestamp ?: System.currentTimeMillis()

        bw.previous_hashes = _signers.map {
            signer -> signer.previousHash?.toHexString()
        }

        // return the body with hashable fields
        return bw.getBodyJson()
    }

    @Throws(XyoValidationException::class)
    fun <T: Payload>payload(schema: String, payload: T): BoundWitnessBuilder {
        payload.validate()
        _payloads.add(payload)
        _payload_hashes.add(payload.dataHash())
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

    private suspend fun sign(hash: String): List<String> {
        return _signers.map {
            val sig = JsonSerializable.bytesToHex(it.sign(hexStringToByteArray(hash)))
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
        bw.meta.client = "android"

        // construct fields involved in hashing
        constructHashableFieldsFields()
    }

    private suspend fun constructHashableFieldsFields() {
        // Note: Once fields are hashed, do not update class properties that are expected
        // in the serialized version of the bw because they will invalidate the hash
        val hashable = hashableFields()
        val hash = hashable.dataHash()
        bw.meta.signatures = this.sign(hash)
    }

    open suspend fun build(): BoundWitnessJson {
        return bw.let{
            // update fields
            constructFields()
            it
        }
    }
}