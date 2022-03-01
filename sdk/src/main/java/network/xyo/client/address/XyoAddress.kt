package network.xyo.client.address

import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.client.XyoSerializable
import org.spongycastle.jcajce.provider.digest.Keccak
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.interfaces.ECPrivateKey
import org.spongycastle.jce.interfaces.ECPublicKey
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.jce.spec.ECParameterSpec
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.spongycastle.jce.spec.ECPublicKeySpec
import org.spongycastle.math.ec.ECPoint
import java.math.BigInteger
import java.security.*

@RequiresApi(Build.VERSION_CODES.M)
open class XyoAddress {

    constructor(alias: String = "xyo") {
        this.keyPair = generateKeyPair()
    }

    constructor(bytes: ByteArray) {
        this.keyPair = getKeyPairFromPrivateKey(bytes)
    }

    constructor(keyPair: KeyPair) {
        this.keyPair = keyPair
    }

    val keyPair: KeyPair

    open val privateKey: ECPrivateKey
        get() {
            return keyPair.private as ECPrivateKey
        }

    open val privateKeyBytes: ByteArray
        get() {
            return privateKey.d.toByteArray()
        }

    open val privateKeyHex: String
        get() {
            return privateKey.d.toString(16).padStart(32, '0')
        }

    open val publicKey: ECPublicKey
        get() {
            return keyPair.public as ECPublicKey
        }

    open val publicKeyBytes: ByteArray
        get() {
            val xBytes = publicKey.q.rawXCoord.toBigInteger().toByteArray()
            val yBytes = publicKey.q.rawYCoord.toBigInteger().toByteArray()
            val entireKey = xBytes.plus(yBytes)
            return entireKey.copyOfRange(1, entireKey.size)
        }

    open val publicKeyHex: String
        get() {
            return XyoSerializable.bytesToHex(publicKeyBytes).padStart(64, '0')
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
        val scInit = Security.insertProviderAt(BouncyCastleProvider(), 1)

        private const val provider = "AndroidKeyStore"

        private fun generateKeyPair(): KeyPair {
            val keyGenerator = KeyPairGenerator.getInstance("ECDSA", "SC")
            return keyGenerator.generateKeyPair()
        }

        fun publicKeyFromPrivateKey(privateKey: ECPrivateKey): ECPublicKey {
            val keyFactory = KeyFactory.getInstance("ECDSA", "SC")
            val ecSpec: ECParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1")

            val Q: ECPoint = ecSpec.g.multiply(privateKey.d)
            val publicDerBytes: ByteArray = Q.getEncoded(false)

            val point: ECPoint = ecSpec.curve.decodePoint(publicDerBytes)
            val pubSpec = ECPublicKeySpec(point, ecSpec)
            return keyFactory.generatePublic(pubSpec) as ECPublicKey
        }

        fun getParametersForCurve(curveName: String): ECParameterSpec {
            return ECNamedCurveTable.getParameterSpec(curveName)
        }

        fun loadPrivateKey(bytes: ByteArray): ECPrivateKey {
            val ecParameterSpec = getParametersForCurve("secp256k1")
            val privateKeySpec = ECPrivateKeySpec(BigInteger(bytes), ecParameterSpec)
            val keyFactory = KeyFactory.getInstance("ECDSA", "SC")
            return keyFactory.generatePrivate(privateKeySpec) as ECPrivateKey
        }

        fun getKeyPairFromPrivateKey(
            bytes: ByteArray
        ): KeyPair {
            val privateKey = loadPrivateKey(bytes)
            val publicKey = publicKeyFromPrivateKey(privateKey)
            return KeyPair(publicKey, privateKey)
        }

        fun getKeyPairFromAlias(
            alias: String
        ): KeyPair {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val entry = keyStore.getEntry(alias, null)
            if (entry != null) {
                val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
                val publicKey = keyStore.getCertificate(alias).publicKey
                return KeyPair(publicKey, privateKey)
            } else {
                return generateKeyPair()
            }
        }
    }
}