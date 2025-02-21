package network.xyo.client.account

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.account.model.Wallet
import network.xyo.client.account.model.WalletStatic
import network.xyo.client.lib.hexStringToByteArray
import tech.figure.hdwallet.bip32.ExtKey
import tech.figure.hdwallet.bip32.toRootKey
import tech.figure.hdwallet.bip39.DeterministicSeed
import tech.figure.hdwallet.bip39.MnemonicWords
import tech.figure.hdwallet.ec.extensions.toBytesPadded

@RequiresApi(Build.VERSION_CODES.M)
open class Wallet(private val _extKey: ExtKey, previousHash: ByteArray? = null):
    Account(_extKey.keyPair.privateKey.key.toBytesPadded(32), previousHash),
    Wallet {

    override fun derivePath(path: String): Wallet {
        return Wallet(_extKey.childKey(path))
    }

    companion object: WalletStatic<Wallet> {

        val defaultPath = "m/44'/60'/0'/0/0"

        override var previousHashStore: PreviousHashStore? = null

        override fun fromExtendedKey(key: ExtKey): Wallet {
            return Wallet(key)
        }

        override fun fromMnemonic(mnemonic: String, path: String?): Wallet {
            return fromMnemonic(MnemonicWords.of(mnemonic), path)
        }

        override fun fromMnemonic(mnemonic: MnemonicWords, path: String?): Wallet {
            val root = fromSeed(mnemonic.toSeed("".toCharArray()))
            return if (path === null) {
                root.derivePath(defaultPath)
            } else {
                root.derivePath(path)
            }
        }

        override fun fromSeed(seed: String): Wallet {
            return fromSeed(hexStringToByteArray(seed))
        }

        override fun fromSeed(seed: ByteArray): Wallet {
            return fromSeed(DeterministicSeed.fromBytes(seed))
        }

        override fun fromSeed(seed: DeterministicSeed): Wallet {
            val key = seed.toRootKey()
            return Wallet(key)
        }
    }
}

