package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import com.squareup.moshi.JsonClass

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
    val type: String?,
    val time: Long?,
    val user: String?,
) {
    companion object {
        fun detect(context: Context): XyoSystemInfoDevice {
            return XyoSystemInfoDevice(
                Build.BOARD,
                Build.BOOTLOADER,
                Build.BRAND,
                Build.DEVICE,
                Build.FINGERPRINT,
                Build.HARDWARE,
                Build.HOST,
                Build.ID,
                Build.MANUFACTURER,
                Build.MODEL,
                Build.PRODUCT,
                Build.TAGS,
                Build.TYPE,
                Build.TIME,
                Build.USER
            )
        }
    }
}