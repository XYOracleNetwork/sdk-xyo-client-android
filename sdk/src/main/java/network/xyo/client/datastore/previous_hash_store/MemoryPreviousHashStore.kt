import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.types.Address
import network.xyo.client.types.Hash

class MemoryPreviousHashStore : PreviousHashStore {
    private val store: MutableMap<Address, Hash> = mutableMapOf()

    /**
     * Retrieves the Hash associated with the given Address.
     *
     * @param address The address for which to retrieve the Hash.
     * @return The corresponding Hash if it exists, or null otherwise.
     */
    override suspend fun getItem(address: Address): Hash? {
        return store[address]
    }

    /**
     * Removes the Hash associated with the given Address.
     *
     * @param address The address for which to remove the Hash.
     */
    override suspend fun removeItem(address: Address) {
        store.remove(address)
    }

    /**
     * Associates the given Hash with the specified Address.
     *
     * @param address The address to associate with the Hash.
     * @param previousHash The Hash to associate with the address.
     */
    override suspend fun setItem(address: Address, previousHash: Hash) {
        store[address] = previousHash
    }
}