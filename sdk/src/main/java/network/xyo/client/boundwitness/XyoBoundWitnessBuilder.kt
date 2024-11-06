package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.XyoWitness
import network.xyo.client.address.XyoAccount
import network.xyo.client.payload.XyoPayload
import network.xyo.client.payload.XyoValidationException

@RequiresApi(Build.VERSION_CODES.M)
open class XyoBoundWitnessBuilder {
    protected var _witnesses = mutableListOf<XyoAccount>()
    protected var _previous_hashes = mutableListOf<String?>()
    protected var _payload_hashes = mutableListOf<String>()
    protected var _payload_schemas = mutableListOf<String>()
    protected var _payloads = mutableListOf<XyoPayload>()
    var _timestamp: Long? = null

    val addresses: List<String>
        get() = _witnesses.map { witness -> witness.address.hex }

    open fun witness(account: XyoAccount, previousHash: String?): XyoBoundWitnessBuilder {
        _witnesses.add(account)
        _previous_hashes.add(previousHash)
        return this
    }

    open fun witnesses(witnesses: List<XyoWitness<XyoPayload>>): XyoBoundWitnessBuilder {
        witnesses.forEach { witness -> _witnesses.add(witness.address) }
        witnesses.forEach { witness -> _previous_hashes.add(witness.previousHash) }
        return this
    }

    open fun hashableFields(bw: XyoBoundWitnessJson): XyoBoundWitnessBodyJson {
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

    fun sign(hash: String): List<String> {
        return _witnesses.map {
            val sig = XyoSerializable.bytesToHex(it.private.sign(hash))
            sig
        }
    }

    protected fun constructFields(bw: XyoBoundWitnessJson) {
        // update json class properties
        bw.payload_hashes = _payload_hashes
        bw.payload_schemas = _payload_schemas
        bw.previous_hashes = _previous_hashes
        bw.addresses = addresses

        // update underscore fields
        bw._client = "android"
        bw._payloads = _payloads

        // construct fields involved in hashing
        constructHashableFieldsFields(bw)
    }

    private fun  constructHashableFieldsFields(bw: XyoBoundWitnessJson) {
        // Note: Once fields are hashed, do not update class properties that are expected
        // in the serialized version of the bw because they will invalidate the hash
        val hashable = hashableFields(bw)
        val hash = XyoSerializable.sha256String(hashable)
        bw._signatures = this.sign(hash)
        bw._hash = hash
    }

    open fun build(previousHash: String? = null): XyoBoundWitnessJson {
        val bw = XyoBoundWitnessJson().let{
            // store the previous hash on the class
            it._previous_hash = previousHash

            // update fields
            constructFields(it)
            constructHashableFieldsFields(it)
            it
        }
        return bw
    }
}