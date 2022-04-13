package network.xyo.client.address

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.asn1.x9.X9ECParameters
import org.spongycastle.crypto.params.ECDomainParameters
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

@RequiresApi(Build.VERSION_CODES.M)
open class XyoAddress {

    constructor() {
        val secureRandom = SecureRandom()
        val private = ByteArray(32)
        secureRandom.nextBytes(private)
        //this line is to make sure the key is below n
        if (private[0] > 0x80) private[0] = (private[0] - 0x80).toByte()
        val privateKey = privateKeyFromBigInteger(bytesToBigInteger(private))
        this.keyPair = ECKeyPair(privateKey, publicKeyFromPrivateKey(bytesToBigInteger(private)))
    }

    constructor(private: ByteArray) {
        val privateKey = privateKeyFromBigInteger(bytesToBigInteger(private))
        this.keyPair = ECKeyPair(privateKey, publicKeyFromPrivateKey(bytesToBigInteger(private)))
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
        val signature: Signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(keyPair.private)
        signature.update(hash.toByteArray())
        return signature.sign()
    }

    companion object {
        val scInit = Security.insertProviderAt(BouncyCastleProvider(), 0)

        val params:X9ECParameters = SECNamedCurves.getByName("secp256k1");
        val CURVE = ECDomainParameters(params.curve, params.g, params.n, params.h);
        val CURVE_SPEC = ECParameterSpec(params.curve, params.g, params.n, params.h);

        fun publicKeyFromPrivateKey(private: BigInteger, compressed: Boolean = false): ECPoint {
            val point = CURVE.g.multiply(private)
            return point
        }

        fun bigIntegerToBytes(b: BigInteger?, numBytes: Int): ByteArray? {
            if (b == null) return null
            val bytes = ByteArray(numBytes)
            val biBytes = b.toByteArray()
            val start = if (biBytes.size == numBytes + 1) 1 else 0
            val length = min(biBytes.size, numBytes)
            System.arraycopy(biBytes, start, bytes, numBytes - length, length)
            return bytes
        }

        private fun privateKeyFromBigInteger(private: BigInteger): BCECPrivateKey {
            return ECKeyFactory
                .getInstance(SpongyCastleProvider.instance)
                .generatePrivate(ECPrivateKeySpec(private, CURVE_SPEC)) as BCECPrivateKey
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