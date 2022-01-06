package network.xyo.client.address

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import network.xyo.client.EcCurve
import network.xyo.client.XyoSerializable
import java.lang.Exception
import java.math.BigInteger
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.*

open class XyoAddress(
    var keyPair: KeyPair = generateKeyPair(),
    val _allowRecreateKey: Boolean = true
) {
    val _encodedPrivateKey: ByteArray? = this.clonePrivateKey()

    //we add leading 0 to make sure it is positive
    constructor(phrase: String): this(decodeECKeyPair(XyoSerializable.sha256(phrase).reversedArray().plus(0).reversedArray()))

    //only clone if allowed
    private fun clonePrivateKey(): ByteArray? {
        if(this._allowRecreateKey) {
            return keyPair.private.encoded
        }
        return null
    }

    open val privateKey: ByteArray
        get() {
            return keyPair.private.encoded
        }

    open val privateKeyHex: String?
        get() {
            return XyoSerializable.bytesToHex(this.privateKey)
        }

    open val publicKey: ByteArray
        get() {
            return keyPair.public.encoded
        }

    open val publicKeyHex: String
        get() {
            return XyoSerializable.bytesToHex(this.publicKey)
        }

    /* regenerateKeyIfNeeded is only used to prevent continual reties on regenerate in case
    it fails */
    open fun sign(hash: String, regenerateKeyIfNeeded: Boolean = true): ByteArray {
        try {
            val signature: Signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(keyPair.private)
            signature.update(hash.toByteArray())
            return signature.sign()
        } catch (ex: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ex is KeyPermanentlyInvalidatedException) {
                    if (regenerateKeyIfNeeded && _encodedPrivateKey != null) {
                        keyPair = decodeECKeyPair(_encodedPrivateKey)
                        return sign(hash, false)
                    }
                }
            }
            throw ex
        }
    }

    companion object {

        private fun getECParamSpec(): ECParameterSpec {
            val localKeyPair = generateKeyPairForSpec()
            return (localKeyPair.public as ECPublicKey).params
        }

        private fun generateKeyPairForSpec(): KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance("EC", "AndroidKeyStore")
            if (Build.VERSION.SDK_INT >= 24) {
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
            }
            return keyPairGenerator.generateKeyPair()
        }

        private fun generateKeyPair(): KeyPair {
            val random = SecureRandom()
            val bytes = ByteArray(32)
            random.nextBytes(bytes)
            return decodeECKeyPair(bytes.reversedArray().plus(0).reversedArray())
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