package network.xyo.client.account.model

interface WalletInstance: AccountInstance {
    fun derivePath(path: String): WalletInstance
}
