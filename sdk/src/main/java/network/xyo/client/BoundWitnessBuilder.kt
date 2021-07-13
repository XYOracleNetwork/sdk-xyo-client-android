package network.xyo.client

import com.google.gson.Gson
import network.xyo.client.address.XyoAddress
import java.security.MessageDigest

class BoundWitnessBuilder {
    private var _witnesses = emptyList<XyoAddress>()
    private var _previous_hashes = emptyList<String?>()
    private var _payload_hashes = emptyList<String>()
    private var _payload_schemas = emptyList<String>()
    private var _payloads = emptyList<XyoPayload>()

    fun witness(address: XyoAddress, previousHash: String? = null): BoundWitnessBuilder {
        _witnesses.plus(address)
        _previous_hashes.plus(previousHash)
        return this
    }

    fun witnesses(witnesses: List<XyoWitness<XyoPayload>>): BoundWitnessBuilder {
        _witnesses.plus(witnesses.map { witness -> witness.address })
        _previous_hashes.plus(witnesses.map { witness -> witness.previousHash })
        return this
    }

    private fun hashableFields(): XyoBoundWitnessBodyJson {
        return XyoBoundWitnessBodyJson(
            _witnesses.map { witness -> witness.publicKey},
            _previous_hashes,
            _payload_hashes,
            _payload_schemas
        )
    }

    fun <T: XyoPayload>payload(schema: String, payload: T): BoundWitnessBuilder {
        _payloads.plus(payload)
        _payload_hashes.plus(BoundWitnessBuilder.hash(payload))
        _payload_schemas.plus(schema)
        return this
    }

    fun payloads(payloads: List<XyoPayload>): BoundWitnessBuilder {
        _payloads.plus(payloads)
        _payload_hashes.plus(payloads.map {payload -> payload.sha256()})
        _payload_schemas.plus(payloads.map {payload -> payload.schema})
        return this
    }

    fun sign(hash: String): List<String> {
        return _witnesses.map {
            bytesToHex(it.sign(hash))
        }
    }

    fun build(): XyoBoundWitnessJson {
        val bw = XyoBoundWitnessJson()
        val hashable = hashableFields()
        val hash = hash(hashable)
        bw._signatures = this.sign(hash)
        bw._hash = hash
        bw._client = "kotlin"
        bw._payloads = _payloads
        bw.addresses = _witnesses.map { witness -> witness.publicKey}
        bw.previous_hashes = _previous_hashes
        bw.payload_hashes = _payload_hashes
        bw.payload_schemas = _payload_schemas
        return bw
    }

    companion object {
        fun <T>hash(json: T): String {
            val jsonString = Gson().toJson(json)
            val md = MessageDigest.getInstance("SHA")
            md.update(jsonString.encodeToByteArray())
            return md.digest().toString()
        }

        private val hexArray = "0123456789ABCDEF".toCharArray()

        fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF

                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }
}