package network.xyo.client.address

import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.Provider

class ECKeyFactory {
    companion object {
        private const val ALGORITHM = "EC"
        private const val algorithmAssertionMsg = "Assumed the JRE supports EC key factories"
        @Throws(NoSuchProviderException::class)
        fun getInstance(provider: String?): KeyFactory {
            return try {
                KeyFactory.getInstance(ALGORITHM, provider)
            } catch (ex: NoSuchAlgorithmException) {
                throw AssertionError(algorithmAssertionMsg, ex)
            }
        }

        fun getInstance(provider: Provider?): KeyFactory {
            return try {
                KeyFactory.getInstance(ALGORITHM, provider)
            } catch (ex: NoSuchAlgorithmException) {
                throw AssertionError(algorithmAssertionMsg, ex)
            }
        }
        val instance: KeyFactory = KeyFactory.getInstance(ALGORITHM)
    }
}