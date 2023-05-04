package network.xyo.client.address

import network.xyo.client.XyoSerializable
import org.spongycastle.jcajce.provider.digest.Keccak

class XyoInvalidSizeException : Exception()

abstract class XyoData(protected val _size: Int) {

    protected fun checkSize() {
        if (bytes.size != _size) {
            throw XyoInvalidSizeException()
        }
    }

    open val size: Int
        get() {
            return _size
        }

    open val hex: String
        get() {
            return XyoSerializable.bytesToHex(bytes)
        }

    abstract val bytes: ByteArray

    open val keccak256: XyoData
        get () {
            val keccak = Keccak.Digest256()
            keccak.update(bytes)
            return MemoryData(32, keccak.digest())
        }

    companion object {
        fun copyByteArrayWithLeadingPaddingOrTrim(src: ByteArray, size: Int): ByteArray {
            val dest = ByteArray(size)

            var srcStartIndex = 0
            if (src.size > dest.size){
                srcStartIndex = src.size - dest.size
            }

            var destOffset = 0
            if (src.size < dest.size){
                destOffset = dest.size - src.size
            }
            src.copyInto(dest, destOffset, srcStartIndex )

            return dest
        }
    }
}