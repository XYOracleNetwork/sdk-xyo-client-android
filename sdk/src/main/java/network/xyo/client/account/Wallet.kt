package network.xyo.client.account

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.account.model.WalletInstance
import network.xyo.client.account.model.WalletStatic
import network.xyo.client.address.XyoAccount
import tech.figure.hdwallet.bip32.ExtKey
import tech.figure.hdwallet.bip32.toRootKey
import tech.figure.hdwallet.bip39.DeterministicSeed
import tech.figure.hdwallet.bip39.MnemonicWords
import tech.figure.hdwallet.ec.extensions.toBytesPadded

@RequiresApi(Build.VERSION_CODES.M)
open class Wallet(private val _extKey: ExtKey, previousHash: ByteArray? = null):
    Account(_extKey.keyPair.privateKey.key.toBytesPadded(32), previousHash), WalletInstance {

    override fun derivePath(path: String): WalletInstance {
        return Wallet(_extKey.childKey(path))
    }

    companion object: WalletStatic<WalletInstance> {

        override var previousHashStore: PreviousHashStore? = null

        override fun fromExtendedKey(key: ExtKey): WalletInstance {
            return Wallet(key)
        }

        override fun fromMnemonic(mnemonic: String, path: String?): WalletInstance {
            return fromMnemonic(MnemonicWords.of(mnemonic), path)
        }

        override fun fromMnemonic(mnemonic: MnemonicWords, path: String?): WalletInstance {
            val root = fromSeed(mnemonic.toSeed("".toCharArray()))
            return if (path === null) {
                root
            } else {
                root.derivePath(path)
            }
        }

        override fun fromSeed(seed: String): WalletInstance {
            return fromSeed(hexStringToByteArray(seed))
        }

        override fun fromSeed(seed: ByteArray): WalletInstance {
            return fromSeed(DeterministicSeed.fromBytes(seed))
        }

        override fun fromSeed(seed: DeterministicSeed): WalletInstance {
            val key = seed.toRootKey()
            return Wallet(key)
        }
    }
}

