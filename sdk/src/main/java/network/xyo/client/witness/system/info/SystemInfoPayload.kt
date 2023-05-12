package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import network.xyo.payload.Payload
import org.json.JSONObject

class SystemInfoPayload: Payload("network.xyo.system.info") {

        var device: SystemInfoDevice?
            get() {
                return this.getJSONObject("device") as SystemInfoDevice
            }
            set(value) {
                this.put("device", value)
            }

        var network: SystemInfoNetwork?
            get() {
                return this.getJSONObject("network") as SystemInfoNetwork
            }
            set(value) {
                this.put("network", value)
            }

        var os: JSONObject?
            get() {
                return this.getJSONObject("os")
            }
            set(value) {
                this.put("os", value)
            }

        companion object {
            @RequiresApi(Build.VERSION_CODES.M)
            fun detect(context: Context): SystemInfoPayload {
                val payload = SystemInfoPayload()
                payload.device = SystemInfoDevice.detect()
                payload.network = SystemInfoNetwork.detect(context)
                payload.os = SystemInfoOs.detect()
                return payload
            }
        }
    }