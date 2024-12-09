package network.xyo.client.account.model

interface Wallet: Account {
    fun derivePath(path: String): Wallet
}
