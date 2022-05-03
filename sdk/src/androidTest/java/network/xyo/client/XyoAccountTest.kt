package network.xyo.client

import network.xyo.client.address.XyoAccount
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class XyoAccountTest {

    val testVectorPrivate = "7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"
    val testVectorPublic = "ed6f3b86542f45aab88ec48ab1366b462bd993fec83e234054afd8f2311fba774800fdb40c04918463b463a6044b83413a604550bfba8f8911beb65475d6528e"
    val testVectorSignature = "b61dad551e910e2793b4f9f880125b5799086510ce102fad0222c1b093c60a6bc755ca10a9068079ac8d9617416a7cd41077093061c1e9bcb2f81812086ae603"
    val testVectorHash = "4b688df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a"
    val testVectorAddressKeccakHash = "0889fa0b3d5bb98e749c7bf75e7a847447e7fec41011ae7d32d768f86605ba03"
    val testVectorAddress = "5e7a847447e7fec41011ae7d32d768f86605ba03"

    @Test
    fun testAddress() {
        val address = XyoAccount(XyoSerializable.hexToBytes(testVectorPrivate))
        val signature = XyoSerializable.bytesToHex(address.sign(testVectorHash))
        assertEquals(testVectorPrivate, address.privateKeyHex)
        assertEquals(testVectorPublic, address.publicKeyHex)
        assertEquals(testVectorAddressKeccakHash, address.keccakHashHex)
        assertEquals(testVectorAddress, address.addressHex)
        assertEquals(testVectorSignature, signature)
    }

    @Test
    fun testAddressHexRoundTrip() {
        val privateBytes = XyoSerializable.hexToBytes(testVectorPrivate)
        val privateHex = XyoSerializable.bytesToHex(privateBytes)
        assertEquals(testVectorPrivate, privateHex)
    }

    @Test
    fun testInitWithGenerate() {
        val address = XyoAccount()
        val privateHex = address.privateKeyHex
        val publicHex = address.publicKeyHex
        val addressHex = address.addressHex
        assertNotNull(address)
    }
}