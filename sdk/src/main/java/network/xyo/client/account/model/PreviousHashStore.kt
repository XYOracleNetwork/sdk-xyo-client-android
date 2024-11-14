package network.xyo.client.account.model

interface PreviousHashStore {
    fun getItem(address: ByteArray): ByteArray?
    fun removeItem(address: ByteArray)
    fun setItem(address: ByteArray, previousHash: ByteArray)
}