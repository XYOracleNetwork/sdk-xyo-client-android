package network.xyo.client.address

import network.xyo.client.XyoSerializable
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger


class ECKeyPair(val private: BCECPrivateKey, val public: ECPoint)

val SECP256K1N = BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16)

open class Account(privateKeyBytes: ByteArray? = null) {

    private val keyPair: KeyPair = KeyPair(privateKeyBytes)

    public var previousHash: String? = null

    val private: PrivateKey
        get() {
            return this.keyPair.private
        }

    val public: PublicKey
        get() {
            return this.keyPair.public
        }

    open val address: AddressValue
        get() {
            return public.address
        }

    fun sign(hash: String): String {
        val signature = this.private.sign(hash)
        this.previousHash = hash
        return XyoSerializable.bytesToHex(signature)
    }
}