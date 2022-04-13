package network.xyo.client.address

import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

class SpongyCastleProvider {
    companion object {
        val instance: Provider

        init {
            val p = Security.getProvider("SC")
            instance = p ?: BouncyCastleProvider()
            instance["MessageDigest.ETH-KECCAK-256"] = "org.ethereum.crypto.cryptohash.Keccak256"
            instance["MessageDigest.ETH-KECCAK-512"] = "org.ethereum.crypto.cryptohash.Keccak512"
        }
    }
}