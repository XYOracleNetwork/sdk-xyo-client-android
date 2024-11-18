package network.xyo.client.account.model

interface PreviousHashStore {
    suspend fun getItem(address: ByteArray): ByteArray?
    suspend fun removeItem(address: ByteArray)
    suspend fun setItem(address: ByteArray, previousHash: ByteArray)
}