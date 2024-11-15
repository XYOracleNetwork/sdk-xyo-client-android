package network.xyo.client.address

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.account.model.AccountInstance
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger

class ECKeyPair(val private: BCECPrivateKey, val public: ECPoint)

val SECP256K1N = BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16)

@RequiresApi(Build.VERSION_CODES.M)
open class XyoAccount(privateKeyBytes: ByteArray? = null): AccountInstance {

    private val keyPair: XyoKeyPair = XyoKeyPair(privateKeyBytes)

    @Deprecated("Use .privateKey instead")
    val private: XyoPrivateKey
        get() {
            return this.keyPair.private
        }

    @Deprecated("Use .publicKey instead")
    val public: XyoPublicKey
        get() {
            return this.keyPair.public
        }
    override val previousHash: ByteArray?
        get() = null
    override val privateKey: ByteArray
        get() = private.bytes
    override val publicKey: ByteArray
        get() = public.bytes

    override fun sign(hash: ByteArray): ByteArray {
        return private.sign(hash)
    }

    override val address: ByteArray
        get() = public.address.bytes

    override fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        return public.verify(msg, signature)
    }
}