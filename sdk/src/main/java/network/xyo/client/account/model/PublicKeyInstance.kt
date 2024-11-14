package network.xyo.client.account.model

interface PublicKeyInstance {
    val address: ByteArray
    fun verify(msg: ByteArray, signature: ByteArray): Boolean
}