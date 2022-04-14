package network.xyo.client.boundwitness

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import network.xyo.client.XyoWitness
import network.xyo.client.address.XyoAddress
import network.xyo.client.payload.XyoPayload
import network.xyo.client.payload.XyoValidationException

@RequiresApi(Build.VERSION_CODES.M)
class XyoBoundWitnessBuilder {
    private var _witnesses = mutableListOf<XyoAddress>()
    private var _previous_hashes = mutableListOf<String>()
    private var _payload_hashes = mutableListOf<String>()
    private var _payload_schemas = mutableListOf<String>()
    private var _payloads = mutableListOf<XyoPayload>()

    fun witness(address: XyoAddress, previousHash: String = ""): XyoBoundWitnessBuilder {
        _witnesses.add(address)
        _previous_hashes.add(previousHash)
        return this
    }

    fun witnesses(witnesses: List<XyoWitness<XyoPayload>>): XyoBoundWitnessBuilder {
        witnesses.forEach { witness -> _witnesses.add(witness.address) }
        witnesses.forEach { witness -> _previous_hashes.add(witness.previousHash) }
        return this
    }

    private fun hashableFields(): XyoBoundWitnessBodyJson {
        return XyoBoundWitnessBodyJson(
            _witnesses.map { witness -> witness.addressHex},
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
            val sig = XyoSerializable.bytesToHex(it.sign(hash))
            sig
        }
    }

    fun build(previousHash: String? = null): XyoBoundWitnessJson {
        val bw = XyoBoundWitnessJson()
        val hashable = hashableFields()
        val hash = XyoSerializable.sha256String(hashable)
        bw._previous_hash = previousHash
        bw._signatures = this.sign(hash)
        bw._hash = hash
        bw._client = "android"
        bw._payloads = _payloads
        bw.addresses = _witnesses.map { witness -> witness.addressHex}
        bw.payload_hashes = _payload_hashes
        bw.payload_schemas = _payload_schemas
        bw.previous_hashes = _previous_hashes
        return bw
    }
}