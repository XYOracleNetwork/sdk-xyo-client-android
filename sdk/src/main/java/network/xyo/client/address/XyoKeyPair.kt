package network.xyo.client.address

class XyoKeyPair(publicKeySource: ByteArray?) {
    val private = XyoPrivateKey(publicKeySource)
    val public: XyoPublicKey
        get() {
            return private.public
        }
}