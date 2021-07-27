package network.xyo.client.address

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import network.xyo.client.EcCurve
import java.math.BigInteger
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.*
import java.util.*


open class XyoAddress {
    private var _keyPair: KeyPair

    open val privateKey: String?
        get() {
            return bytesToHex(_keyPair.private.encoded)
        }

    open val publicKey: String
        get() {
            return bytesToHex(_keyPair.public.encoded)
        }

    constructor() {
        _keyPair = generateKeyPair()
    }

    constructor(keyPair: KeyPair)
    {
        _keyPair = keyPair
    }

    constructor(publicKey: ByteArray)
    {
        _keyPair = decodeECKeyPair(publicKey)
    }

    constructor(phrase: String)
    {
        _keyPair = decodeECKeyPair(sha256(phrase))
    }

    open fun sign(hash: String): ByteArray {
        val signature: Signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(_keyPair.private)
        signature.update(hash.toByteArray())
        return signature.sign()
    }

    companion object {
        fun sha256(value: String): ByteArray {
            val md = MessageDigest.getInstance("SHA256")
            md.update(value.encodeToByteArray())
            return md.digest()
        }

        fun sha256String(value: String): String {
            return bytesToHex(sha256(value))
        }

        private val hexArray = "0123456789abcdef".toCharArray()

        fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF

                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }

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