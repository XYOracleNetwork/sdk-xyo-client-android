package network.xyo.client.address

import network.xyo.client.XyoSerializable
import org.spongycastle.jcajce.provider.digest.Keccak

class XyoInvalidSizeException : Exception()

open class XyoData(private val _size: Int, sourceBytes: ByteArray?) {

    private val _bytes = sourceBytes?.copyInto(ByteArray(_size), sourceBytes?.size - _size) ?: ByteArray(_size)

    private fun checkSize() {
        if (_bytes.size != _size) {
            throw XyoInvalidSizeException()
        }
    }

    val size: Int
        get() {
            checkSize()
            return _size
        }

    val hex: String
        get() {
            return XyoSerializable.bytesToHex(bytes)
        }

    val bytes: ByteArray
        get() {
            checkSize()
            return _bytes
        }

    val keccak256: ByteArray
        get () {
            checkSize()
            val keccak = Keccak.Digest256()
            keccak.update(bytes)
            return keccak.digest()
        }
}