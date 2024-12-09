package network.xyo.client.account.model

interface PublicKey {
    val address: ByteArray
    fun verify(msg: ByteArray, signature: ByteArray): Boolean
}