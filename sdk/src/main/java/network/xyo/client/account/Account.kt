package network.xyo.client.account

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.AccountInstance
import network.xyo.client.account.model.AccountStatic
import network.xyo.client.account.model.PreviousHashStore
import org.spongycastle.jcajce.provider.digest.Keccak
import tech.figure.hdwallet.ec.CurvePoint
import tech.figure.hdwallet.ec.PrivateKey
import tech.figure.hdwallet.ec.PublicKey
import tech.figure.hdwallet.ec.extensions.toBytesPadded
import tech.figure.hdwallet.ec.secp256k1Curve
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
        get() = _privateKey.toPublicKey().key.toBytesPadded(64)

    override fun sign(hash: ByteArray): ByteArray {
        val result = BCECSigner().sign(_privateKey, hash)
        _previousHash = hash
        return result.encodeAsBTC().toByteArray()
    }

    @OptIn(ExperimentalStdlibApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        val recoveredPublicKey = recoverPublicKey(msg, signature)
        val recoveredPublicKeyHex = recoveredPublicKey?.toHexString()
        val recoveredAddress = if (recoveredPublicKey == null) null else publicKeyToAddress(recoveredPublicKey)
        val recoveredAddressHex = recoveredAddress?.toHexString()
        val expectedAddress = address.toHexString()
        val expectedPublicKeyHex = publicKey.toHexString()
        val publicKey = if (recoveredPublicKey == null) null else PublicKey.fromBytes(byteArrayOf(4.toByte()) + recoveredPublicKey)
        return recoveredPublicKey != null && publicKey != null && BCECSigner().verify(publicKey, msg, ECDSASignature.Companion.decode(
            BTCSignature.fromByteArray(signature))) &&
                recoveredAddressHex == expectedAddress
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
            while(BigInteger(private) > secp256k1Curve.n) {
                secureRandom.nextBytes(private)
            }
            return private
        }

        fun publicKeyFromPrivateKey(private: BigInteger): ByteArray {
            return secp256k1Curve.g.mul(private).encoded(false)
        }

        fun recoverPublicKeyFromSignature(signature: ByteArray, msgHash: ByteArray): ByteArray? {
            val signObj = ECDSASignature.Companion.decode(BTCSignature.fromByteArray(signature))
            require(signature.size == 64) { "Signature must be 64 bytes (r, s format)" }

            // Load secp256k1 curve parameters
            val g = secp256k1Curve.g
            val n = secp256k1Curve.n

            // Adjust v to be 0 or 1 for public key recovery
            val recId = 1

            // Calculate the x-coordinate of the R point
            val x = signObj.r.add(n.multiply(BigInteger.valueOf(recId.toLong())))

            // Check if x is valid on the curve
            // if (x >= curve.field.characteristic) return null

            // Create the point R by decompression
            val xBytes = x.toByteArray()
            val xBytesFinal = if (xBytes.size == 33) xBytes.sliceArray(1 until xBytes.size) else xBytes
            val rPoint: CurvePoint = secp256k1Curve.decodePoint(byteArrayOf((2 + recId).toByte()) + xBytesFinal)

            // Calculate e = HASH(message)
            val e = BigInteger(1, msgHash)

            // Calculate r^-1 mod n
            val rInv = signObj.r.modInverse(n)

            // Calculate s * R
            val sR = rPoint.mul(signObj.s)

            // Calculate (-e) * G
            val negE = e.negate().mod(n)
            val negEG = g.mul(negE)

            // Calculate Q = r^-1 * (s * R + (-e) * G)
            val q = sR.add(negEG).mul(rInv).normalize()

            // Convert the recovered public key to uncompressed format
            val encodedPublicKey =  q.encoded(false)
            return encodedPublicKey.sliceArray(1 until encodedPublicKey.size)  // Use `true` for compressed format if desired
        }

        fun bytesToBigInteger(bb: ByteArray): BigInteger {
            return if (bb.isEmpty()) BigInteger.ZERO else BigInteger(1, bb)
        }


        fun extractRSV(signature: ByteArray): Pair<BigInteger, BigInteger> {

            val decodedSignature = ECDSASignature.decode(BTCSignature.fromByteArray(signature))

            return Pair(decodedSignature.r, decodedSignature.s)
        }
    }
}

