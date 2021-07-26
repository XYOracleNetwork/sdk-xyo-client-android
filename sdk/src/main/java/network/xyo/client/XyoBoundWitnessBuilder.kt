package network.xyo.client

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import network.xyo.client.address.XyoAddress
import java.security.MessageDigest


class XyoBoundWitnessBuilder {
    private var _witnesses = emptyList<XyoAddress>()
    private var _previous_hashes = emptyList<String?>()
    private var _payload_hashes = emptyList<String>()
    private var _payload_schemas = emptyList<String>()
    private var _payloads = emptyList<XyoPayload>()

    fun witness(address: XyoAddress, previousHash: String? = null): XyoBoundWitnessBuilder {
        _witnesses.plus(address)
        _previous_hashes.plus(previousHash)
        return this
    }

    fun witnesses(witnesses: List<XyoWitness<XyoPayload>>): XyoBoundWitnessBuilder {
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

    fun <T: XyoPayload>payload(schema: String, payload: T): XyoBoundWitnessBuilder {
        _payloads.plus(payload)
        _payload_hashes.plus(XyoBoundWitnessBuilder.hash(payload))
        _payload_schemas.plus(schema)
        return this
    }

    fun payloads(payloads: List<XyoPayload>): XyoBoundWitnessBuilder {
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
        fun hash(obj: Any): String {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(obj.javaClass)
            val jsonString = adapter.toJson(obj)
            Log.d("jsonString", jsonString)
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