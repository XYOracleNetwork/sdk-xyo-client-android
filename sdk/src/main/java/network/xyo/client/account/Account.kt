package network.xyo.client.account

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.Account
import network.xyo.client.account.model.AccountStatic
import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.lib.Secp256k1CurveConstants
import network.xyo.client.lib.hexStringToByteArray
import network.xyo.client.lib.publicKeyToAddress
import network.xyo.client.lib.recoverPublicKey
import org.spongycastle.jcajce.provider.digest.Keccak
import tech.figure.hdwallet.ec.PrivateKey
import tech.figure.hdwallet.ec.extensions.toBytesPadded
import tech.figure.hdwallet.ec.secp256k1Curve
import tech.figure.hdwallet.signer.BCECSigner
import java.math.BigInteger
import java.security.SecureRandom

open class Account private constructor (private val _privateKey: PrivateKey, private var _previousHash: ByteArray? = null):
    Account {

    constructor(privateKey: ByteArray, previousHash: ByteArray? = null) : this(PrivateKey.fromBytes(privateKey, secp256k1Curve), previousHash)
    constructor(privateKey: BigInteger, previousHash: ByteArray? = null) : this(privateKey.toByteArray(), previousHash)

    private val _address = addressFromUncompressedPublicKey(publicKeyUncompressed)

    final override val address: ByteArray
        get() = _address
    final override val previousHash: ByteArray?
        get() = _previousHash
    final override val privateKey: ByteArray
        get() = _privateKey.key.toBytesPadded(32)
    final override val publicKey: ByteArray
        get() = _privateKey.toPublicKey().compressed()
    final override val publicKeyUncompressed: ByteArray
        get() = _privateKey.toPublicKey().key.toBytesPadded(64)

    override suspend fun sign(hash: ByteArray): ByteArray {
        val result = BCECSigner().sign(_privateKey, hash)
        _previousHash = hash
        previousHashStore?.setItem(address, hash)
        return result.encodeAsBTC().toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        val recoveredPublicKey = recoverPublicKey(msg, signature) ?: return false
        val recoveredAddress = publicKeyToAddress(recoveredPublicKey)
        return recoveredAddress.contentEquals(address)
    }

    companion object: AccountStatic<Account> {
        override var previousHashStore: PreviousHashStore? = null

        override fun fromPrivateKey(key: ByteArray): Account {
            return Account(key)
        }

        override fun fromPrivateKey(key: String): Account {
            return fromPrivateKey(hexStringToByteArray(key))
        }

        override fun random(): Account {
            return fromPrivateKey(generatePrivateKeyBytes())
        }

        fun addressFromUncompressedPublicKey(key: ByteArray): ByteArray {
            assert(key.size == 64, ) { "Invalid Key Length" }
            val publicKeyHash = toKeccak(key)
            return publicKeyHash.copyOfRange(12, publicKeyHash.size)
        }

        private fun toKeccak(bytes: ByteArray): ByteArray {
            val keccak = Keccak.Digest256()
            keccak.update(bytes)
            return keccak.digest()
        }

        private fun generatePrivateKeyBytes(): ByteArray {
            val secureRandom = SecureRandom()
            val private = ByteArray(32)
            secureRandom.nextBytes(private)
            //this line is to make sure the key is below n
            while(BigInteger(private) > Secp256k1CurveConstants.n) {
                secureRandom.nextBytes(private)
            }
            return private
        }
    }
}

