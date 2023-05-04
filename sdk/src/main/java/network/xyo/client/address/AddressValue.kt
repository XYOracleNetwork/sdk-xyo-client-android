package network.xyo.client.address

class AddressValue(address: ByteArray): MemoryData(20, address) {

    constructor(address: XyoData): this(address.bytes)

    fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        return false
    }

    companion object {

        fun addressFromPublicKey(key: ByteArray): AddressValue {
            return addressFromPublicKey(MemoryData(64, key))
        }

        fun addressFromPublicKey(key: XyoData): AddressValue {
            return AddressValue(key.keccak256.bytes.sliceArray(12 until key.keccak256.size))
        }

        fun addressFromAddressOrPublicKey(bytes: ByteArray): AddressValue {
            return addressFromAddressOrPublicKey(MemoryData(bytes.size, bytes))
        }

        fun addressFromAddressOrPublicKey(bytes: XyoData): AddressValue {
            return if (bytes.size == 20) AddressValue(bytes) else addressFromPublicKey(bytes)
        }
    }
}