package network.xyo.client.account.model

interface PrivateKeyInstance: PublicKeyInstance {
    fun sign(hash: ByteArray): ByteArray
}