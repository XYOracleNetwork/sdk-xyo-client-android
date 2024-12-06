package network.xyo.client.account.model

import network.xyo.client.types.Address
import network.xyo.client.types.Hash

interface PreviousHashStore {
    suspend fun getItem(address: Address): Hash?
    suspend fun removeItem(address: Address)
    suspend fun setItem(address: Address, previousHash: Hash)
}