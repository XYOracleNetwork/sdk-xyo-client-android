package network.xyo.chain.protocol.wrapper

/**
 * Wrapper for XYO/XL1 addresses with utility methods,
 * matching JS Address wrapper.
 */
class AddressWrapper(val address: String) {

    /** Get the address as a lowercase hex string. */
    val hex: String get() = address.lowercase()

    /** Get a shortened display version of the address. */
    val short: String
        get() {
            if (address.length <= 10) return address
            return "${address.take(6)}...${address.takeLast(4)}"
        }

    /** Check if two addresses are equal (case-insensitive). */
    fun equals(other: String): Boolean {
        return address.equals(other, ignoreCase = true)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AddressWrapper) return false
        return address.equals(other.address, ignoreCase = true)
    }

    override fun hashCode(): Int = address.lowercase().hashCode()

    override fun toString(): String = address

    companion object {
        fun of(address: String): AddressWrapper = AddressWrapper(address)
    }
}
