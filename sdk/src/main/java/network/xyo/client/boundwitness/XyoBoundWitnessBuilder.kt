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

    open fun hashableFields(): XyoBoundWitnessBodyJson {
        return XyoBoundWitnessBodyJson(
            _witnesses.map { witness -> witness.address.hex},
            _previous_hashes,
            _payload_hashes,
            _payload_schemas
        )
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

    protected fun  constructFields(bw: XyoBoundWitnessJson, previousHash: String?) {
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
    }

    open fun build(previousHash: String? = null): XyoBoundWitnessJson {
        val bw = XyoBoundWitnessJson().let{
            constructFields(it, previousHash)
            it
        }
        return bw
    }
}