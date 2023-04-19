package network.xyo.client

import network.xyo.client.address.XyoAccount
import network.xyo.client.payload.XyoPayload

class DebugPayload(schema: String, val nonce: Int) : XyoPayload(schema) {}

class TestConstants {
    companion object {
        const val accountPrivateKey = "69f0b123c094c34191f22c25426036d6e46d5e1fab0a04a164b3c1c2621152ab"
        val TestAccount = XyoAccount(XyoSerializable.hexToBytes(accountPrivateKey))
        val debugPayload = DebugPayload("network.xyo.debug",1)
        const val debugPayloadHash = "15b8d0e30ca5aa96ca6cc9e1528c075aec88cd3f2c3eb0394fde647eb4bf4547"
        const val nodeUrlLocal = "http://10.0.2.2:8080"
        const val nodeUrlBeta = "https://beta.api.archivist.xyo.network"
    }
}
