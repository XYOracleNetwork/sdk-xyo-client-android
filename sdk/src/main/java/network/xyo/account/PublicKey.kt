package network.xyo.account

import org.spongycastle.math.ec.ECPoint

class PublicKey(sourceBytes: ByteArray): EllipticKey(64) {

    private val _bytes = sourceBytes.copyOfRange(sourceBytes.size - _size, sourceBytes.size)

    constructor(point: ECPoint): this(point.getEncoded(false))

    init {
        checkSize()
    }

    override val bytes: ByteArray
        get() {
            return _bytes
        }

    val address: AddressValue
        get() {
            return AddressValue.addressFromAddressOrPublicKey(bytes)
        }

    val point: ECPoint
        get() {
            return CURVE.curve.decodePoint(bytes)
        }

    fun verify(msg: ByteArray, signature: ByteArray) : Boolean {
        return false
    }
}