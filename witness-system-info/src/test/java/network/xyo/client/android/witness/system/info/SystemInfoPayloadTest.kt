package network.xyo.client.android.witness.system.info

import network.xyo.client.lib.JsonSerializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SystemInfoPayloadTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `serializes and hashes SystemInfoPayload with network details`() {
        val payload = SystemInfoPayload(
            device = SystemInfoDevice(
                board = "goldfish",
                bootloader = "unknown",
                brand = "google",
                device = "emu64a",
                fingerprint = "google/sdk",
                hardware = "ranchu",
                host = "build-host",
                id = "UP1A",
                manufacturer = "Google",
                model = "sdk_gphone64",
                product = "sdk_gphone64_arm64",
                tags = "dev-keys",
                time = 1700000000L,
                type = "userdebug",
                user = "android-build",
            ),
            network = SystemInfoNetwork(
                cellular = SystemInfoNetworkCellular(
                    ip = "10.0.0.2",
                    provider = SystemInfoNetworkCellularProvider("T-Mobile"),
                ),
                wifi = SystemInfoNetworkWifi(
                    ip = "192.168.1.10",
                    mac = "02:00:00:00:00:00",
                    rssi = -55,
                    ssid = "XYO-Net",
                ),
                wired = SystemInfoNetworkWired(ip = "192.168.1.2"),
            ),
            os = SystemInfoOs(
                base_os = "",
                codename = "REL",
                incremental = "12345",
                preview_sdk_int = 0,
                release = "16",
                sdk_int = 36,
                security_patch = "2026-09-01",
            ),
        )

        assertEquals("network.xyo.system.info.android", payload.schema)
        val json = JsonSerializable.toJson(payload)
        assertTrue(json.contains("\"schema\":\"network.xyo.system.info.android\""))
        assertTrue(json.contains("\"ssid\":\"XYO-Net\""))
        assertEquals(64, payload.dataHash().toHexString().length)

        val witness: SystemInfoWitness = XyoSystemInfoWitness()
        assertNotNull(witness.address)
    }
}
