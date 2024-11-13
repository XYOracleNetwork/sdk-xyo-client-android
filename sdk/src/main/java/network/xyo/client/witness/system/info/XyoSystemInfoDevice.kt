package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import com.squareup.moshi.JsonClass
import java.net.URLEncoder

@JsonClass(generateAdapter = true)
class XyoSystemInfoDevice(
    val board: String?,
    val bootloader: String?,
    val brand: String?,
    val device: String?,
    val fingerprint: String?,
    val hardware: String?,
    val host: String?,
    val id: String?,
    val manufacturer: String?,
    val model: String?,
    val product: String?,
    val tags: String?,
    val time: Long?,
    val type: String?,
    val user: String?,
) {
    companion object {
        fun detect(context: Context): XyoSystemInfoDevice {
            return XyoSystemInfoDevice(
                URLEncoder.encode(Build.BOARD, "UTF-8"),
                URLEncoder.encode(Build.BOOTLOADER, "UTF-8"),
                URLEncoder.encode(Build.BRAND, "UTF-8"),
                URLEncoder.encode(Build.DEVICE, "UTF-8"),
                URLEncoder.encode(Build.FINGERPRINT, "UTF-8"),
                URLEncoder.encode(Build.HARDWARE, "UTF-8"),
                URLEncoder.encode(Build.HOST, "UTF-8"),
                URLEncoder.encode(Build.ID, "UTF-8"),
                URLEncoder.encode(Build.MANUFACTURER, "UTF-8"),
                URLEncoder.encode(Build.MODEL, "UTF-8"),
                URLEncoder.encode(Build.PRODUCT, "UTF-8"),
                URLEncoder.encode(Build.TAGS, "UTF-8"),
                Build.TIME,
                URLEncoder.encode(Build.TYPE, "UTF-8"),
                URLEncoder.encode(Build.USER, "UTF-8")
            )
        }
    }
}