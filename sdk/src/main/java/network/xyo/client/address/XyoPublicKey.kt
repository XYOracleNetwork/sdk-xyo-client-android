package network.xyo.client.address

class XyoPublicKey(bytes: ByteArray): XyoData(64, bytes) {
    val address: XyoAddressValue
        get() {
            return XyoAddressValue.addressFromAddressOrPublicKey(bytes)
        }
    fun verify(msg: ByteArray, signature: ByteArray) : Boolean {
        return address.verify(msg, signature)
    }
}