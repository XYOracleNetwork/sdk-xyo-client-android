package network.xyo.client.address

class KeyPair(publicKeySource: ByteArray?) {
    val private = PrivateKey(publicKeySource)
    val public: PublicKey
        get() {
            return private.public
        }
}