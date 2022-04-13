package network.xyo.client

import network.xyo.client.address.XyoAddress
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class XyoAddressTest {
    @Test
    fun testAddress() {
        val address = XyoAddress(XyoSerializable.hexToBytes("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592"))
        assertEquals("5a95531488b4d0d3645aea49678297ae9e2034879ce0389b80eb788e8b533592", address.privateKeyHex)
        assertEquals("81346d7bcaae3281bad166b3fd7e50d94b0a0a62c79926ce803919acc730735a1f3272f0ca7bf5738d651903ad8347d9f617710fd21df6c5cda10cf44c789a33", address.publicKeyHex)
        assertEquals("2a9c73875ce86f38d388a9d17b64f16c00aa5cc258a555e1424df46dd1766d33", address.keccakHashHex)
        assertEquals("7b64f16c00aa5cc258a555e1424df46dd1766d33", address.addressHex)
    }

    @Test
    fun testAddressHexRoundTrip() {
        val privateBytes = XyoSerializable.hexToBytes("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f")
        val privateHex = XyoSerializable.bytesToHex(privateBytes)
        assertEquals("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f", privateHex)
    }

    @Test
    fun testInitWithGenerate() {
        val address = XyoAddress()
        val privateHex = address.privateKeyHex
        val publicHex = address.publicKeyHex
        val addressHex = address.addressHex
        assertNotNull(address)
    }
}