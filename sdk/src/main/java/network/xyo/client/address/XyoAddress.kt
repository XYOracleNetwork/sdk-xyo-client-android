package network.xyo.client.address

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import network.xyo.client.XyoSerializable
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.interfaces.ECPrivateKey
import org.bouncycastle.jce.interfaces.ECPublicKey
import org.bouncycastle.jce.spec.ECPublicKeySpec
import java.security.*
import java.security.spec.ECGenParameterSpec

open class XyoAddress(
    var keyPair: KeyPair? = generateKeyPair(),
    val _allowRecreateKey: Boolean = true
) {
    //we add leading 0 to make sure it is positive
    constructor(privateKey: ECPrivateKey): this(decodeECKeyPair(privateKey))

    //only clone if allowed
    private fun clonePrivateKey(): ByteArray? {
        if(this._allowRecreateKey) {
            return keyPair?.private?.encoded
        }
        return null
    }

    open val privateKey: ByteArray?
        get() {
            return keyPair?.private?.encoded
        }

    open val privateKeyHex: String?
        get() {
            val privateKey = this.privateKey
            return if (privateKey !== null) XyoSerializable.bytesToHex(privateKey) else null
        }

    open val publicKey: ByteArray?
        get() {
            return keyPair?.public?.encoded
        }

    open val publicKeyHex: String?
        get() {
            val publicKey = this.publicKey
            return if (publicKey !== null) XyoSerializable.bytesToHex(publicKey) else null
        }

    open val address: ByteArray?
        get() {
            return keyPair?.public?.encoded
        }

    open val addressHex: String?
        get() {
            val publicKey = this.publicKey
            return if (publicKey != null) XyoSerializable.bytesToHex(publicKey) else null
        }

    /* regenerateKeyIfNeeded is only used to prevent continual reties on regenerate in case
    it fails */
    open fun sign(hash: String, regenerateKeyIfNeeded: Boolean = true): ByteArray? {
        try {
            val signature: Signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(keyPair?.private)
            signature.update(hash.toByteArray())
            return signature.sign()
        } catch (ex: Exception) {
            Log.w("sign", "bad private key?")
            return null
        }
    }

    companion object {

        private fun generateKeyPair(): KeyPair? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore"
                );
                keyPairGenerator.initialize(keyGenParameterSpec())
                keyPairGenerator.generateKeyPair()
            } else {
                null
            }
        }

        fun keyGenParameterSpec(): KeyGenParameterSpec? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyGenParameterSpec.Builder(
                    "xyo",
                    KeyProperties.PURPOSE_SIGN
                ).setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                    .setDigests(
                        KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512
                    ) // Only permit the private key to be used if the user authenticated
                    // within the last five minutes.
                    .setUserAuthenticationRequired(true)
                    .build()
            } else {
                null
            }
        }

        fun publicKeyFromPrivate(privateKey: ECPrivateKey): ECPublicKey {
            val keyFactory = KeyFactory.getInstance("ECDSA", "BC")
            val ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1")

            val Q = ecSpec.g.multiply(privateKey.d)
            val publicDerBytes: ByteArray = Q.getEncoded(false)

            val point = ecSpec.curve.decodePoint(publicDerBytes)
            val pubSpec = ECPublicKeySpec(point, ecSpec)
            return keyFactory.generatePublic(pubSpec) as ECPublicKey
        }

        fun decodeECKeyPair(
            key: ECPrivateKey
        ): KeyPair {
            return KeyPair(publicKeyFromPrivate(key), key)
        }
    }
}