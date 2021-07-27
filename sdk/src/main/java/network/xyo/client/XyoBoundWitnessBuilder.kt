package network.xyo.client

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.address.XyoAddress
import java.security.MessageDigest


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
            _witnesses.map { witness -> witness.publicKey},
            _previous_hashes,
            _payload_hashes,
            _payload_schemas
        )
    }

    fun <T: XyoPayload>payload(schema: String, payload: T): XyoBoundWitnessBuilder {
        _payloads.add(payload)
        _payload_hashes.add(sha256(payload))
        _payload_schemas.add(schema)
        return this
    }

    fun payloads(payloads: List<XyoPayload>): XyoBoundWitnessBuilder {
        payloads.forEach {
            _payloads.add(it)
        }
        payloads.forEach {payload -> _payload_hashes.add(sha256(payload))}
        payloads.forEach {payload -> _payload_schemas.add(payload.schema)}
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
        val hash = sha256(hashable)
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
        fun sha256(obj: Any): String {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(obj.javaClass)
            val jsonString = adapter.toJson(obj)
            val md = MessageDigest.getInstance("SHA256")
            md.update(jsonString.encodeToByteArray())
            val bytes: ByteArray = md.digest()
            return bytesToHex(bytes)
        }

        private val hexArray = "0123456789abcdef".toCharArray()

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