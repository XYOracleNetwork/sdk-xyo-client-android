package network.xyo.client

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import network.xyo.client.address.XyoAddress
import network.xyo.client.payload.XyoPayload
import network.xyo.client.witness.system.info.XyoSystemInfoWitness
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.security.KeyPair

class XyoAddressTest {
    @Test
    fun testAddressCreateFromPhrase() {
        val address = XyoAddress("test")
        assertEquals("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f", address.privateKeyHex, )
        assertEquals("ed6f3b86542f45aab88ec48ab1366b462bd993fec83e234054afd8f2311fba774800fdb40c04918463b463a6044b83413a604550bfba8f8911beb65475d6528e", address.privateKeyHex, )
        assertEquals("5e7a847447e7fec41011ae7d32d768f86605ba03", address.addressHex, )
    }

    @Test
    fun testAddressHexRoundTrip() {
        val privateBytes = XyoSerializable.hexToBytes("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f")
        val privateHex = XyoSerializable.bytesToHex(privateBytes)
        assertEquals("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f", privateHex)
    }

    @Test
    fun testAddressPrivateKeyRoundTrip() {
        val keyPair = XyoAddress.decodeECKeyPair(XyoSerializable.hexToBytes("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"))
        val address = XyoAddress(keyPair)
        assertEquals(keyPair.private.encoded, address.privateKey)
    }

    @Test
    fun testAddressCreateFromPrivateKey() {
        val keyPair = XyoAddress.decodeECKeyPair(XyoSerializable.hexToBytes("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f"))
        val address = XyoAddress(keyPair)
        assertEquals("7f71bc5644f8f521f7e9b73f7a391e82c05432f8a9d36c44d6b1edbf1d8db62f", address.privateKeyHex, )
        assertEquals("ed6f3b86542f45aab88ec48ab1366b462bd993fec83e234054afd8f2311fba774800fdb40c04918463b463a6044b83413a604550bfba8f8911beb65475d6528e", address.privateKeyHex, )
        assertEquals("5e7a847447e7fec41011ae7d32d768f86605ba03", address.addressHex, )
    }
}