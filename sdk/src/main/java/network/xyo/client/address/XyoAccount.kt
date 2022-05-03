package network.xyo.client.address

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.asn1.x9.X9ECParameters
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.spongycastle.jcajce.provider.digest.Keccak
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.jce.spec.ECParameterSpec
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger
import java.security.*
import kotlin.math.min


class ECKeyPair(val private: BCECPrivateKey, val public: ECPoint)

val SECP256K1N = BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16)

@RequiresApi(Build.VERSION_CODES.M)
open class XyoAccount {

    constructor() {
        val secureRandom = SecureRandom()
        val private = ByteArray(32)
        secureRandom.nextBytes(private)
        //this line is to make sure the key is below n
        while(BigInteger(private) > SECP256K1N) {
            secureRandom.nextBytes(private)
        }
        val privateKey = privateKeyFromBigInteger(bytesToBigInteger(private))
        this.keyPair = ECKeyPair(privateKey, publicKeyFromPrivateKey(bytesToBigInteger(private)))
    }

    constructor(privateKeyBytes: ByteArray) {
        val privateKey = privateKeyFromBigInteger(bytesToBigInteger(privateKeyBytes))
        this.keyPair = ECKeyPair(privateKey, publicKeyFromPrivateKey(bytesToBigInteger(privateKeyBytes)))
    }

    val keyPair: ECKeyPair

    open val privateKeyBytes: ByteArray
        get() {
            return keyPair.private.d.toByteArray()
        }

    open val privateKeyHex: String
        get() {
            return XyoSerializable.bytesToHex(privateKeyBytes).padStart(64, '0')
        }

    open val publicKeyBytes: ByteArray
        get() {
            val bytes = keyPair.public.getEncoded(false)
            return bytes.copyOfRange(1, bytes.size)
        }

    open val publicKeyHex: String
        get() {
            return XyoSerializable.bytesToHex(publicKeyBytes).padStart(128, '0')
        }

    open val keccakHash: ByteArray
        get() {
            val keccak = Keccak.Digest256()
            keccak.update(publicKeyBytes)
            return keccak.digest()
        }

    open val keccakHashHex: String
        get() {
            return XyoSerializable.bytesToHex(keccakHash).padStart(32, '0')
        }

    open val address: ByteArray
        get() {
            return keccakHash.copyOfRange(12, keccakHash.size)
        }

    open val addressHex: String
        get() {
            return XyoSerializable.bytesToHex(address).padStart(20, '0')
        }

    open fun sign(hash: String): ByteArray {
        val input = XyoSerializable.hexToBytes(hash)

        val privateKey = this.keyPair.private

        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        val privKeyParams = ECPrivateKeyParameters(privateKey.d, CURVE)
        signer.init(true, privKeyParams)
        val components = signer.generateSignature(input)
        val rArray = components[0].toByteArray()
        val r = copyByteArrayWithLeadingPaddingOrTrim(rArray, 32)
        val sArray = components[1].toByteArray()
        val s = copyByteArrayWithLeadingPaddingOrTrim(sArray, 32)
        return r + s
    }

    companion object {
        val scInit = Security.insertProviderAt(BouncyCastleProvider(), 0)

        val params:X9ECParameters = SECNamedCurves.getByName("secp256k1");
        val CURVE = ECDomainParameters(params.curve, params.g, params.n, params.h);
        val CURVE_SPEC = ECParameterSpec(params.curve, params.g, params.n, params.h);

        fun copyByteArrayWithLeadingPaddingOrTrim(src: ByteArray, size: Int): ByteArray {
            val dest = ByteArray(size)

            var srcStartIndex = 0
            if (src.size > dest.size){
                srcStartIndex = src.size - dest.size
            }

            var destOffest = 0
            if (src.size < dest.size){
                destOffest = dest.size - src.size
            }
            src.copyInto(dest, destOffest, srcStartIndex )

            return dest
        }

        fun publicKeyFromPrivateKey(private: BigInteger): ECPoint {
            return CURVE.g.multiply(private)
        }

        fun signatureToByteArray(r: BigInteger, s: BigInteger, v: Byte = 0.toByte()): ByteArray {
            val result = mergeByteArrays(
                bigIntegerToBytes(r, 32),
                bigIntegerToBytes(s, 32),
                arrayOf(v).toByteArray()
            )
            return result
        }

        fun mergeByteArrays(vararg arrays: ByteArray): ByteArray {
            var count = 0
            for (array in arrays) {
                count += array.size
            }

            // Create new array and copy all array contents
            val mergedArray = ByteArray(count)
            var start = 0
            for (array in arrays) {
                System.arraycopy(array, 0, mergedArray, start, array.size)
                start += array.size
            }
            return mergedArray
        }

        fun bigIntegerToBytes(b: BigInteger, numBytes: Int): ByteArray {
            val bytes = ByteArray(numBytes)
            val biBytes = b.toByteArray()
            val start = if (biBytes.size == numBytes + 1) 1 else 0
            val length = min(biBytes.size, numBytes)
            System.arraycopy(biBytes, start, bytes, numBytes - length, length)
            return bytes
        }

        private fun privateKeyFromBigInteger(private: BigInteger): BCECPrivateKey {
            val keyFactory = KeyFactory.getInstance("EC", SpongyCastleProvider.instance)
            return keyFactory.generatePrivate(ECPrivateKeySpec(private, CURVE_SPEC)) as BCECPrivateKey
        }

        fun bytesToBigInteger(bb: ByteArray): BigInteger {
            return if (bb.isEmpty()) BigInteger.ZERO else BigInteger(1, bb)
        }

        private fun extractPublicKey(ecPublicKey: BCECPublicKey): ECPoint? {
            val publicPointW = ecPublicKey.w
            val x = publicPointW.affineX
            val y = publicPointW.affineY
            return CURVE.curve.createPoint(x, y)
        }
    }
}