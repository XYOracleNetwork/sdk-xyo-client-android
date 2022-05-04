package network.xyo.client.address

import network.xyo.client.XyoSerializable
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger
import java.security.KeyFactory
import java.security.SecureRandom

class XyoPrivateKey(privateBytes: ByteArray?): XyoEllipticKey(32) {
    private val keyPair: ECKeyPair

    init {
        val resolvedBytes = privateBytes ?: generatePrivateKeyBytes()
        val privateKey = privateKeyFromBigInteger(bytesToBigInteger(resolvedBytes))
        keyPair = ECKeyPair(privateKey, publicKeyFromPrivateKey(bytesToBigInteger(resolvedBytes)))
        checkSize()
    }

    val public: XyoPublicKey
        get() {
            return XyoPublicKey(this.keyPair.public)
        }

    override val bytes: ByteArray
        get() {
            return copyByteArrayWithLeadingPaddingOrTrim(keyPair.private.d.toByteArray(), _size)
        }

    fun sign(hash: ByteArray) : ByteArray {
        return sign(XyoSerializable.bytesToHex(hash))
    }

    fun sign(hash: XyoData) : ByteArray {
        return sign(hash.hex)
    }

    fun sign(hash: String): ByteArray {
        val input = XyoSerializable.hexToBytes(hash)

        val privateKey = this.keyPair.private

        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        val privateKeyParams = ECPrivateKeyParameters(privateKey.d, CURVE)
        signer.init(true, privateKeyParams)
        val components = signer.generateSignature(input)
        val rArray = components[0].toByteArray()
        val r = copyByteArrayWithLeadingPaddingOrTrim(rArray, 32)
        val sArray = components[1].toByteArray()
        val s = copyByteArrayWithLeadingPaddingOrTrim(sArray, 32)
        return r + s
    }

    fun verify(msg: ByteArray, signature: ByteArray) : Boolean {
        return public.verify(msg, signature)
    }

    companion object {
        private fun privateKeyFromBigInteger(private: BigInteger): BCECPrivateKey {
            val keyFactory = KeyFactory.getInstance("EC", SpongyCastleProvider.instance)
            return keyFactory.generatePrivate(ECPrivateKeySpec(private, CURVE_SPEC)) as BCECPrivateKey
        }

        private fun generatePrivateKeyBytes(): ByteArray {
            val secureRandom = SecureRandom()
            val private = ByteArray(32)
            secureRandom.nextBytes(private)
            //this line is to make sure the key is below n
            while(BigInteger(private) > SECP256K1N) {
                secureRandom.nextBytes(private)
            }
            return private
        }

        fun publicKeyFromPrivateKey(private: BigInteger): ECPoint {
            return CURVE.g.multiply(private)
        }

        fun bytesToBigInteger(bb: ByteArray): BigInteger {
            return if (bb.isEmpty()) BigInteger.ZERO else BigInteger(1, bb)
        }
    }
}