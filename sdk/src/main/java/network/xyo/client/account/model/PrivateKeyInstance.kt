package network.xyo.client.account.model

interface PrivateKeyInstance: PublicKeyInstance {
    suspend fun sign(hash: ByteArray): ByteArray
}