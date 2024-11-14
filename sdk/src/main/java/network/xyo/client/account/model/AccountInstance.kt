package network.xyo.client.account.model

interface AccountInstance: PrivateKeyInstance {
    val previousHash: ByteArray?
    val privateKey: ByteArray
    val publicKey: ByteArray
}