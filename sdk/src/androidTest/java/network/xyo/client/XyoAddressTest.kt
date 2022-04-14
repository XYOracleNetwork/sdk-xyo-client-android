package network.xyo.client

import network.xyo.client.address.XyoAddress
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class XyoAddressTest {
    @Test
    fun testAddress() {
        val testVectorSignature = "e63caff060241cba8e7f0347ed4629ffdfb1bf3c3484e88a212e4bc2ecf402f209826e2ad75a934e5a74bdc536c83d368826edda201e7def2cc78afd1cc8432000"
        val testVectorHash = "4b688df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a"
        val address = XyoAddress(XyoSerializable.hexToBytes("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"))
        val signature = XyoSerializable.bytesToHex(address.sign(testVectorHash))
        assertEquals(testVectorSignature, signature)
        assertEquals("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f", address.privateKeyHex)
        assertEquals("ed6f3b86542f45aab88ec48ab1366b462bd993fec83e234054afd8f2311fba774800fdb40c04918463b463a6044b83413a604550bfba8f8911beb65475d6528e", address.publicKeyHex)
        assertEquals("5e7a847447e7fec41011ae7d32d768f86605ba03", address.addressHex)
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