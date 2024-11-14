package network.xyo.client.account.model

import tech.figure.hdwallet.bip32.ExtKey
import tech.figure.hdwallet.bip39.DeterministicSeed
import tech.figure.hdwallet.bip39.MnemonicWords

interface WalletStatic<T: WalletInstance> {
    var previousHashStore: PreviousHashStore?
    fun fromExtendedKey(key: ExtKey): T
    fun fromMnemonic(mnemonic: MnemonicWords, path: String?): T
    fun fromMnemonic(mnemonic: String, path: String?): T
    fun fromSeed(seed: String): T
    fun fromSeed(seed: ByteArray): T
    fun fromSeed(seed: DeterministicSeed): T
}
