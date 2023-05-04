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
open class XyoAccount(privateKeyBytes: ByteArray? = null) {

    private val keyPair: XyoKeyPair = XyoKeyPair(privateKeyBytes)

    public var previousHash: String? = null

    val private: XyoPrivateKey
        get() {
            return this.keyPair.private
        }

    val public: XyoPublicKey
        get() {
            return this.keyPair.public
        }

    open val address: XyoAddressValue
        get() {
            return public.address
        }

    fun sign(hash: String): String {
        val signature = this.private.sign(hash)
        this.previousHash = hash
        return XyoSerializable.bytesToHex(signature)
    }
}