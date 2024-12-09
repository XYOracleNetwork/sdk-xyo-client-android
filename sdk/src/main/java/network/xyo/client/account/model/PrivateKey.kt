package network.xyo.client.account.model

interface PrivateKey: PublicKey {
    suspend fun sign(hash: ByteArray): ByteArray
}