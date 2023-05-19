package network.xyo.witness.system.info

import android.content.Context
import org.json.JSONObject

class SystemInfoNetwork (
    val cellular: SystemInfoNetworkCellular?,
    val wifi: SystemInfoNetworkWifi?,
    val wired: SystemInfoNetworkWired?
): JSONObject() {
    companion object {
        fun detect(context: Context): SystemInfoNetwork? {
            val result = SystemInfoNetwork(
                SystemInfoNetworkCellular.detect(context),
                SystemInfoNetworkWifi.detect(context),
                SystemInfoNetworkWired.detect(context)
            )
            if (result.cellular != null || result.wifi != null || result.wired != null) {
                return result
            }
            return null
        }
    }
}