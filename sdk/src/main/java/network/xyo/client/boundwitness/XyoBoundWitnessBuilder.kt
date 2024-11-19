package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.XyoWitness
import network.xyo.client.account.hexStringToByteArray
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.payload.XyoPayload
import network.xyo.client.payload.XyoValidationException

@RequiresApi(Build.VERSION_CODES.M)
open class XyoBoundWitnessBuilder {
    protected var _witnesses = mutableListOf<AccountInstance>()
    protected var _previous_hashes = mutableListOf<String?>()
    protected var _payload_hashes = mutableListOf<String>()
    protected var _payload_schemas = mutableListOf<String>()
    protected var _payloads = mutableListOf<XyoPayload>()
    protected open var bw: XyoBoundWitnessJson = XyoBoundWitnessJson()

    var _timestamp: Long? = null

    @OptIn(ExperimentalStdlibApi::class)
    val addresses: List<String>
        get() = _witnesses.map { witness -> witness.address.toHexString() }

    open fun witness(account: AccountInstance, previousHash: String?): XyoBoundWitnessBuilder {
        _witnesses.add(account)
        _previous_hashes.add(previousHash)
        return this
    }

    open fun witnesses(witnesses: List<XyoWitness<XyoPayload>>): XyoBoundWitnessBuilder {
        witnesses.forEach { witness -> _witnesses.add(witness.address) }
        witnesses.forEach { witness -> _previous_hashes.add(witness.previousHash) }
        return this
    }

    private fun hashableFields(): XyoBoundWitnessBodyJson {
        // if a timestamp is not provided, set one at the time hashable fields are set
        bw.timestamp = _timestamp ?: System.currentTimeMillis()

        // return the body with hashable fields
        return bw.getBodyJson()
    }

    @Throws(XyoValidationException::class)
    fun <T: XyoPayload>payload(schema: String, payload: T): XyoBoundWitnessBuilder {
        payload.validate()
        _payloads.add(payload)
        _payload_hashes.add(XyoSerializable.sha256String(payload))
        _payload_schemas.add(schema)
        return this
    }

    @Throws(XyoValidationException::class)
    fun payloads(payloads: List<XyoPayload>): XyoBoundWitnessBuilder {
        payloads.forEach {
            payload(it.schema, it)
        }
        return this
    }

    private suspend fun sign(hash: String): List<String> {
        return _witnesses.map {
            val sig = XyoSerializable.bytesToHex(it.sign(hexStringToByteArray(hash)))
            sig
        }
    }

    protected suspend fun constructFields() {
        // update json class properties
        bw.payload_hashes = _payload_hashes
        bw.payload_schemas = _payload_schemas
        bw.previous_hashes = _previous_hashes
        bw.addresses = addresses

        // update underscore fields
        bw._client = "android"

        // construct fields involved in hashing
        constructHashableFieldsFields()
    }

    private suspend fun constructHashableFieldsFields() {
        // Note: Once fields are hashed, do not update class properties that are expected
        // in the serialized version of the bw because they will invalidate the hash
        val hashable = hashableFields()
        val hash = XyoSerializable.sha256String(hashable)
        bw._signatures = this.sign(hash)
        bw._hash = hash
    }

    open suspend fun build(previousHash: String? = null): XyoBoundWitnessJson {
        return bw.let{
            // store the previous hash on the class
            it._previous_hash = previousHash

            // update fields
            constructFields()
            it
        }
    }
}