package network.xyo.client.address

import java.security.*
import java.security.spec.ECGenParameterSpec

class ECKeyPairGenerator {
    companion object {
        val instance: KeyPairGenerator

        const val ALGORITHM = "EC"
        const val CURVE_NAME = "secp256k1"
        private const val algorithmAssertionMsg = "Assumed JRE supports EC key pair generation"
        private const val keySpecAssertionMsg = "Assumed correct key spec statically"
        private val SECP256K1_CURVE = ECGenParameterSpec(CURVE_NAME)
        fun generateKeyPair(): KeyPair {
            return instance.generateKeyPair()
        }

        @Throws(NoSuchProviderException::class)
        private fun getInstance(provider: String?, random: SecureRandom?): KeyPairGenerator {
            return try {
                val gen = KeyPairGenerator.getInstance(ALGORITHM, provider)
                gen.initialize(SECP256K1_CURVE, random)
                gen
            } catch (ex: NoSuchAlgorithmException) {
                throw AssertionError(algorithmAssertionMsg, ex)
            } catch (ex: InvalidAlgorithmParameterException) {
                throw AssertionError(keySpecAssertionMsg, ex)
            }
        }

        private fun getInstance(provider: Provider?, random: SecureRandom?): KeyPairGenerator {
            return try {
                val gen = KeyPairGenerator.getInstance(ALGORITHM, provider)
                gen.initialize(SECP256K1_CURVE, random)
                gen
            } catch (ex: NoSuchAlgorithmException) {
                throw AssertionError(algorithmAssertionMsg, ex)
            } catch (ex: InvalidAlgorithmParameterException) {
                throw AssertionError(keySpecAssertionMsg, ex)
            }
        }

        init {
            try {
                instance = KeyPairGenerator.getInstance(ALGORITHM, "SC")
                instance.initialize(SECP256K1_CURVE)
            } catch (ex: NoSuchAlgorithmException) {
                throw AssertionError(algorithmAssertionMsg, ex)
            } catch (ex: InvalidAlgorithmParameterException) {
                throw AssertionError(keySpecAssertionMsg, ex)
            }
        }
    }
}