package network.xyo.client.address

import org.spongycastle.math.ec.ECPoint

class XyoPublicKey(sourceBytes: ByteArray): XyoEllipticKey(64) {

    private val _bytes = sourceBytes.copyOfRange(sourceBytes.size - _size, sourceBytes.size)

    constructor(point: ECPoint): this(point.getEncoded(false))

    init {
        checkSize()
    }

    override val bytes: ByteArray
        get() {
            return _bytes
        }

    val address: XyoAddressValue
        get() {
            return XyoAddressValue.addressFromAddressOrPublicKey(bytes)
        }

    val point: ECPoint
        get() {
            return CURVE.curve.decodePoint(bytes)
        }

    fun verify(msg: ByteArray, signature: ByteArray) : Boolean {
        return false
    }
}