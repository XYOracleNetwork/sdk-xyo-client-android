package network.xyo.client.address

class XyoAddressValue(address: ByteArray): XyoMemoryData(20, address) {

    constructor(address: XyoData): this(address.bytes)

    fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        return false
    }

    companion object {

        fun addressFromPublicKey(key: ByteArray): XyoAddressValue {
            return addressFromPublicKey(XyoMemoryData(64, key))
        }

        fun addressFromPublicKey(key: XyoData): XyoAddressValue {
            return XyoAddressValue(key.keccak256.bytes.sliceArray(12 until key.keccak256.size))
        }

        fun addressFromAddressOrPublicKey(bytes: ByteArray): XyoAddressValue {
            return addressFromAddressOrPublicKey(XyoMemoryData(bytes.size, bytes))
        }

        fun addressFromAddressOrPublicKey(bytes: XyoData): XyoAddressValue {
            return if (bytes.size == 20) XyoAddressValue(bytes) else addressFromPublicKey(bytes)
        }
    }
}