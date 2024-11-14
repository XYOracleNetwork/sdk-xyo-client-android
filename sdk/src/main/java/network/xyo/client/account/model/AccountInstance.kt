package network.xyo.client.account.model

interface AccountInstance {
    val address: ByteArray
    val previousHash: ByteArray?
    val privateKey: ByteArray
    val publicKey: ByteArray
    fun sign(hash: ByteArray): ByteArray
    fun verify(msg: ByteArray, signature: ByteArray): Boolean
}