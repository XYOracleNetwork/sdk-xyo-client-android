package network.xyo.client.address

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import network.xyo.client.EcCurve
import network.xyo.client.XyoSerializable
import java.math.BigInteger
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.*

open class XyoAddress(var _keyPair: KeyPair = generateKeyPair()) {

    constructor(publicKey: ByteArray): this(decodeECKeyPair(publicKey))

    constructor(phrase: String): this(decodeECKeyPair(XyoSerializable.sha256(phrase)))

    open val privateKey: String?
        get() {
            return XyoSerializable.bytesToHex(_keyPair.private.encoded)
        }

    open val publicKey: String
        get() {
            return XyoSerializable.bytesToHex(_keyPair.public.encoded)
        }

    open fun sign(hash: String): ByteArray {
        val signature: Signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(_keyPair.private)
        signature.update(hash.toByteArray())
        return signature.sign()
    }

    companion object {

        private fun getECParamSpec(): ECParameterSpec {
            val localKeyPair = generateKeyPair()
            return (localKeyPair.public as ECPublicKey).params
        }

        private fun generateKeyPair(): KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    "xyo",
                    KeyProperties.PURPOSE_SIGN
                )
                    .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                    .setDigests(
                        KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512
                    ).build()
            )
            return keyPairGenerator.generateKeyPair()
        }

        private fun decodeECKeyPair(
            key: ByteArray
        ): KeyPair {
            val params = getECParamSpec()
            val privateKeySpec = ECPrivateKeySpec(BigInteger(key), params)
            val keyFactory = KeyFactory.getInstance("EC")
            val privateKey = keyFactory.generatePrivate(privateKeySpec) as ECPrivateKey
            val curve = EcCurve(params)
            val publicKeyPoint = curve.multiply(curve.g, params.cofactor.toBigInteger())
            val publicKeySpec = ECPublicKeySpec(publicKeyPoint, params)
            val publicKey = keyFactory.generatePublic(publicKeySpec)
            return KeyPair(publicKey, privateKey)
        }
    }
}