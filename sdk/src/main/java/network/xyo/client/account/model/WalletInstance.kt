package network.xyo.client.account.model

/**
 * HD Wallet interface matching JS WalletInstance.
 *
 * Extends AccountInstance with hierarchical deterministic key derivation.
 * This is the primary interface name matching the JS SDK.
 */
interface WalletInstance : AccountInstance {
    fun derivePath(path: String): WalletInstance
}
