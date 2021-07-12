package network.xyo.client.address

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.spec.ECGenParameterSpec


open class XyoAddress {
    private var _keyPair: KeyPair? = null

    open val privateKey: String?
        get() {
            return _keyPair?.private?.toString()
        }

    open val publicKey: String?
        get() {
            return _keyPair?.public?.toString()
        }

    constructor() {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(
                "key1",
                KeyProperties.PURPOSE_SIGN
            )
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(
                    KeyProperties.DIGEST_SHA256,
                    KeyProperties.DIGEST_SHA384,
                    KeyProperties.DIGEST_SHA512
                ).build()
        )
        _keyPair = keyPairGenerator.generateKeyPair()
    }

    constructor(keyPair: KeyPair)
    {
        _keyPair = keyPair
    }

    open fun sign(hash: String): ByteArray {
        val signature: Signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(_keyPair?.private)
        signature.update(hash.toByteArray())
        return signature.sign()
    }
}