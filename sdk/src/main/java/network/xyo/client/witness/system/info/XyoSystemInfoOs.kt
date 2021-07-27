package network.xyo.client.witness.system.info

import android.content.Context
import android.os.Build
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class XyoSystemInfoOs (
    val base_os: String?,
    val codename: String?,
    val incremental: String?,
    val preview_sdk_int: Int?,
    val release: String?,
    val sdk_int: Int?,
    val security_patch: String?
) {
    companion object {
        fun detect(context: Context): XyoSystemInfoOs {
            return XyoSystemInfoOs(
                Build.VERSION.BASE_OS,
                Build.VERSION.CODENAME,
                Build.VERSION.INCREMENTAL,
                Build.VERSION.PREVIEW_SDK_INT,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                Build.VERSION.SECURITY_PATCH
            )
        }
    }
}