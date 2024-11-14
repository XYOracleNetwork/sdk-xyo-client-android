package network.xyo.client.account

import network.xyo.client.XyoSerializable
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.account.model.AccountStatic
import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.address.XyoData.Companion.copyByteArrayWithLeadingPaddingOrTrim
import network.xyo.client.address.XyoEllipticKey.Companion.CURVE
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.spongycastle.jcajce.provider.digest.Keccak
import tech.figure.hdwallet.ec.PrivateKey
import tech.figure.hdwallet.ec.extensions.toBytesPadded
import tech.figure.hdwallet.ec.secp256k1Curve
import tech.figure.hdwallet.signer.ASN1Signature
import tech.figure.hdwallet.signer.BCECSigner
import tech.figure.hdwallet.signer.BTCSignature
import tech.figure.hdwallet.signer.ECDSASignature
import java.math.BigInteger
import java.security.SecureRandom

open class Account(private val _privateKey: PrivateKey, private var _previousHash: ByteArray? = null): AccountInstance {

    constructor(privateKey: ByteArray, previousHash: ByteArray? = null) : this(PrivateKey.fromBytes(privateKey, secp256k1Curve), previousHash) {}
    constructor(privateKey: BigInteger, previousHash: ByteArray? = null) : this(privateKey.toByteArray(), previousHash) {}

    private val _address = addressFromPublicKey(publicKey)

    final override val address: ByteArray
        get() = _address
    final override val previousHash: ByteArray?
        get() = _previousHash
    final override val privateKey: ByteArray
        get() = _privateKey.key.toBytesPadded(32)
    final override val publicKey: ByteArray
        get() = publicKeyFromPrivateKey(BigInteger(privateKey))

    override fun sign(hash: ByteArray): ByteArray {
        val result = BCECSigner().sign(_privateKey, hash)
        _previousHash = hash
        return result.encodeAsBTC().toByteArray()
    }

    override fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        return BCECSigner().verify(_privateKey.toPublicKey(), msg, ECDSASignature.Companion.decode(
            BTCSignature.fromByteArray(signature)))
    }

    companion object: AccountStatic<AccountInstance> {
        override var previousHashStore: PreviousHashStore? = null

        override fun fromPrivateKey(key: ByteArray): AccountInstance {
            return Account(key)
        }

        override fun random(): AccountInstance {
            return fromPrivateKey(generatePrivateKeyBytes())
        }

        fun addressFromPublicKey(key: ByteArray): ByteArray {
            val publicKeyHash = toKeccak(key.copyOfRange(1, key.size))
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
            while(BigInteger(private) > secp256k1Curve.n) {
                secureRandom.nextBytes(private)
            }
            return private
        }

        fun publicKeyFromPrivateKey(private: BigInteger): ByteArray {
            return secp256k1Curve.g.mul(private).encoded(false)
        }

        fun bytesToBigInteger(bb: ByteArray): BigInteger {
            return if (bb.isEmpty()) BigInteger.ZERO else BigInteger(1, bb)
        }
    }
}

