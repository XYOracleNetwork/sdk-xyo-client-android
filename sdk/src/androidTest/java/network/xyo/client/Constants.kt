package network.xyo.client

import network.xyo.client.address.Account
import network.xyo.payload.Payload

class DebugPayload(schema: String, val nonce: Int) : Payload(schema)


class TestConstants {
    companion object {
        const val accountPrivateKey = "69f0b123c094c34191f22c25426036d6e46d5e1fab0a04a164b3c1c2621152ab"
        val TestAccount = Account(XyoSerializable.hexToBytes(accountPrivateKey))
        val debugPayload = DebugPayload("network.xyo.debug",1)
        const val debugPayloadHash = "15b8d0e30ca5aa96ca6cc9e1528c075aec88cd3f2c3eb0394fde647eb4bf4547"
        const val nodeUrlLocal = "http://10.0.2.2:8080"
        const val nodeUrlBeta = "https://beta.api.archivist.xyo.network"
        const val queryResponseJson = "{\"data\":[{\"addresses\":[\"cddaa9e8922142dfd53e5067f5b9e5de5c2ea0cf\",\"6cfeb02624f01112892e02f18a5e3409ae0a0739\"],\"payload_hashes\":[\"09a4dda042973bcb69f7a7f63a8a79763760e0e2ca9b0d486d7edf72ac2288be\"],\"payload_schemas\":[\"network.xyo.boundwitness\"],\"previous_hashes\":[\"09a4dda042973bcb69f7a7f63a8a79763760e0e2ca9b0d486d7edf72ac2288be\",null],\"schema\":\"network.xyo.boundwitness\",\"timestamp\":1682101895916,\"_signatures\":[\"5c94a98e2da7a4d3750c54c6a3a539900d7722c49419555f0563227876fc7eb3c67718b20bce7ba84913754557eea1a7864a1bc60aa5de5184f1e711a5b54637\",\"02910768ebb01049ccc10e957cbc70c32b71d1891bee66dcb71bedee284f796ca82c791e0efd361d49b34c10afce2aecacf9f27655c3496c91a83f5ad6ed8398\"]},[{\"addresses\":[\"cddaa9e8922142dfd53e5067f5b9e5de5c2ea0cf\"],\"payload_hashes\":[\"a83df3b9d9e92a391d6e172817762a90f1f6ca19da226e7a4679284d6af91f9b\",\"89dafadc4fa4249d73fce1bae21fe14ecff36175564fcbb8dc343982402f4d7e\",\"3cef5677c2f800d6603cebebab17e1cb459e086b81a50fd8d154a5dd5f279d2d\"],\"payload_schemas\":[\"network.xyo.boundwitness\",\"network.xyo.system.info\",\"network.xyo.query.archivist.insert\"],\"previous_hashes\":[null],\"schema\":\"network.xyo.boundwitness\",\"timestamp\":1682101895915,\"_signatures\":[\"5cd4359570fd0273f79a9181c3345aa034481e62c01e6f29e29861eeb167fbf0c2451fc9096c2c9d99b2a5e1ad3e53318fb6a47647fb083842cc82ead6d7c58c\"]}]],\"meta\":{\"profile\":{\"duration\":37,\"endTime\":1682101895919,\"startTime\":1682101895882}}}"
        const val queryResponseBWHash = "85f4d8c614701e8623e844bbdc220570e163b29f91408e5fc676139ca1832b69"
    }
}
