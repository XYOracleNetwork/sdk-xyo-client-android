package network.xyo.client.account.model

interface AccountStatic<T: AccountInstance> {
    var previousHashStore: PreviousHashStore?
    fun fromPrivateKey(key: ByteArray): T
    fun random(): T
}