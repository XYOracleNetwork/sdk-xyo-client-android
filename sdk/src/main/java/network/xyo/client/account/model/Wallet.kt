package network.xyo.client.account.model

/**
 * Backward-compatible alias for [WalletInstance].
 *
 * New code should prefer [WalletInstance] to match JS SDK naming.
 */
interface Wallet : Account, WalletInstance {
    override fun derivePath(path: String): Wallet
}
