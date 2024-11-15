package network.xyo.client.account

import org.spongycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security

class SpongyCastleProvider {
    companion object {
        val instance: Provider

        init {
            val p = Security.getProvider("SC")
            instance = p ?: BouncyCastleProvider()
        }
    }
}