package network.xyo.client.address

import android.os.Build
import androidx.annotation.RequiresApi
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger

class ECKeyPair(val private: BCECPrivateKey, val public: ECPoint)

val SECP256K1N = BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16)

@RequiresApi(Build.VERSION_CODES.M)
open class XyoAccount(privateKeyBytes: ByteArray? = null) {

    private val keyPair: XyoKeyPair = XyoKeyPair(privateKeyBytes)

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
}